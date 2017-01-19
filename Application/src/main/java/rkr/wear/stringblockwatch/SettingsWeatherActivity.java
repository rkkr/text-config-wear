package rkr.wear.stringblockwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.HashSet;

public class SettingsWeatherActivity extends SettingsItemCommon {

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
            String weatherValue = getPreferenceManager().getSharedPreferences().getString("row_" + rowNum + "_item_" + itemNum + "_value", "Temperature");
            final ListPreference unitsPref = AddListPreference(defaultCategory, "Units", "units", R.array.weather_temp_units);
            unitsPref.setEnabled("Temperature".equals(weatherValue));
            AddSwitchPreference(defaultCategory, "Append units", "show_units");

            valuePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    unitsPref.setEnabled("Temperature".equals(newValue));
                    return true;
                }
            });
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, rowNum, itemNum);
            keys.add("row_" + rowNum + "_item_" + itemNum + "_value");
            preferences.putString("row_" + rowNum + "_item_" + itemNum + "_value", "Temperature");
            keys.add("row_" + rowNum + "_item_" + itemNum + "_units");
            preferences.putBoolean("row_" + rowNum + "_item_" + itemNum + "_show_units", true);

            return keys;
        }
    }
}
