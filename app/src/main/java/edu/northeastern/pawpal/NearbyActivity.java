package edu.northeastern.pawpal;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import edu.northeastern.pawpal.utils.GoogleMapsUtils;
import edu.northeastern.pawpal.utils.GooglePlacesUtils;
import edu.northeastern.pawpal.utils.PermissionUtils;

public class NearbyActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        String[] places = getResources().getStringArray(R.array.places_menu_items);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.places_dropdown_item, places);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);

            // request Google Places API
            URL requestURL;
            try {
                requestURL = GooglePlacesUtils.buildURL(getString(R.string.google_places_api_base_url), mLastLocation, selectedItem);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            String res = GooglePlacesUtils.fetchData(requestURL);
            List<HashMap<String, String>> nearbyPlaceList = GooglePlacesUtils.parseData(res);

            // display places in nearbyPlaceList on screen by marker
            for (int i=0; i<nearbyPlaceList.size(); i++) {
                HashMap<String, String> place = nearbyPlaceList.get(i);
                // calculate and add distance into place item
                double currentLat = mLastLocation.getLatitude();
                double currentLng = mLastLocation.getLongitude();
                double placeLat = Double.parseDouble(place.get("lat"));
                double placeLng = Double.parseDouble(place.get("lng"));
                place.put("distance", GoogleMapsUtils.getDistanceInMiles(currentLat, currentLng, placeLat, placeLng));
                LatLng latLng = new LatLng(placeLat, placeLng);
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(place.get("place_name"))
                        .snippet(place.get("distance")+" mi"));
                if (i==0) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }
            }

        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();

        // set info window click listener for the map
        map.setOnInfoWindowClickListener(marker -> {
            // Get the latitude and longitude of the clicked marker
            LatLng position = marker.getPosition();
            String title = marker.getTitle();
            double lat = position.latitude;
            double lng = position.longitude;

            // Create an intent to launch Google Maps
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + title);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Verify that the intent will resolve to an activity
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                mLastLocation = location;
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // move camera to user's current location
            }
        });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
                getLocation(); // get user's current location
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, R.string.my_location_button_clicked_msg, Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        String latLongStr = location.getLatitude() + ", " + location.getLongitude();
        Toast.makeText(this, "Current location:\n" + latLongStr, Toast.LENGTH_LONG).show();
    }

    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}