package org.hejwo.gobybus.busstopcrawler.actors;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.hejwo.gobybus.busstopcrawler.actors.exceptions.BusStopNotFound;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.hejwo.gobybus.busstopcrawler.domain.Timetable;
import org.hejwo.gobybus.busstopcrawler.integration.WarsawBusStopApi;
import org.hejwo.gobybus.busstopcrawler.integration.dto.TimetableDTO;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hejwo.gobybus.busstopcrawler.utils.ClassUtils.getLowerCasedName;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BusStopTimetableDownloadActor extends AbstractBusStopActor<BusStopTimetableDownloadActor.TimetableDownloadMsg> {

    @Autowired
    public BusStopTimetableDownloadActor(WarsawBusStopApi warsawBusStopApi,
                                         AkkaContextHolder akkaContext,
                                         BusStopRepository busStopRepository,
                                         @Value("${openData.warsawApi.key}") String warsawApiKey) {
        super(TimetableDownloadMsg.class, akkaContext, warsawApiKey, warsawBusStopApi, busStopRepository);
    }

    public static TimetableDownloadMsg message(String busStopId, String busStopNr, String line, LocalDate createdAt) {
        return new TimetableDownloadMsg(busStopId, busStopNr, line, createdAt);
    }

    @Override
    protected void onDownloadMessage(TimetableDownloadMsg timetableDownloadMsg) {
        log().info("Invoked: '{}', msg: '{}', instance: '{}'", "BusStopTimetableDownloadActor", timetableDownloadMsg, hashCode());
        String busStopId = timetableDownloadMsg.getBusStopId();
        String busStopNr = timetableDownloadMsg.getBusStopNr();
        String line = timetableDownloadMsg.getLine();
        LocalDate createdAt = timetableDownloadMsg.getCreatedAt();

        List<TimetableDTO> timetableDTOs = warsawBusStopApi.getTimetablesForLineAndBusStop(busStopId, busStopNr, line, warsawApiKey);
        log().info("Downloaded {} timetables for busStop {}-{} : {}", timetableDTOs.size(), busStopId, busStopNr, line);

        if (CollectionUtils.isNotEmpty(timetableDTOs)) {

            BusStop busStop = busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt(busStopId, busStopNr, createdAt)
                    .orElseThrow(() -> new BusStopNotFound(busStopId, busStopNr, createdAt));

            List<Timetable> timeTables = timetableDTOs.stream()
                    .map(timetableDto -> timetableDto.toTimetable(line))
                    .collect(Collectors.toList());

            busStop.setTimetables(timeTables);
            busStopRepository.save(busStop);
            log().info("Saved {} timetables for busStop {}-{} : {} at day " + createdAt, timeTables.size(), busStopId, busStopNr, line);
        } else {
            log().info("No timetables found for busStop {}-{} : {} at day {}", busStopId, busStopNr, line, createdAt);
        }
    }

    @Getter
    @AllArgsConstructor(staticName = "from")
    @ToString
    @EqualsAndHashCode
    public static class TimetableDownloadMsg {
        private String busStopId;
        private String busStopNr;
        private String line;
        private LocalDate createdAt;
    }

    public static String getBeanName() {
        return getLowerCasedName(BusStopTimetableDownloadActor.class);
    }

}
