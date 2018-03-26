package org.hejwo.gobybus.busstopcrawler.repository;

import com.google.common.collect.Lists;
import org.hejwo.gobybus.busstopcrawler.BusStopApp;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
    BusStopApp.class, EmbeddedMongoAutoConfiguration.class
})
public class BusStopRepositoryIntegrationTest {

    @Autowired
    private BusStopRepository busStopRepository;

    @Test
    @Ignore
    public void shouldUpdateBusStop() {
        LocalDate now = LocalDate.now();
        BusStop existingBusStop = BusStop.builder()
            .busStopId("1001").busStopNr("01")
            .busStopName("Bus stop 1 - existing")
            .lines(Lists.newArrayList()).timetables(Lists.newArrayList())
            .build();

        busStopRepository.save(existingBusStop);

        Optional<BusStop> foundBusStop = busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("1001", "01", now);

        foundBusStop.ifPresent(busStop -> {
            busStop.getLines().addAll(Lists.newArrayList("N22", "1", "9", "709"));
            busStopRepository.save(busStop);
        });

        List<BusStop> all = busStopRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getBusStopId()).isEqualToIgnoringCase("1001");
        assertThat(all.get(0).getBusStopNr()).isEqualToIgnoringCase("01");
        assertThat(all.get(0).getBusStopName()).isEqualToIgnoringCase("Bus stop 1 - existing");
        assertThat(all.get(0).getLines()).containsExactly("N22", "1", "9", "709");
        // TODO: 13.02.18, phejwowski,
    }
}