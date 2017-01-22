package rkr.wear.stringblockwatch.block;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.HashSet;

import rkr.wear.stringblockwatch.R;

public class SettingsWeatherActivity extends SettingsItemCommon {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesFragment fragment = new PreferencesFragment();
        fragment.mWatchId = mWatchId;
        fragment.mRowId = mRowId;
        fragment.mItemId = mItemId;

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    public static class PreferencesFragment extends SettingsItemFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Preference tribute = AddPreference(defaultCategory, null);
            tribute.setSummary("Weather information provided by openweathermap.org");

            ListPreference valuePref = AddListPreference(defaultCategory, "Weather value", "value", R.array.weather_item);
            String weatherValue = getPreferenceManager().getSharedPreferences().getString(mWatchId + "_row_" + mRowId + "_item_" + mItemId + "_value", "Temperature");
            final ListPreference unitsPref = AddListPreference(defaultCategory, "Units", "units", getResource(weatherValue));
            unitsPref.setEnabled(enableUnits(weatherValue));
            AddSwitchPreference(defaultCategory, "Append units", "show_units");

            valuePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int resource = getResource((String) newValue);
                    unitsPref.setEnabled(enableUnits((String)newValue));
                    unitsPref.setEntries(resource);
                    unitsPref.setEntryValues(resource);

                    switch ((String) newValue) {
                        case "Temperature":
                            unitsPref.setValue("Celsius");
                            break;
                        case "Condition":
                            unitsPref.setValue("Icon");
                            break;
                        case "Wind Speed":
                            unitsPref.setValue("Meters per second");
                            break;
                        case "Sunrise":
                        case "Sunset":
                            unitsPref.setValue("Time (24H)");
                            break;
                        case "Location":
                            unitsPref.setValue("City");
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });
        }

        private int getResource(String weatherValue) {
            switch (weatherValue) {
                case "Temperature":
                    return R.array.weather_temp_units;
                case "Condition":
                    return R.array.weather_condition_units;
                case "Wind Speed":
                    return R.array.weather_wind_units;
                case "Sunrise":
                case "Sunset":
                    return R.array.weather_sun_units;
                case "Location":
                    return R.array.weather_location_units;
            }
            return R.array.weather_temp_units;
        }

        private boolean enableUnits(String weatherValue) {
            switch (weatherValue) {
                case "Temperature":
                case "Condition":
                case "Wind Speed":
                case "Sunrise":
                case "Sunset":
                case "Location":
                    return true;
            }
            return false;
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, String phoneId, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, phoneId, rowNum, itemNum);
            keys.add("row_" + rowNum + "_item_" + itemNum + "_value");
            preferences.putString("row_" + rowNum + "_item_" + itemNum + "_value", "Temperature");
            keys.add("row_" + rowNum + "_item_" + itemNum + "_units");
            preferences.putBoolean("row_" + rowNum + "_item_" + itemNum + "_show_units", true);

            return keys;
        }
    }
}
