package com.geofencing.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.geofencing.constants.Constants;
import com.geofencing.listeners.BaseListener;

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

public class NetworkPostRequest extends AsyncTask<String, Void, String> {

    private final BaseListener callback;
    private final String url;
    private boolean success = false;
    private Context context;
    private int status;
    private String task;

    public NetworkPostRequest(Context context, String url, BaseListener callback, String task){
        this.context = context;
        this.callback = callback;
        this.url = url;
        this.task = task;
    }

    @Override
    protected String doInBackground(String... strings) {
            String connStr=url;
            String result = "";
            try {
                URL url = new URL(connStr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = "";
                if (task == Constants.SAVE_LAST_KNOWN_LOCATION_TASK){
                    data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(strings[0],"UTF-8") + "&&"
                            + URLEncoder.encode("childFCMToken","UTF-8")+"="+URLEncoder.encode(strings[1],"UTF-8") + "&&"
                            + URLEncoder.encode("lastKnownLat","UTF-8")+"="+URLEncoder.encode(strings[2],"UTF-8") + "&&"
                            + URLEncoder.encode("lastKnownLng","UTF-8")+"="+URLEncoder.encode(strings[3],"UTF-8");
                }
                else if (task == Constants.SIGN_IN_TASK) {
                    data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(strings[0],"UTF-8") + "&&"
                            + URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(strings[1],"UTF-8") + "&&"
                            + URLEncoder.encode("childPhoneNumber","UTF-8")+"="+URLEncoder.encode(strings[2],"UTF-8");
                }
                else if (task == Constants.GET_GEOFENCES_TASK){
                    data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(strings[0],"UTF-8");
                }
                else if (task == Constants.CHILD_UPDATE_FCM_TOKEN_TASK) {
                    Log.v("FCM_CHILD_ID", strings[1]);
                    data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(strings[0],"UTF-8") + "&&" +
                            URLEncoder.encode("fcmToken","UTF-8")+"="+URLEncoder.encode(strings[1],"UTF-8");
                }
                else if (task == Constants.PUSH_NOTIFICATION_TO_PARENT_TASK) {
                    Log.v("FCM_NOTIFICATION_GEOID", strings[1]);
                    Log.v("FCM_NOTIFICATION_USER", strings[0]);
                    data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(strings[0],"UTF-8") + "&&" +
                            URLEncoder.encode("geofenceId","UTF-8")+"="+URLEncoder.encode(strings[1],"UTF-8");
                }

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
                this.status = http.getResponseCode();
                http.disconnect();
            }
            catch (MalformedURLException e) {
                Log.v("JSON_Exception1", e.toString());
                return "Malformed URL";
            }
            catch (IOException e){
                Log.v("JSON_Exception2", e.toString());
                return "IOException";
            }
            success = true;
            return result;
        }

    @Override
    protected void onPostExecute(String json) {
        if(success)
            callback.callback(context, status,json);
            success = false;
        }
    }

