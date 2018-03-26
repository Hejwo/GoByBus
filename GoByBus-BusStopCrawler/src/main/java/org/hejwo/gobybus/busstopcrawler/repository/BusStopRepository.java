package org.hejwo.gobybus.busstopcrawler.repository;

import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BusStopRepository extends MongoRepository<BusStop, String> {

    Optional<BusStop> findOneByBusStopIdAndAndBusStopNrAndCreatedAt(final String busStopId, final String busStopNr, final LocalDate createdAt);

}
