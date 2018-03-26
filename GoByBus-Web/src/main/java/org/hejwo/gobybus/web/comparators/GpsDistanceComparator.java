package org.hejwo.gobybus.web.comparators;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class GpsDistanceComparator implements Comparator<Point2D.Double> {

    private Point2D.Double origin = new Point2D.Double(0,0);

    @Override
    public int compare(Point2D.Double o1, Point2D.Double o2) {
        double distance1 = distance(o1.getX(), origin.getY(), o1.getY(), origin.getY(), 0, 0);
        double distance2 = distance(o2.getX(), origin.getY(), o2.getY(), origin.getY(), 0, 0);
        return Double.compare(distance1, distance2);
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
