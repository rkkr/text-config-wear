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

            ListPreference valuePref = AddListPreference("Date value", "value", R.array.date_item);
            final ListPreference formatPref = AddListPreference("Display format", "format", R.array.date_day_display);
            valuePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    switch ((String) newValue) {
                        case "Year (YYYY)":
                        case "Year (YY)":
                        case "Day of Month":
                        case "Day of Year":
                        case "Week of Year":
                            formatPref.setEntries(R.array.date_day_display);
                            formatPref.setEntryValues(R.array.date_day_display);
                            formatPref.setValue("Number");
                            break;
                        case "Month":
                            formatPref.setEntries(R.array.date_month_display);
                            formatPref.setEntryValues(R.array.date_month_display);
                            formatPref.setValue("Number");
                            break;
                        case "Weekday":
                            formatPref.setEntries(R.array.date_weekday_display);
                            formatPref.setEntryValues(R.array.date_weekday_display);
                            formatPref.setValue("Word Full");
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });
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
