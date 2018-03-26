package org.hejwo.gobybus.web.comparators;

import org.hejwo.gobybus.commons.domain.LocationData;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class LocationDataDistanceComparator implements Comparator<LocationData> {

    protected static final GpsDistanceComparator distanceComparator = new GpsDistanceComparator();

    @Override
    public int compare(LocationData o1, LocationData o2) {
        Double o1Latitude = o1.getLatitude();
        Double o1Longitude = o1.getLongitude();
        Point2D.Double o1Point = new Point2D.Double(o1Latitude, o1Longitude);

        Double o2Latitude = o2.getLatitude();
        Double o2Longitude = o2.getLongitude();
        Point2D.Double o2Point = new Point2D.Double(o2Latitude, o2Longitude);

        return distanceComparator.compare(o1Point, o2Point);
    }

}
