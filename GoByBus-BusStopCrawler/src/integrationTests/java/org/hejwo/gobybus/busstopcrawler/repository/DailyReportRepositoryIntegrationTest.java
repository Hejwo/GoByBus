package org.hejwo.gobybus.busstopcrawler.repository;

import org.hejwo.gobybus.busstopcrawler.BusStopApp;
import org.hejwo.gobybus.busstopcrawler.domain.DailyReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        BusStopApp.class, EmbeddedMongoAutoConfiguration.class
})
public class DailyReportRepositoryIntegrationTest {

    @Autowired
    private DailyReportRepository dailyReportRepository;

    @Before
    public void setUp() {
        dailyReportRepository.deleteAll();
    }

    @Test
    public void save_shouldSave_allDailyReports() {
        List<DailyReport> dailyReports = createReportsWithDates(
                LocalDate.of(2018, 4, 14),
                LocalDate.of(2017, 2, 14),
                LocalDate.of(2017, 3, 14)
        );
        List<DailyReport> savedDailyReports = dailyReportRepository.save(dailyReports);

        assertThat(savedDailyReports).hasSize(3);
        assertThat(savedDailyReports).extracting(DailyReport::getLocalDate)
                .containsExactly(LocalDate.of(2018, 4, 14), LocalDate.of(2017, 2, 14), LocalDate.of(2017, 3, 14));
    }

    @Test
    public void findFirstOrderByLocalDate_shouldFindLatestDate() {
        // given
        List<DailyReport> dailyReports = createReportsWithDates(
                LocalDate.of(2018, 4, 14),
                LocalDate.of(2017, 2, 14),
                LocalDate.of(2017, 3, 14)
        );
        dailyReportRepository.save(dailyReports);

        // when
        Optional<DailyReport> latestDailyReport = dailyReportRepository.findFirstByOrderByLocalDateDesc();

        // then
        assertThat(latestDailyReport).isPresent();
        assertThat(latestDailyReport.get().getLocalDate()).isEqualTo(LocalDate.of(2018, 4, 14));
    }

    @Test
    public void findFirstOrderByLocalDate_shouldFindEmpty_whenNoRecords() {
        Optional<DailyReport> latestDailyReport = dailyReportRepository.findFirstByOrderByLocalDateDesc();

        // then
        assertThat(latestDailyReport).isEmpty();
    }

    private List<DailyReport> createReportsWithDates(LocalDate... dates) {
        return Arrays.stream(dates).map(date -> {
            int busStopDownloadCount = (int) (Math.random() * 600 + 1000);
            return DailyReport.from(date, busStopDownloadCount);
        }).collect(Collectors.toList());
    }
}