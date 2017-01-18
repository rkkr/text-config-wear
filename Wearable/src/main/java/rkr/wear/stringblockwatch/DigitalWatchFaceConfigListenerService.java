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

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.HashSet;
import java.util.Set;

public class DigitalWatchFaceConfigListenerService extends WearableListenerService {
    private static final String TAG = "DigitalListenerService";
    public static final String SETTINGS_PATH = "/watch_face_config";
    //private static final String WEATHER_PATH = "/watch_face_weather";
    public static final String HTTP_PROXY_PATH = "/watch_face_proxy";

    //private static String phoneNoneId;

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
                String response = new String(messageEvent.getData());
                WeatherService.getHttpRequestCallback(this.getApplicationContext(), response);
                //DataMap weatherConfig = DataMap.fromByteArray(messageEvent.getData());
                //SaveDataMap(weatherConfig);
                return;
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        DigitalWatchFaceService.setPhoneNode(peer);
    }

    /*@Override
    public void onPeerDisconnected(Node peer) {
        phoneNoneId = null;
    }

    public static String getPhoneNodeId() {
        return phoneNoneId;
    };*/

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
}
