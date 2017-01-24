/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rkr.wear.stringblockwatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ConfigListenerService extends WearableListenerService {
    private static final String TAG = "DigitalListenerService";
    private static final String SETTINGS_PATH = "/watch_face_config";
    public static final String HTTP_PROXY_PATH = "/watch_face_proxy";
    public static final String CHECKSUM_PATH = "/watch_face_checksum";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "onMessageReceived: " + messageEvent);

        switch (messageEvent.getPath()) {
            case SETTINGS_PATH:
                DataMap settingsConfig = DataMap.fromByteArray(messageEvent.getData());
                SaveDataMap(settingsConfig);
                Intent intent = new Intent("text.config.wear.SETTING_CHANGED");
                sendBroadcast(intent);
                return;
            case HTTP_PROXY_PATH:
                String response = DataMap.fromByteArray(messageEvent.getData()).getString("body");
                GetHttpRequestCallback(this.getApplicationContext(), response);
                return;
            case CHECKSUM_PATH:
                long checksumPhone = DataMap.fromByteArray(messageEvent.getData()).getLong("checksum");
                long checksumWatch = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getLong("checksum", 0);
                if (checksumPhone != checksumWatch) {
                    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                            .addApi(Wearable.API)
                            .useDefaultAccount()
                            .build();
                    googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
                    if (googleApiClient.isConnected())
                        Wearable.MessageApi.sendMessage(googleApiClient, messageEvent.getSourceNodeId(), CHECKSUM_PATH, null);
                }
                return;
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        DigitalWatchFaceService.setPhoneNode(peer);
    }

    private void SaveDataMap(DataMap dataMap)
    {
        Log.d(TAG, "Received watch face config message: " + dataMap);

        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
        for (String key : dataMap.keySet())
        {
            Object value = dataMap.get(key);
            if (value == null)
                prefs.remove(key);
            else if (value instanceof String)
                prefs.putString(key, (String)value);
            else if (value instanceof Long)
                prefs.putLong(key, (Long) value);
            else if (value instanceof Integer)
                prefs.putInt(key, (Integer) value);
            else if (value instanceof Boolean)
                prefs.putBoolean(key, (Boolean) value);
            else if (value instanceof String[]) {

                Set<String> temp = new HashSet<String>();
                for (String item : (String[]) value)
                    temp.add(item);
                prefs.putStringSet(key, temp);
            }
            else
                Log.e(TAG, "Unsupported setting type");
        }

        prefs.commit();
    }

    public static void GetHttpRequestCallback(Context context, String contents) {
        Log.d(TAG, contents);

        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        try {
            JSONObject jObject = new JSONObject(contents);

            prefs.putString("weather_description", jObject.getJSONArray("weather").getJSONObject(0).getString("main"));
            prefs.putString("weather_icon", jObject.getJSONArray("weather").getJSONObject(0).getString("icon"));
            prefs.putInt("weather_temp", jObject.getJSONObject("main").getInt("temp"));
            prefs.putInt("weather_pressure", jObject.getJSONObject("main").getInt("pressure"));
            prefs.putInt("weather_humidity", jObject.getJSONObject("main").getInt("humidity"));
            //prefs.putInt("weather_temp_min", jObject.getJSONObject("main").getInt("temp_min"));
            //prefs.putInt("weather_temp_max", jObject.getJSONObject("main").getInt("temp_max"));
            prefs.putFloat("weather_wind_speed", (float)jObject.getJSONObject("wind").getDouble("speed"));
            //prefs.putInt("weather_wind_direction", jObject.getJSONObject("wind").getInt("deg"));
            //prefs.putString("weather_country", jObject.getJSONObject("sys").getString("country"));
            prefs.putLong("weather_sunrise", jObject.getJSONObject("sys").getLong("sunrise"));
            prefs.putLong("weather_sunset", jObject.getJSONObject("sys").getLong("sunset"));
            prefs.putString("weather_city", jObject.getString("name"));
            prefs.putLong("weather_update_time", System.currentTimeMillis());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        prefs.commit();
    }
}
