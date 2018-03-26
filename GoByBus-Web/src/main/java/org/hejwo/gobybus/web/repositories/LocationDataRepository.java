package org.hejwo.gobybus.web.repositories;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.web.invocationtimeacpect.LogInvocationTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@LogInvocationTime
@Repository
public interface LocationDataRepository extends MongoRepository<LocationData, String> {

    //http://stackoverflow.com/questions/10067169/query-with-sort-and-limit-in-spring-repository-interface
    @Query(value = "{time:{ $gt: ?0, $lt: ?1 }}")
    List<LocationData> findInRange(LocalDateTime dayStart, LocalDateTime dayEnd);

    List<LocationData> findByFirstLineAndTimeBetween(String lineId, LocalDateTime dayStart, LocalDateTime dayEnd);

    List<LocationData> findByFirstLineAndBrigadeAndTimeBetweenOrderByTimeAsc(String firstLine, String brigade, LocalDateTime dayStart, LocalDateTime dayEnd);

    List<LocationData> findByFirstLineAndTimeBetweenOrderByLongitudeAscLatitudeAsc(String lineId, LocalDateTime dayStart, LocalDateTime dayEnd);

    Seq<LocationData> findByFirstLine(String line);

}
