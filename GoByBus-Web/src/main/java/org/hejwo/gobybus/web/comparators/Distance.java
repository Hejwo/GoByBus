package org.hejwo.gobybus.web.comparators;

public final class Distance {

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public static void main(String[] args) {
        System.out.println(getDistance(48.154563, 17.072561, 48.154564, 17.072562)); //     0.133727653211
        System.out.println(getDistance(48.154563, 17.072561, 48.158800, 17.064064));//   787.656612953
        System.out.println(getDistance(48.148636, 17.107558, 48.208810, 16.372477));// 54992.5682073
    }
}
