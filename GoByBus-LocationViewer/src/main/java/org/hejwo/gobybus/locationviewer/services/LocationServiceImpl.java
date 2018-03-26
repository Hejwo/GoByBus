package org.hejwo.gobybus.locationviewer.services;

import lombok.extern.slf4j.Slf4j;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.locationviewer.repositories.LocationDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.Assert.notNull;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final LocationDataRepository locationDataRepository;

    @Autowired
    public LocationServiceImpl(LocationDataRepository locationDataRepository) {
        this.locationDataRepository = locationDataRepository;
    }

    @Override
    public void saveLocations(List<LocationData> locations) {
        notNull(locations);

        long savedLocations = locations.stream()
                .map(this::saveLocation)
                .filter(saved -> saved)
                .count();

        log.info(String.format("Fetched %s locations, saved %s locations.",
                locations.size(), savedLocations));
    }

    private boolean saveLocation(LocationData locationData) {
        try {
            locationDataRepository.insert(locationData);
            return true;
        } catch(DataIntegrityViolationException ex) {
            return false;
        }
    }
}
