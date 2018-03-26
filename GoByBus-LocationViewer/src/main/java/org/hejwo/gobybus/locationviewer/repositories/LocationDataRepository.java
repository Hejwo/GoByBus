package org.hejwo.gobybus.locationviewer.repositories;

import org.hejwo.gobybus.commons.domain.LocationData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationDataRepository extends MongoRepository<LocationData, String> {
}
