package es.ucm.fdi.educavial;


import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final String BASE_URL =
            "https://en.wikipedia.org/w/api.php?";
    private static final String QUERY_PARAM = "action";
    private static final String TITLES = "titles";
    private static final String PROP = "prop";

    private static final String IIPROP = "iiprop";
    private static final String FORMAT = "format";
    //https://en.wikipedia.org/w/api.php?action=query&titles=File:Spain_traffic_signal_s14a.svg&prop=imageinfo&iiprop=url&format=json

    static String getSignalInfoJson(String signalName){
        HttpURLConnection conn = null;
        InputStream is = null;
        String res = null;
        Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, "query")
                .appendQueryParameter(TITLES, "File"+Uri.decode(":")+"Spain_traffic_signal_"+signalName+".svg")
                .appendQueryParameter(PROP, "imageinfo")
                .appendQueryParameter(IIPROP, "url")
                .appendQueryParameter(FORMAT, "json")
                .build();
        try {
            URL requestURL = new URL(builtURI.toString());
            conn = (HttpURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int response = conn.getResponseCode();
            Log.i(TAG, "The response is: " + response);

            is = conn.getInputStream();
            if(is == null){
                return null;
            }
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            if (builder.length() == 0) {
                return null;
            }
            res = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "The result is:" + res);
            return res;
        }
    }
}
