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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import java.util.HashSet;

public class DigitalWatchFaceService extends CanvasWatchFaceService {
    private static final String TAG = "DigitalWatchFaceService";

    private static final long NORMAL_UPDATE_RATE_MS = 1000;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        static final int MSG_UPDATE_TIME = 0;
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
                if (intent.getAction().equals("text.config.wear.SETTING_CHANGED"))
                    updateUiForConfig();
                else {
                    invalidate();
                }
            }
        };

        boolean mRegisteredReceiver = false;
        boolean mLowBitAmbient;
        boolean mIsRound;
        boolean mIsAmbient;
        String peekCardMode;
        Paint blackPaint;
        DrawableScreen drawableScreen;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            peekCardMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("peek_mode", "Black");

            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFaceService.this)
                    .setAcceptsTapEvents(false)
                    .setCardPeekMode(peekCardMode.equals("Hidden") ? WatchFaceStyle.PEEK_MODE_NONE : WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setPeekOpacityMode(WatchFaceStyle.PEEK_OPACITY_MODE_OPAQUE)
                    .setShowSystemUiTime(false)
                    .build());

            drawableScreen = new DrawableScreen(getApplicationContext());
            blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);
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
            DigitalWatchFaceService.this.registerReceiver(mReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredReceiver) {
                return;
            }
            mRegisteredReceiver = false;
            DigitalWatchFaceService.this.unregisterReceiver(mReceiver);
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

        private void updateUiForConfig() {
            drawableScreen = new DrawableScreen(getApplicationContext());

            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            final PowerManager.WakeLock mWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "WatchFaceWakelockTag");

            mWakeLock.acquire();
            Handler mWakeLockHandler = new Handler();
            mWakeLockHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWakeLock.release();
                }
            }, 5000);

            peekCardMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("peek_mode", "Black");
            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFaceService.this)
                    .setAcceptsTapEvents(false)
                    .setCardPeekMode(peekCardMode.equals("Hidden") ? WatchFaceStyle.PEEK_MODE_NONE : WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setPeekOpacityMode(WatchFaceStyle.PEEK_OPACITY_MODE_OPAQUE)
                    .setShowSystemUiTime(false)
                    .build());

            invalidate();
        }
    }
}
