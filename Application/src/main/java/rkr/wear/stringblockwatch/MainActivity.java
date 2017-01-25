package rkr.wear.stringblockwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.wearable.companion.WatchFaceCompanion;
import android.view.View;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashSet;

import rkr.wear.stringblockwatch.common.SettingsCommon;
import rkr.wear.stringblockwatch.common.SettingsSharedFragment;
import rkr.wear.stringblockwatch.row.SettingsRowActivity;
import rkr.wear.stringblockwatch.service.HttpProxy;
import rkr.wear.stringblockwatch.settings.AdvancedActivity;
import rkr.wear.stringblockwatch.settings.ImportActivity;

public class MainActivity extends SettingsCommon {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_fab);

        PreferencesFragment fragment = new PreferencesFragment();
        fragment.mWatchId = mWatchId;

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.settings_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(view.getContext()).unregisterOnSharedPreferenceChangeListener(MainActivity.this);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit();
                int rowNum = mSettings.AddRow();
                HashSet<String> keys = SettingsRowActivity.PreferencesFragment.SaveDefaultSettings(editor, rowNum, mWatchId);
                editor.commit();

                Intent intent = new Intent(view.getContext(), SettingsRowActivity.class);
                intent.putExtra("ROW_ID", rowNum);
                intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                view.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(view.getContext()), keys);
            }
        });

        if (!mSettings.HasSettings()) {
            //We are started for the first time or app settings have been cleared
            ImportActivity.ImportWatch(getApplicationContext().getResources().openRawResource(R.raw.watch_sample1), getApplicationContext(), mWatchId);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        if (mWatchId != null) {
            long checksum = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(mWatchId + "_checksum", 0);
            DataMap data = new DataMap();
            data.putLong("checksum", checksum);
            byte[] rawData = data.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mWatchId, HttpProxy.CHECKSUM_PATH, rawData);
        }
    }

    public static class PreferencesFragment extends SettingsSharedFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_main);

            PreferenceCategory category = (PreferenceCategory) findPreference("general_settings");
            AddListPreference(category, "Wallpaper color", mWatchId + "_wallpaper_color", R.array.font_color, R.array.font_color_value);

            Preference advanced = findPreference("advanced");
            advanced.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getView().getContext(), AdvancedActivity.class);
                    intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                    getView().getContext().startActivity(intent);
                    return true;
                }
            });

            Preference importSample = findPreference("import");
            importSample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getView().getContext(), ImportActivity.class);
                    intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                    getView().getContext().startActivity(intent);
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();

            ArrayList<Integer> rows = mSettings.GetRows();
            PreferenceCategory category = (PreferenceCategory)findPreference("rows");
            category.removeAll();

            for (final Integer row : rows)
            {
                Preference pref = AddPreference(category, String.format("Row %d", rows.indexOf(row) + 1));
                String rowValue = "";
                for (Integer rowItem : mSettings.GetRowItems(row)) {
                    String itemValue = mSettings.GetRowItemValue(row, rowItem);
                    if (itemValue != null)
                        rowValue += "{" + itemValue + "}";
                }
                if (!rowValue.isEmpty())
                    pref.setSummary(rowValue);

                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(getView().getContext(), SettingsRowActivity.class);
                        intent.putExtra("ROW_ID", row);
                        intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                        getView().getContext().startActivity(intent);
                        return true;
                    }
                });
            }
        }
    }
}
