package com.geofencing.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class PostGeofenceEventData extends AsyncTask<String, Void, String> { //AsyncTask<Params, Progress, Result>
    //private String userId;
    private String requestId;
    public PostGeofenceEventData(String requestId){
        //this.userId = userId;
        this.requestId = requestId;
    }
    @Override
    protected String doInBackground(String... strings) {
        String connStr="http://10.0.2.2:80/safechild/send_notification_to_parent.php";

        String deviceId = "ash";
        String locationId = "ash";
        String result = "";
        Log.v("GEO_TRIGGER", "Inside do in background");
        try {

            URL url = new URL(connStr);
            Log.v("GEO_TRIGGER", String.valueOf(connStr));
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));

            String data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(deviceId,"UTF-8")
                    +"&&"+ URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(locationId,"UTF-8");

            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();

            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
            String line="";
            while((line = reader.readLine())!=null){
                result+=line;
            }

            reader.close();
            ips.close();
            http.disconnect();

        } catch (MalformedURLException e) {
            Log.v("GEO_TRIGGER", "MALFORMED URL");
        }
        catch (IOException e){
            Log.v("GEO_TRIGGER", e.toString());
        }

        return result;

    }

    @Override
    protected void onPostExecute(String s) {
        Log.v("GEO_TRIGGER", s);
        if(s.equals("Geofencing data saved")){
            //Delete from local database
            Log.v("GEO_TRIGGER", "GEOFENCING NOTIFICATION SENT TO PARENT");
        }
    }
}
