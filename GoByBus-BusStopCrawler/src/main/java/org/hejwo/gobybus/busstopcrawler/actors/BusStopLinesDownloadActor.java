package org.hejwo.gobybus.busstopcrawler.actors;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hejwo.gobybus.busstopcrawler.actors.exceptions.BusStopNotFound;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.TimetablesDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.hejwo.gobybus.busstopcrawler.integration.WarsawBusStopApi;
import org.hejwo.gobybus.busstopcrawler.integration.dto.LineDTO;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.hejwo.gobybus.busstopcrawler.utils.ClassUtils.getLowerCasedName;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BusStopLinesDownloadActor extends AbstractBusStopActor<BusStopLinesDownloadActor.BusStopLineDownloadMsg> {

    private final TimetablesDownloadProperties timetablesDownloadProperties;

    @Autowired
    public BusStopLinesDownloadActor(WarsawBusStopApi warsawBusStopApi,
                                     AkkaContextHolder akkaContext,
                                     BusStopRepository busStopRepository,
                                     @Value("${openData.warsawApi.key}") String warsawApiKey) {
        super(BusStopLineDownloadMsg.class, akkaContext, warsawApiKey, warsawBusStopApi, busStopRepository);
        this.timetablesDownloadProperties = akkaContext.getTimetablesDownloadProperties();
    }

    public static BusStopLineDownloadMsg message(String busStopId, String busStopNr, LocalDate createdAt) {
        return new BusStopLineDownloadMsg(busStopId, busStopNr, createdAt);
    }

    @Override
    protected void onDownloadMessage(BusStopLineDownloadMsg busStopLineDownloadMsg) {
        log().info("Invoked: '{}', msg: '{}', instance: '{}'", "BusStopLinesDownloadActor", busStopLineDownloadMsg, hashCode());

        String busStopId = busStopLineDownloadMsg.getBusStopId();
        String busStopNr = busStopLineDownloadMsg.getBusStopNr();
        LocalDate createdAt = busStopLineDownloadMsg.getCreatedAt();

        List<LineDTO> linesForBusStop = warsawBusStopApi.getLinesForBusStop(busStopId, busStopNr, warsawApiKey);
        List<String> lines = linesForBusStop.stream().map(LineDTO::getLine).collect(Collectors.toList());
        log().info("Downloaded {} lines for busStop {}-{}", lines.size(), busStopId, busStopNr);

        if (isNotEmpty(lines)) {
            saveLines(lines, busStopLineDownloadMsg);

            int timetableDownloadsScheduledSize = 0;
            Duration initialDelay = timetablesDownloadProperties.getInitialDelay();
            Duration increment = timetablesDownloadProperties.getDelayForNext();
            for (String line : lines) {
                BusStopTimetableDownloadActor.TimetableDownloadMsg timetableDownloadMsg = BusStopTimetableDownloadActor.message(busStopId, busStopNr, line, createdAt);
                scheduleTimetableDownload(timetableDownloadMsg, initialDelay);
                timetableDownloadsScheduledSize++;

                initialDelay = getIncrementedInitialDelay(initialDelay, increment);
            }
            log().info("Scheduled timetable {} downloads for busStop : {}-{}", timetableDownloadsScheduledSize, busStopId, busStopNr);
        } else {
            log().info("No lines found for busStop : {}-{} ", busStopId, busStopNr);
        }
    }

    private void saveLines(List<String> lines, BusStopLineDownloadMsg busStopsMsg) {
        String busStopId = busStopsMsg.getBusStopId();
        String busStopNr = busStopsMsg.getBusStopNr();
        LocalDate createdAt = busStopsMsg.getCreatedAt();

        BusStop busStop = busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt(
                busStopId,
                busStopNr,
                createdAt)
                .orElseThrow(() -> new BusStopNotFound(busStopId, busStopNr, createdAt));

        busStop.setLines(lines);
        busStopRepository.save(busStop);

        log().info("Saved {} lines of bus stop {}-{}/{}",
                lines.size(), busStopsMsg.busStopId, busStopsMsg.busStopNr, busStopsMsg.createdAt);
    }

    private void scheduleTimetableDownload(BusStopTimetableDownloadActor.TimetableDownloadMsg timetableDownloadMsg, Duration initialDelay) {
        ActorRef busStopTimetableDownloadRef = timetablesDownloadProperties.getRef();
        akkaContext.scheduleOnce(initialDelay, busStopTimetableDownloadRef, timetableDownloadMsg);
    }

    @Getter
    @AllArgsConstructor(staticName = "from")
    @ToString
    @EqualsAndHashCode
    public static class BusStopLineDownloadMsg {

        private final String busStopId;
        private final String busStopNr;
        private final LocalDate createdAt;
    }

    public static String getBeanName() {
        return getLowerCasedName(BusStopLinesDownloadActor.class);
    }

}
