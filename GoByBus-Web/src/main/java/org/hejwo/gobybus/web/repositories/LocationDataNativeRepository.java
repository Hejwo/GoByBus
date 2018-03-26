package org.hejwo.gobybus.web.repositories;

import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.apache.commons.lang3.text.WordUtils;
import org.hejwo.gobybus.commons.Constraints;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class LocationDataNativeRepository {

    private static final String TABLE_LOCATION_DATA_NAME = WordUtils.uncapitalize(LocationData.class.getSimpleName());
    private static final String FIELD_FIRST_LINE_NAME = "firstLine";
    private static final String FIELD_TIME_NAME = "time";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public LocationDataNativeRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Set<String> findLinesListForRange(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        Date dateStart = convertToDate(dateTimeStart);
        Date dateEnd = convertToDate(dateTimeEnd);

        BasicDBObject append = new BasicDBObject("time",
            BasicDBObjectBuilder.start("$gt", dateStart).add("$lt", dateEnd).get());

        List<String> distinct = mongoTemplate
            .getCollection(TABLE_LOCATION_DATA_NAME)
            .distinct(FIELD_FIRST_LINE_NAME, append);

        return HashSet.ofAll(distinct);
    }

    public Set<LocalDate> findAvailableDates() {
        BasicDBObject append = new BasicDBObject("$group",
            BasicDBObjectBuilder.start("_id",
                BasicDBObjectBuilder.start("day",
                    BasicDBObjectBuilder.start("$dateToString",
                        BasicDBObjectBuilder.start("format", "%Y-%m-%d")
                            .append("date", "$time").get()).get()).get()).get()
        );

        AggregationOutput aggregate = mongoTemplate
            .getCollection(TABLE_LOCATION_DATA_NAME)
            .aggregate(Lists.newArrayList(append));

        List<LocalDate> dates = transformDayAggregateToLocalizedStrings(aggregate.results());
        return HashSet.ofAll(dates);
    }

    private List<LocalDate> transformDayAggregateToLocalizedStrings(Iterable<DBObject> results) {
        return StreamSupport.stream(results.spliterator(), false)
            .map(resultObject -> resultObject.get("_id"))
            .map(rawResult -> (DBObject) rawResult)
            .map(dbObj -> dbObj.get("day"))
            .map(Object::toString)
            .map(LocalDate::parse)
            .sorted()
            .collect(Collectors.toList());
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(Constraints.TIME_ZONE).toInstant());
    }

}
