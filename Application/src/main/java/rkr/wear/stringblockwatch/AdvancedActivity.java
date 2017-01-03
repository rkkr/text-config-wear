package rkr.wear.stringblockwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AdvancedActivity extends SettingsCommon {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();

        registerReceiver(syncSend, new IntentFilter("string.block.watch.FORCE_SYNC"));
        registerReceiver(syncCallback, new IntentFilter("string.block.watch.SYNCED"));
    }

    @Override
    protected void onDestroy () {
        unregisterReceiver(syncSend);
        unregisterReceiver(syncCallback);
        super.onDestroy();
    }

    BroadcastReceiver syncSend = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> keys = prefs.getAll().keySet();
            onSharedPreferenceChanged(prefs, keys);
        }
    };

    BroadcastReceiver syncCallback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Settings synced", Toast.LENGTH_SHORT).show();
        }
    };

    public static class PreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_advanced);

            Preference sync = findPreference("force_sync");
            sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent("string.block.watch.FORCE_SYNC");
                    preference.getContext().sendBroadcast(intent);
                    return true;
                }
            });
        }
    }
}
