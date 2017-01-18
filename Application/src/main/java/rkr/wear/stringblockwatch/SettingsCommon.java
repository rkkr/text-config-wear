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

import rkr.wear.stringblockwatch.weather.BootService;


public class SettingsCommon extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsCommon";
    private static final String PATH_WITH_FEATURE = "/watch_face_config";

    private GoogleApiClient mGoogleApiClient;
    private static String mPeerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(WatchFaceCompanion.EXTRA_PEER_ID))
            mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

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
            Set<String> keys = prefs.getAll().keySet();
            onSharedPreferenceChanged(prefs, keys);
        }
    };

    @Override
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnected: " + connectionHint);
        }

        if (mPeerId == null) {
            displayNoConnectedDeviceDialog();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionSuspended: " + cause);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionFailed: " + result);
        }
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
        Set<String> keys = new HashSet<>();
        keys.add(key);
        onSharedPreferenceChanged(sharedPreferences, keys);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, Set<String> keys) {
        DataMap config = new DataMap();

        for (String key : keys) {
            if (!sharedPreferences.contains(key)) {
                config.putString(key, null);
                continue;
            }

            Object pref = sharedPreferences.getAll().get(key);
            if (pref instanceof String)
                config.putString(key, sharedPreferences.getString(key, ""));
            else if (pref instanceof Boolean)
                config.putBoolean(key, sharedPreferences.getBoolean(key, false));
            else if (pref instanceof Integer)
                config.putInt(key, sharedPreferences.getInt(key, 0));
            else if (pref instanceof Set) {
                Set<String> temp = sharedPreferences.getStringSet(key, new HashSet<String>());
                config.putStringArray(key, temp.toArray(new String[temp.size()]));
            }
            else
                Log.e(TAG, key + " unsupported type");
        }

        if (config.size() > 0) {
            sendConfigUpdateMessage(config);

            //Something changed in the settings. Ping boot service to check for Weather blocks.
            Intent intent = new Intent("string.block.watch.SETTINGS_CHANGE");
            sendBroadcast(intent);
        }
    }

    private void sendConfigUpdateMessage(DataMap config) {
        if (mPeerId != null) {
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, PATH_WITH_FEATURE, rawData).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
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
