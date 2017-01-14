package rkr.wear.stringblockwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.HashSet;

public class SettingsDateActivity extends SettingsItemCommon {
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

            ListPreference valuePref = AddListPreference(defaultCategory, "Date value", "value", R.array.date_item);
            String dateValue = getPreferenceManager().getSharedPreferences().getString("row_" + rowNum + "_item_" + itemNum + "_value", "Year (YYYY)");
            final ListPreference formatPref = AddListPreference(defaultCategory, "Display format", "format", getResource(dateValue));
            valuePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int resource = getResource((String) newValue);
                    formatPref.setEntries(resource);
                    formatPref.setEntryValues(resource);
                    switch ((String) newValue) {
                        case "Year (YYYY)":
                        case "Year (YY)":
                        case "Day of Month":
                        case "Day of Year":
                        case "Week of Year":
                            formatPref.setValue("Number");
                            break;
                        case "Month":
                            formatPref.setValue("Number");
                            break;
                        case "Weekday":
                            formatPref.setValue("Word Full");
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });
        }

        private int getResource(String dateValue) {
            switch (dateValue) {
                case "Year (YYYY)":
                case "Year (YY)":
                case "Day of Month":
                case "Day of Year":
                case "Week of Year":
                    return R.array.date_day_display;
                case "Month":
                    return R.array.date_month_display;
                case "Weekday":
                    return R.array.date_weekday_display;
            }
            return R.array.date_day_display;
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, rowNum, itemNum);
            keys.add("row_" + rowNum + "_item_" + itemNum + "_value");
            preferences.putString("row_" + rowNum + "_item_" + itemNum + "_value", "Year (YYYY)");
            keys.add("row_" + rowNum + "_item_" + itemNum + "_format");
            preferences.putString("row_" + rowNum + "_item_" + itemNum + "_format", "Number");

            return keys;
        }
    }
}
