package edu.northeastern.pawpal.utils;

import static edu.northeastern.pawpal.BuildConfig.MAPS_API_KEY;

import android.location.Location;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class GooglePlacesUtils {

    public static URL buildURL(String baseURLStr, Location location, String keyword) throws MalformedURLException {
        String locationStr = "location=" + location.getLatitude() + "%2C" + location.getLongitude();
        String radiusStr = "&radius=5000";
        String typeStr = "&keyword=" + keyword;
        String keyStr = "&key="+ MAPS_API_KEY;
        String urlString = baseURLStr + locationStr + radiusStr + typeStr + keyStr;
        return new URL(urlString);
    }

    public static String fetchData(URL requestURL) {

        Handler responseHandler = new Handler();
        String response;
        APIThread apiThread = new APIThread(requestURL, responseHandler);
        apiThread.start();

        try {
            apiThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        response = apiThread.getResponse();
        return response;
    }

    private static HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "--NA--";
        String latitude = "";
        String longitude = "";

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private static List<HashMap<String, String>> getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String, String>> placeList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for(int i = 0; i<count;i++)
        {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placeList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placeList;
    }

    public static List<HashMap<String, String>> parseData(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

}
