package edu.northeastern.pawpal;

import java.text.DecimalFormat;


public class GoogleMapsUtils {
    
    public String getDistanceInMiles(double currentLat, double currentLng, double destLat, double destLng) {
        int earthRadius = 6371; // radius of the earth in km
        double dLat = Math.toRadians(destLat - currentLat);
        double dLng = Math.toRadians(destLng - currentLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(currentLat)) * Math.cos(Math.toRadians(destLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c; // distance in km

        double miles = distance / 1.609; // convert km to miles
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(miles);
    }
}
