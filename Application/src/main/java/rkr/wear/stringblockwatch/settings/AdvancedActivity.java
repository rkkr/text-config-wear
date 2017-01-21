package rkr.wear.stringblockwatch.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.widget.Toast;

import rkr.wear.stringblockwatch.R;
import rkr.wear.stringblockwatch.common.SettingsCommon;
import rkr.wear.stringblockwatch.common.SettingsSharedFragment;

public class AdvancedActivity extends SettingsCommon {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();

        registerReceiver(syncCallback, new IntentFilter("string.block.watch.SYNCED"));
    }

    @Override
    protected void onDestroy () {
        unregisterReceiver(syncCallback);
        super.onDestroy();
    }

    BroadcastReceiver syncCallback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Settings synced", Toast.LENGTH_SHORT).show();
        }
    };

    public static class PreferencesFragment extends SettingsSharedFragment {

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
