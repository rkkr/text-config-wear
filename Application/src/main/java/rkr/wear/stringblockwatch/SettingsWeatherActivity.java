package rkr.wear.stringblockwatch;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActivityCompat;

import java.util.HashSet;

public class SettingsWeatherActivity extends SettingsItemCommon {

    //private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
    }

    public static class PreferencesFragment extends SettingsItemFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState, rowNum, itemNum);

            //final PreferenceScreen screen = getPreferenceScreen();

            ListPreference valuePref = AddListPreference(defaultCategory, "Weather value", "value", R.array.weather_item);
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, rowNum, itemNum);
            keys.add("row_" + rowNum + "_item_" + itemNum + "_value");
            preferences.putString("row_" + rowNum + "_item_" + itemNum + "_value", "Temperature");

            return keys;
        }
    }
}
