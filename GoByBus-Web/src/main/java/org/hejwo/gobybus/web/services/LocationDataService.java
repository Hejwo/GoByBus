package org.hejwo.gobybus.web.services;

import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.web.dto.Position;
import org.hejwo.gobybus.web.invocationtimeacpect.LogInvocationTime;
import org.hejwo.gobybus.web.repositories.LocationDataNativeRepository;
import org.hejwo.gobybus.web.repositories.LocationDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

@LogInvocationTime
@Service
public class LocationDataService {

    private final LocationDataRepository locationDataRepository;
    private final LocationDataNativeRepository locationDataNativeRepository;

    @Autowired
    public LocationDataService(LocationDataRepository locationDataRepository,
                               LocationDataNativeRepository locationDataNativeRepository) {
        this.locationDataRepository = locationDataRepository;
        this.locationDataNativeRepository = locationDataNativeRepository;
    }

    public Set<String> findLinesListForDay(LocalDate selectedDate) {
        LocalDateTime dayStart = selectedDate.atTime(0, 0, 0);
        LocalDateTime dayEnd = selectedDate.atTime(23, 59, 59);

        Set<String> linesListForDay = locationDataNativeRepository.findLinesListForRange(dayStart, dayEnd);

        return linesListForDay;
    }

    public Set<String> findDays() {
        Set<LocalDate> availableDates = locationDataNativeRepository.findAvailableDates();
        return availableDates.toSortedSet().map(LocalDate::toString);
    }

    public Set<String> findBrigadesForLine(LocalDate selectedDate, String lineId) {
        LocalDateTime dayStart = selectedDate.atStartOfDay();
        LocalDateTime dayEnd = selectedDate.atStartOfDay().plusDays(1).minusSeconds(1);

        List<LocationData> allLinePointsForDay = locationDataRepository.findByFirstLineAndTimeBetween(lineId, dayStart, dayEnd);

        return allLinePointsForDay.map(LocationData::getBrigade).toSet();
    }

    public List<Position> findPointsForLine(LocalDate selectedDate, String lineId, String brigadeId) {
        LocalDateTime dayStart = selectedDate.atStartOfDay();
        LocalDateTime dayEnd = selectedDate.atStartOfDay().plusDays(1).minusSeconds(1);

        List<LocationData> lines = locationDataRepository.findByFirstLineAndBrigadeAndTimeBetweenOrderByTimeAsc(lineId, brigadeId, dayStart, dayEnd);
        return lines.sorted(Comparator.comparing(LocationData::getTime)).map(Position::from).toList();
    }

    public List<Position> findAllLinePointsForDay(LocalDate localDate, String lineId) {
        LocalDateTime dayStart = localDate.atStartOfDay();
        LocalDateTime dayEnd = localDate.atStartOfDay().plusDays(1).minusSeconds(1);
        List<LocationData> allLinePointsForDay = locationDataRepository.findByFirstLineAndTimeBetweenOrderByLongitudeAscLatitudeAsc(lineId, dayStart, dayEnd);

        return allLinePointsForDay.map(Position::from);
    }

    public List<Position> findAllPointsForDay(LocalDate selectedDate) {
        LocalDateTime dayStart = selectedDate.atStartOfDay();
        LocalDateTime dayEnd = selectedDate.atStartOfDay().plusDays(1).minusSeconds(1);

        List<LocationData> allLines = locationDataRepository.findInRange(dayStart, dayEnd);

        return allLines.map(Position::from);
    }
}
