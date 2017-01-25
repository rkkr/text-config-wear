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

package rkr.wear.stringblockwatch.common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rkr.wear.stringblockwatch.R;


public class SettingsCommon extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsCommon";
    private static final String PATH_WITH_FEATURE = "/watch_face_config";

    public GoogleApiClient mGoogleApiClient;
    public String mWatchId;
    public SettingsManager mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra(WatchFaceCompanion.EXTRA_PEER_ID))
            displayNoConnectedDeviceDialog();
        mWatchId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        mSettings = new SettingsManager(getApplicationContext(), mWatchId);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        registerReceiver(syncSend, new IntentFilter("string.block.watch.FORCE_SYNC"));
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        unregisterReceiver(syncSend);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    BroadcastReceiver syncSend = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> keys = new HashSet<String>();
            for (String key : prefs.getAll().keySet())
                if (key.startsWith(mWatchId + "_"))
                    keys.add(key);

            onSharedPreferenceChanged(prefs, keys);
        }
    };

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mWatchId == null) {
            displayNoConnectedDeviceDialog();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
    }

    private void displayNoConnectedDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String messageText = getResources().getString(R.string.title_no_device_connected);
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(mWatchId + "_checksum"))
            return;
        Set<String> keys = new HashSet<>();
        keys.add(key);
        onSharedPreferenceChanged(sharedPreferences, keys);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, Set<String> keys) {
        DataMap config = new DataMap();
        Map<String, ?> prefs = sharedPreferences.getAll();

        for (String key : keys) {
            if (!key.startsWith(mWatchId + "_")) {
                Log.e(TAG, "Setting not for phone used: " + key);
                continue;
            }

            if (!sharedPreferences.contains(key)) {
                config.putString(key.replaceFirst(mWatchId + "_", ""), null);
                continue;
            }

            Object pref = prefs.get(key);
            key = key.replaceFirst(mWatchId + "_", "");
            if (pref instanceof String)
                config.putString(key, (String) pref);
            else if (pref instanceof Boolean)
                config.putBoolean(key, (Boolean) pref);
            else if (pref instanceof Integer)
                config.putInt(key, (Integer) pref);
            else if (pref instanceof Long)
                config.putLong(key, (Long) pref);
            else if (pref instanceof Set) {
                Set<String> temp = (Set<String>) pref;
                config.putStringArray(key, temp.toArray(new String[temp.size()]));
            }
            else
                Log.e(TAG, key + " unsupported type");
        }

        //Add checksum
        long timeStamp = System.currentTimeMillis();
        config.putLong("checksum", timeStamp);

        if (config.size() > 1) {
            sharedPreferences.edit().putLong(mWatchId + "_checksum", timeStamp).apply();
            sendConfigUpdateMessage(config);

            //Something changed in the settings.
            Intent intent = new Intent("string.block.watch.SETTINGS_CHANGE");
            sendBroadcast(intent);
        }
    }

    private void sendConfigUpdateMessage(DataMap config) {
        if (mWatchId != null) {
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mWatchId, PATH_WITH_FEATURE, rawData).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                    Log.d("Settings", "Send message: " + sendMessageResult.getStatus().getStatusMessage());
                    if (sendMessageResult.getStatus().isSuccess()) {
                        Intent intent = new Intent("string.block.watch.SYNCED");
                        sendBroadcast(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Settings sync failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
