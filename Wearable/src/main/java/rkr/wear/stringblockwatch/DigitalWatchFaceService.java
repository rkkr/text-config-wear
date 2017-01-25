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

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import rkr.wear.stringblockwatch.drawable.DrawableScreen;

public class DigitalWatchFaceService extends CanvasWatchFaceService {
    private static final String TAG = "DigitalWatchFaceService";

    private static final long NORMAL_UPDATE_RATE_MS = 1000;
    private static Node phoneNode;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    public static Node getPhoneNode() {
        return phoneNode;
    }

    public static void setPhoneNode(Node node) {
        phoneNode = node;
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
            GoogleApiClient.ConnectionCallbacks,
            LocationListener {

        static final int MSG_UPDATE_TIME = 0;
        private static final String TAG = "WeatherService";
        private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&APPID=%s";
        private static final String WEATHER_KEY = "e4cfc261b89dc243b2856b53cae142eb";

        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        if (Log.isLoggable(TAG, Log.VERBOSE)) {
                            Log.v(TAG, "updating time");
                        }
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs =
                                    NORMAL_UPDATE_RATE_MS - (timeMs % NORMAL_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "text.config.wear.SETTING_CHANGED":
                        updateUiForConfig(true);
                        break;
                    case Intent.ACTION_TIMEZONE_CHANGED:
                    case Intent.ACTION_LOCALE_CHANGED:
                        updateUiForConfig(false);
                        break;
                    case "text.config.wear.WEATHER_UPDATE":
                        UpdateWeather();
                        break;
                    case "text.config.wear.FIT_UPDATE":
                        FitUpdate();
                        break;
                    default:
                        Log.e(TAG, "Unknown event received: " + intent.getAction());
                }
            }
        };

