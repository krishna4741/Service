package servicetutorial.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by deepshikha on 24/11/16.
 */

public class GoogleService extends Service implements LocationListener{

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude,longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 1000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    private String lat;
    private String lon;


    public GoogleService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        intent = new Intent(str_receiver);
//        fn_getlocation();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fn_getlocation(){
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable){

        }else {

            if (isNetworkEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){

                        Log.e("latitude",location.getLatitude()+"");
                        Log.e("longitude",location.getLongitude()+"");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }

            }


            if (isGPSEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Log.e("latitude",location.getLatitude()+"");
                        Log.e("longitude",location.getLongitude()+"");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }


        }

    }

    private class TimerTaskToGetLocation extends TimerTask{
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }

    private void fn_update(Location location){

        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        lat=String.valueOf(location.getLatitude());
        lon=String.valueOf(location.getLongitude());

        submitQuery();
        sendBroadcast(intent);
    }
    private void submitQuery() {
        // TODO Auto-generated method stub
        InsertDetailToServer s = new InsertDetailToServer();
        s.execute("HELLO");
    }

    private class InsertDetailToServer extends
            AsyncTask<String, String, String> {

        private void insertEntries() {
            // TODO Auto-generated method stub
            ArrayList<NameValuePair> register = new ArrayList<NameValuePair>();


            // Name,Email,ContactNo,Addr,City,UserName,pwd;
            System.out.println("--------------01");

            // insert into passanger table

            register.add(new BasicNameValuePair("lat", lat));
            register.add(new BasicNameValuePair("lng", lon));
            register.add(new BasicNameValuePair("imei", "1"));


            try {
                HttpPost httppost;
                System.out.println("--------------1");
                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("--------------2");

                httppost = new HttpPost(
                        "http://www.gopajibaba.com/gps/insertlatlng.php");

                System.out.println("--------------3");
                httppost.setEntity(new UrlEncodedFormEntity(register));
                System.out.println("--------------4");
                HttpResponse respnc = httpclient.execute(httppost);
                System.out.println("--------------5");

            } catch (Exception e) {


                // TODO: handle exception
                Log.e("log_tag", "ERROR IN HTTP CON " + e.toString());

            }

        }


        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            insertEntries();
            return null;
        }
    }




}
