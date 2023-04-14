package edu.northeastern.pawpal;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class APIThread extends Thread {

    private final URL requestURL;
    private String response;
    private Handler responseHandler;

    public APIThread(URL requestURL, Handler responseHandler) {
        this.requestURL = requestURL;
        this.response = "";
        this.responseHandler = responseHandler;
    }

    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            conn.connect();

            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);
            response = resp;

            conn.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    public String getResponse() {
        return response;
    }

}
