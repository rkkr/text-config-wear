package rkr.wear.stringblockwatch;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class WeatherService extends BroadcastReceiver {

    private static final String TAG = "WeatherService";
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&APPID=%s";
    private static final String WEATHER_KEY = "e4cfc261b89dc243b2856b53cae142eb";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Calling weather service");

        long lastUpdate = PreferenceManager.getDefaultSharedPreferences(context).getLong("weather_update_time", 0);
        if (System.currentTimeMillis() - lastUpdate < 1000 * 60 * 15) {
            Log.d(TAG, "Not updating weather, timeout not passed");
            return;
        }

        //this.context = context;

        //if (ActivityCompat.checkSelfPermission(screen.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
        //        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission unavailable");
            return;
        }

        //if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        //    Log.e(TAG, "Location is disabled");
        //    return;
        //}

        if (DigitalWatchFaceService.getGoogleApiClient() == null || !DigitalWatchFaceService.getGoogleApiClient().isConnected()) {
            Log.e(TAG, "google api client connect failed");
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(DigitalWatchFaceService.getGoogleApiClient());

        //final Location net_loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            Log.e(TAG, "Location is not available");
            return;
        }

        getWeather(location.getLatitude(), location.getLongitude());

        //String response = getWeather(net_loc.getLatitude(), net_loc.getLongitude());
        //if (response == null) {
        //    Log.d("Weather", "ERROR");
        //    return;
        //}

        //Log.d("Weather", response);
        //DataMap data = parseJson(response);
        //if (data.isEmpty())
        //    return;

        //sendWeatherUpdateMessage(context, data);
    }

    private void getWeather(double lat, double lon){
        String url = String.format(Locale.US, WEATHER_URL, lat, lon, WEATHER_KEY);
        getHttpRequest(url);
    }

    private void getHttpRequest(String url) {
        //PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(DigitalWatchFaceConfigListenerService.HTTP_PROXY_PATH);
        //putDataMapRequest.getDataMap().putString("url", url);
        //PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        //putDataRequest.setUrgent();

        //Wearable.DataApi.putDataItem(googleClient, putDataRequest);

        DataMap config = new DataMap();
        config.putString("url", url);

        Node phoneNode = DigitalWatchFaceService.getPhoneNode();
        if (phoneNode == null || !phoneNode.isNearby()) {
            Log.e(TAG, "Phone is not available");
            return;
        }


        Wearable.MessageApi.sendMessage(DigitalWatchFaceService.getGoogleApiClient(), phoneNode.getId(), DigitalWatchFaceConfigListenerService.HTTP_PROXY_PATH, config.toByteArray()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                Log.d(TAG, "Send message: " + sendMessageResult.getStatus().getStatusMessage());
            }
        });
    }

    public static void getHttpRequestCallback(Context context, String contents) {
        Log.d(TAG, contents);

        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        try {
            JSONObject jObject = new JSONObject(contents);

            prefs.putString("weather_description", jObject.getJSONArray("weather").getJSONObject(0).getString("main"));
            //prefs.putString("weather_icon", jObject.getJSONArray("weather").getJSONObject(0).getString("icon"));
            prefs.putInt("weather_temp", jObject.getJSONObject("main").getInt("temp"));
            //prefs.putInt("weather_pressure", jObject.getJSONObject("main").getInt("pressure"));
            //prefs.putInt("weather_humidity", jObject.getJSONObject("main").getInt("humidity"));
            prefs.putInt("weather_temp_min", jObject.getJSONObject("main").getInt("temp_min"));
            prefs.putInt("weather_temp_max", jObject.getJSONObject("main").getInt("temp_max"));
            //prefs.putDouble("weather_wind_speed", jObject.getJSONObject("wind").getDouble("speed"));
            //prefs.putInt("weather_wind_direction", jObject.getJSONObject("wind").getInt("deg"));
            prefs.putString("weather_country", jObject.getJSONObject("sys").getString("country"));
            //prefs.putLong("weather_sunrise", jObject.getJSONObject("sys").getLong("sunrise"));
            //prefs.putLong("weather_sunset", jObject.getJSONObject("sys").getLong("sunset"));
            prefs.putString("weather_city", jObject.getString("name"));
            prefs.putLong("weather_update_time", System.currentTimeMillis());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        prefs.commit();
    }
}
