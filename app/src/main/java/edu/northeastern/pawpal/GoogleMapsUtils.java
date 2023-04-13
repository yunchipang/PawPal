package edu.northeastern.pawpal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class GoogleMapsUtils {
    private Context context;

    public GoogleMapsUtils(Context context) {
        this.context = context;
    }

    // TODO: method to launch google maps
    public void launchGoogleMaps(String placeId) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=place_id:" + placeId);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    // TODO: method to calculate "xxx miles away" using lat lng
    public double getDistanceInMiles(double currentLat, double currentLng, double destLat, double destLng) {
        int earthRadius = 6371; // radius of the earth in km
        double dLat = Math.toRadians(destLat - currentLat);
        double dLng = Math.toRadians(destLng - currentLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(currentLat)) * Math.cos(Math.toRadians(destLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c; // distance in km

        double miles = distance / 1.609; // convert km to miles
        return miles;
    }
}
