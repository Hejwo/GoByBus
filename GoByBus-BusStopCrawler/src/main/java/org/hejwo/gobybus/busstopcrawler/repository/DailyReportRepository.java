package org.hejwo.gobybus.busstopcrawler.repository;

import org.hejwo.gobybus.busstopcrawler.domain.DailyReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DailyReportRepository extends MongoRepository<DailyReport, String> {

    Optional<DailyReport> findFirstByOrderByLocalDateDesc();
}
