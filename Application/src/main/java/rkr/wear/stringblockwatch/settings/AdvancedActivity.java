package rkr.wear.stringblockwatch.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import rkr.wear.stringblockwatch.MainActivity;
import rkr.wear.stringblockwatch.R;
import rkr.wear.stringblockwatch.common.SettingsCommon;
import rkr.wear.stringblockwatch.common.SettingsSharedFragment;

public class AdvancedActivity extends SettingsCommon {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesFragment fragment = new PreferencesFragment();
        fragment.mWatchId = mWatchId;

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    public static class PreferencesFragment extends SettingsSharedFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_advanced);

            PreferenceCategory category = (PreferenceCategory) findPreference("advanced_settings");
            AddListPreference(category, "Idle mode text color", mWatchId + "_idle_mode_color", R.array.idle_modes);
            AddListPreference(category, "Peek card style", mWatchId + "_peek_mode", R.array.peek_modes);

            Preference sync = findPreference("force_sync");
            final Context context = getPreferenceScreen().getContext();
            sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    context.registerReceiver(syncCallback, new IntentFilter("string.block.watch.SYNCED"));
                    Intent intent = new Intent("string.block.watch.FORCE_SYNC");
                    preference.getContext().sendBroadcast(intent);
                    return true;
                }
            });
        }

        BroadcastReceiver syncCallback = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(syncCallback);
                Toast.makeText(context, "Settings synced", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
