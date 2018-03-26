package org.hejwo.gobybus.busstopcrawler.actors;

import akka.actor.ActorRef;
import lombok.ToString;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopLinesDownloadActor.BusStopLineDownloadMsg;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopLinesDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.hejwo.gobybus.busstopcrawler.domain.DailyReport;
import org.hejwo.gobybus.busstopcrawler.integration.WarsawBusStopApi;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
import org.hejwo.gobybus.busstopcrawler.repository.DailyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hejwo.gobybus.busstopcrawler.utils.ClassUtils.getLowerCasedName;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BusStopsDownloadActor extends AbstractBusStopActor<BusStopsDownloadActor.DownloadBusStopsMsg> {

    private final BusStopLinesDownloadProperties busStopLinesDownloadProperties;
    private final DailyReportRepository dailyReportRepository;
    private final Integer downloadEveryNDays;

    @Autowired
    public BusStopsDownloadActor(WarsawBusStopApi warsawBusStopApi,
                                 AkkaContextHolder akkaContext,
                                 BusStopRepository busStopRepository,
                                 DailyReportRepository dailyReportRepository,
                                 @Value("${openData.warsawApi.key}") String warsawApiKey) {
        super(DownloadBusStopsMsg.class, akkaContext, warsawApiKey, warsawBusStopApi, busStopRepository);
        this.busStopLinesDownloadProperties = akkaContext.getBusStopLinesDownloadProperties();
        this.downloadEveryNDays = akkaContext.getBusStopsDownloadProperties().getDownloadEveryNDays();
        this.dailyReportRepository = dailyReportRepository;
    }

    public static DownloadBusStopsMsg message() {
        return new DownloadBusStopsMsg();
    }

    @Override
    protected void onDownloadMessage(DownloadBusStopsMsg downloadBusStopsMsg) {
        log().info("Invoked: '{}', msg: '{}', instance: '{}'", "BusStopsDownloadActor", downloadBusStopsMsg, hashCode());
        LocalDate today = LocalDate.now();
        if (!busStopsWhereIngestedLessThenNDaysAgo(today, downloadEveryNDays)) {
            List<BusStopDTO> busStopDTOs = warsawBusStopApi.getAllBusStops(warsawApiKey);
            log().info("Downloaded {} bus stops. ", busStopDTOs.size());
            saveBusStops(today, busStopDTOs);
            log().info("Scheduled all line downloads");
        } else {
            log().info("Not scheduling anything - last download was less than {} days ago.", downloadEveryNDays);
        }
    }

    private boolean busStopsWhereIngestedLessThenNDaysAgo(LocalDate today, int numberOfDays) {
        Optional<DailyReport> latestDownloadDate = dailyReportRepository.findFirstByOrderByLocalDateDesc();
        if (latestDownloadDate.isPresent()) {
            long daysSinceLastDownload = DAYS.between(latestDownloadDate.get().getLocalDate(), today);
            log().info("Last download was {} days ago.", daysSinceLastDownload);
            return daysSinceLastDownload < numberOfDays;
        } else {
            return false;
        }
    }

    private void saveBusStops(LocalDate today, List<BusStopDTO> busStopDTOs) {
        List<BusStop> busStops = busStopDTOs.stream()
                .map(dto -> dto.toBusStop(today))
                .collect(Collectors.toList());

        int savedBusStops = 0;
        Duration initialDelay = busStopLinesDownloadProperties.getInitialDelay();
        for (BusStop busStop : busStops) {
            Optional<BusStop> busStopToSave = busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt(
                    busStop.getBusStopId(),
                    busStop.getBusStopNr(),
                    busStop.getCreatedAt());

            if (!busStopToSave.isPresent()) {
                busStopRepository.save(busStop);
                savedBusStops++;

                BusStopLineDownloadMsg linesDownloadMsg = BusStopLinesDownloadActor.message(busStop.getBusStopId(), busStop.getBusStopNr(), today);
                scheduleBusStopLinesDownLoad(linesDownloadMsg, initialDelay);
                initialDelay = getIncrementedInitialDelay(initialDelay, busStopLinesDownloadProperties.getDelayForNext());
            }
        }
        saveDailyReport(DailyReport.from(today, savedBusStops));

        log().info("Saved {}  new busStops for a date: '{}'", savedBusStops, today);
    }

    private void saveDailyReport(DailyReport dailyReport) {
        dailyReportRepository.save(dailyReport);
        log().info("Saved daily report for day '{}'", dailyReport.getLocalDate());
    }

    public static String getBeanName() {
        return getLowerCasedName(BusStopsDownloadActor.class);
    }

    private void scheduleBusStopLinesDownLoad(BusStopLineDownloadMsg linesDownloadMsg, Duration initialDelay) {
        ActorRef busStopLinesDownloadRef = busStopLinesDownloadProperties.getRef();
        akkaContext.scheduleOnce(initialDelay, busStopLinesDownloadRef, linesDownloadMsg);
    }

    //*** Messages ===============================
    @ToString
    public static class DownloadBusStopsMsg {
    }
}
