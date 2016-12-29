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

package com.example.android.wearable.watchface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DigitalWatchFaceConfigListenerService extends WearableListenerService {
    private static final String TAG = "DigitalListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "onMessageReceived: " + messageEvent);

        if (!messageEvent.getPath().equals(DigitalWatchFaceUtil.PATH_WITH_FEATURE)) {
            return;
        }
        byte[] rawData = messageEvent.getData();

        DataMap configKeysToOverwrite = DataMap.fromByteArray(rawData);
        Log.d(TAG, "Received watch face config message: " + configKeysToOverwrite);

        SaveDataMap(configKeysToOverwrite);
        Intent intent = new Intent("text.config.wear.SETTING_CHANGED");
        sendBroadcast(intent);
    }

    private void SaveDataMap(DataMap dataMap)
    {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
        for (String key : dataMap.keySet())
        {
            Object value = dataMap.get(key);
            if (value instanceof String)
                prefs.putString(key, (String)value);
            else
                Log.e(TAG, "Unsupported setting type");
        }

        prefs.commit();
    }
}
