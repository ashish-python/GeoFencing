package com.geofencing.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.geofencing.interfaces.GeofenceInterface;

import org.json.JSONException;

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

public class NetworkGetRequest extends AsyncTask<String, Void, String> {

    private GeofenceInterface callback;
    private String task;
    private boolean success = false;

    public NetworkGetRequest(GeofenceInterface listener, String task){
        this.callback = listener;
        this.task = task;
    }

    @Override
    protected String doInBackground(String... strings) {
            String connStr="http://10.0.2.2:80/safechild/get_data.php";
            String result = "";
            try {
                URL url = new URL(connStr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));

                String data = URLEncoder.encode("task","UTF-8")+"="+URLEncoder.encode(task,"UTF-8");

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
            }
            catch (MalformedURLException e) {
                Log.v("JSON_Exception", e.toString());
                return "Malformed URL";
            }
            catch (IOException e){
                Log.v("JSON_Exception", e.toString());
                return "IOException";
            }
            success = true;
            return result;
        }

    @Override
    protected void onPostExecute(String json) {
        if(success){
            try {
                callback.addGeofences(json);
            }
            catch (JSONException e){

            }
            success = false;
        }
    }
}