        boolean mRegisteredReceiver = false;
        boolean mLowBitAmbient;
        boolean mIsRound;
        boolean mIsAmbient;
        boolean needWeather;
        boolean needFit;
        String peekCardMode;
        Paint blackPaint;
        DrawableScreen drawableScreen;
        GoogleApiClient mGoogleApiClient;
        long lastRequest = 0;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            updateUiForConfig(false);
            blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addApi(Wearable.API)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.RECORDING_API)
                    //.addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    //.addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                    .useDefaultAccount()
                    .addConnectionCallbacks(this)
                    .build();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
            } else {
                unregisterReceiver();
            }

            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredReceiver) {
                return;
            }
            mRegisteredReceiver = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_LOCALE_CHANGED);
            filter.addAction("text.config.wear.SETTING_CHANGED");
            filter.addAction("text.config.wear.WEATHER_UPDATE");
            filter.addAction("text.config.wear.FIT_UPDATE");
            DigitalWatchFaceService.this.registerReceiver(mReceiver, filter);

            mGoogleApiClient.connect();

            registerWeatherReceiver();
            registerLocationReceiver();
            registerFitReceiver();
        }

        private void registerWeatherReceiver() {
            if (!needWeather)
                return;

            int REPEAT_TIME = 1000 * 60 * 30;

            AlarmManager service = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
            final Intent weatherIntent = new Intent("text.config.wear.WEATHER_UPDATE");
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 1000 * 5, REPEAT_TIME, pending);
        }

        private void registerFitReceiver() {
            if (!needFit)
                return;

            int REPEAT_TIME = 1000 * 60;

            AlarmManager service = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
            final Intent weatherIntent = new Intent("text.config.wear.FIT_UPDATE");
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 1000 * 5, REPEAT_TIME, pending);
        }

        private void unregisterReceiver() {
            if (!mRegisteredReceiver) {
                return;
            }
            mRegisteredReceiver = false;
            DigitalWatchFaceService.this.unregisterReceiver(mReceiver);

            unregisterWeatherReceiver();
            unregisterLocationReceiver();
            unregisterFitReceiver();

            mGoogleApiClient.disconnect();
        }

        private void unregisterWeatherReceiver() {
            AlarmManager service = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
            final Intent weatherIntent = new Intent("text.config.wear.WEATHER_UPDATE");
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            service.cancel(pending);
        }

        private void unregisterFitReceiver() {
            AlarmManager service = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
            final Intent weatherIntent = new Intent("text.config.wear.WEATHER_UPDATE");
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            service.cancel(pending);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            mIsRound = insets.isRound();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mIsAmbient = inAmbientMode;

            invalidate();
            updateTimer();
        }

        @Override
        public void onPeekCardPositionUpdate(Rect rect) {
            super.onPeekCardPositionUpdate(rect);
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            Rect previewCard = getPeekCardPosition();

            if (!previewCard.isEmpty() && peekCardMode.equals("Resize screen")) {
                bounds = new Rect(bounds.left, bounds.top, bounds.right, previewCard.top);
            }
            drawableScreen.Draw(canvas, bounds, mIsRound, mIsAmbient, mLowBitAmbient);
            if (!previewCard.isEmpty() && peekCardMode.equals("Black"))
                canvas.drawRect(previewCard, blackPaint);
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void updateUiForConfig(boolean wake) {
            if (wake) {
                String lockTag = "WatchFaceWakelockTag_" + System.currentTimeMillis();
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                final PowerManager.WakeLock mWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), lockTag);

                mWakeLock.acquire();
                Handler mWakeLockHandler = new Handler();
                mWakeLockHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWakeLock.release();
                    }
                }, 5000);
            }

            drawableScreen = new DrawableScreen(getApplicationContext());

            peekCardMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("peek_mode", "Black");
            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFaceService.this)
                    .setAcceptsTapEvents(false)
                    .setCardPeekMode(peekCardMode.equals("Hidden") ? WatchFaceStyle.PEEK_MODE_NONE : WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setPeekOpacityMode(WatchFaceStyle.PEEK_OPACITY_MODE_OPAQUE)
                    .setShowSystemUiTime(false)
                    .build());

            needWeather = drawableScreen.NeedsWeatherAccess();
            needFit = drawableScreen.NeedsFitAccess();

            //check if weather or fit block is added and request location access
            if ((needWeather || needFit) && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), PermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            invalidate();
            registerLocationReceiver();
            registerFitReceiver();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            registerLocationReceiver();
            registerFitReceiver();

            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                @Override
                public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                    if (!getConnectedNodesResult.getStatus().isSuccess() || getConnectedNodesResult.getNodes().size() == 0) {
                        Log.e(TAG, "Phone is not connected");
                        return;
                    }
                    phoneNode = getConnectedNodesResult.getNodes().get(0);
                }
            });
        }

        private void FitUpdate() {
            if (!needFit)
                return;

            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
                return;

            Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA).setResultCallback(new ResultCallback<DailyTotalResult>() {
            //Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA).setResultCallback(new ResultCallback<DailyTotalResult>() {
                @Override
                public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                    if (!dailyTotalResult.getStatus().isSuccess() || dailyTotalResult.getTotal() == null) {
                        Log.e(TAG, "Fit update error: " + dailyTotalResult.getStatus().getStatusMessage());
                        return;
                    }
                    List<DataPoint> points = dailyTotalResult.getTotal().getDataPoints();
                    float value = 0;
                    if (!points.isEmpty())
                        value = points.get(0).getValue(Field.FIELD_DISTANCE).asFloat();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putFloat("fit_distance", value).apply();
                }
            });
            Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA).setResultCallback(new ResultCallback<DailyTotalResult>() {
                @Override
                public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                    if (!dailyTotalResult.getStatus().isSuccess() || dailyTotalResult.getTotal() == null) {
                        Log.e(TAG, "Fit update error: " + dailyTotalResult.getStatus().getStatusMessage());
                        return;
                    }
                    List<DataPoint> points = dailyTotalResult.getTotal().getDataPoints();
                    int value = 0;
                    if (!points.isEmpty())
                        value = points.get(0).getValue(Field.FIELD_STEPS).asInt();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("fit_steps", value).apply();
                }
            });
            Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED).setResultCallback(new ResultCallback<DailyTotalResult>() {
                @Override
                public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                    if (!dailyTotalResult.getStatus().isSuccess() || dailyTotalResult.getTotal() == null) {
                        Log.e(TAG, "Fit update error: " + dailyTotalResult.getStatus().getStatusMessage());
                        return;
                    }
                    List<DataPoint> points = dailyTotalResult.getTotal().getDataPoints();
                    float value = 0;
                    if (!points.isEmpty())
                        value = points.get(0).getValue(Field.FIELD_CALORIES).asFloat();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putFloat("fit_calories", value).apply();
                }
            });
            Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_ACTIVITY_SEGMENT).setResultCallback(new ResultCallback<DailyTotalResult>() {
                @Override
                public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                    if (!dailyTotalResult.getStatus().isSuccess() || dailyTotalResult.getTotal() == null) {
                        Log.e(TAG, "Fit update error: " + dailyTotalResult.getStatus().getStatusMessage());
                        return;
                    }
                    List<DataPoint> points = dailyTotalResult.getTotal().getDataPoints();
                    int value = 0;
                    if (!points.isEmpty())
                        value = points.get(0).getValue(Field.FIELD_DURATION).asInt();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("fit_activity_time", value).apply();
                }
            });
        }

        private void registerLocationReceiver() {
            if (!needWeather)
                return;

            Log.d(TAG, "Registering for location");
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
                return;

            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                    .setInterval(1000 * 60 * 60)
                    .setFastestInterval(1000 * 60 * 5);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Location permission unavailable");
                return;
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (!status.getStatus().isSuccess()) {
                        Log.e(TAG, "Location request failed: " + status.getStatusMessage());
                    }
                }
            });
        }

        private void unregisterLocationReceiver() {
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
                return;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location update");

            SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            prefs.putFloat("weather_lat", (float) location.getLatitude());
            prefs.putFloat("weather_lon", (float) location.getLongitude());
            prefs.commit();

            if (phoneNode == null)
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (!getConnectedNodesResult.getStatus().isSuccess() || getConnectedNodesResult.getNodes().size() == 0) {
                            Log.e(TAG, "Phone is not connected");
                            return;
                        }
                        phoneNode = getConnectedNodesResult.getNodes().get(0);
                        UpdateWeather();
                    }
                });


            UpdateWeather();
            //Intent intent = new Intent();
            //intent.setAction("rkr.wear.stringblockwatch.WEATHER_UPDATE");
            //sendBroadcast(intent);
        }

        private void UpdateWeather() {
            Log.d(TAG, "Calling weather update");

            if (System.currentTimeMillis() - lastRequest < 1000 * 60) {
                Log.d(TAG, "Not updating weather, timeout not passed");
                return;
            }

            long lastUpdate = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("weather_update_time", 0);
            if (System.currentTimeMillis() - lastUpdate < 1000 * 60 * 10) {
                Log.d(TAG, "Not updating weather, timeout not passed");
                return;
            }

            Node phoneNode = DigitalWatchFaceService.getPhoneNode();
            if (phoneNode == null || !phoneNode.isNearby()) {
                Log.e(TAG, "Phone is not available");
                return;
            }

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Location permission unavailable");
                return;
            }

            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                Log.e(TAG, "google api client connect failed");
                return;
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (!prefs.contains("weather_lat") || !prefs.contains("weather_lon")) {
                Log.e(TAG, "Location is unavailable");
                return;
            }

            //We have everything we need. Request will be made.
            lastRequest = System.currentTimeMillis();

            getWeather(prefs.getFloat("weather_lat", 0), prefs.getFloat("weather_lon", 0));
        }

        private void getWeather(double lat, double lon){
            String url = String.format(Locale.US, WEATHER_URL, lat, lon, WEATHER_KEY);
            getHttpRequest(url);
        }

        private void getHttpRequest(String url) {
            DataMap config = new DataMap();
            config.putString("url", url);

            Node phoneNode = DigitalWatchFaceService.getPhoneNode();
            if (phoneNode == null || !phoneNode.isNearby()) {
                Log.e(TAG, "Phone is not available");
                return;
            }

            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), ConfigListenerService.HTTP_PROXY_PATH, config.toByteArray()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                    Log.d(TAG, "Send message: " + sendMessageResult.getStatus().getStatusMessage());
                }
            });
        }
    }
}
