package org.hejwo.gobybus.locationviewer.services;


import org.hejwo.gobybus.commons.domain.LocationData;

import java.util.List;

public interface LocationService {

    void saveLocations(List<LocationData> locations);

}
