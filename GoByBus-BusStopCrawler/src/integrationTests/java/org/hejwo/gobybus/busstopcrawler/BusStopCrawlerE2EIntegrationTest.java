package org.hejwo.gobybus.busstopcrawler;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.Lists;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
    BusStopApp.class, EmbeddedMongoAutoConfiguration.class
})
@Ignore
// TODO: 13.02.18, phejwowski,
public class BusStopCrawlerE2EIntegrationTest {

    private static final int TOTAL_PIPELINE_TIME = 7000;

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(options()
        .port(1111)
        .usingFilesUnderClasspath("src/integrationTests/resources"));

    @Autowired
    private BusStopApp busStopApp;
    @Autowired
    private BusStopRepository busStopRepository;

    @Before
    public void setUp() {
        busStopRepository.deleteAll();
    }

    @Test
    public void shouldDownloadBusStops_whenNoData() throws InterruptedException {
        Thread.sleep(TOTAL_PIPELINE_TIME);

        List<BusStop> busStops = busStopRepository.findAll();

        assertThat(busStops).hasSize(0);
    }

    @Test
    public void shouldDownloadBusStops_whenExistingData() throws InterruptedException {
        // given
        BusStop existingBusStop = BusStop.from("1001", "01", "Old entity", null, null, null, null, null, LocalDate.now(), Lists.newArrayList(), Lists.newArrayList());
        busStopRepository.save(existingBusStop);

        // when
        Thread.sleep(TOTAL_PIPELINE_TIME);

        // then
        List<BusStop> busStops = busStopRepository.findAll();

        assertThat(busStops).hasSize(1);
        assertThat(busStops.get(0)).matches(busStop -> busStop.getBusStopName().equals("Old entity"));
        assertThat(busStops.get(0).getLines()).hasSize(0);
    }

}
