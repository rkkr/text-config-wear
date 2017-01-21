package rkr.wear.stringblockwatch.block;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.HashSet;

import rkr.wear.stringblockwatch.R;

public class SettingsTimeActivity extends SettingsItemCommon {
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
            super.onCreate(savedInstanceState, phoneId, rowNum, itemNum);

            ListPreference valuePref = AddListPreference(defaultCategory, "Time value", "value", R.array.time_value);
            final ListPreference formatPref = AddListPreference(defaultCategory, "Display format", "format", R.array.time_display);
            valuePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    switch ((String) newValue) {
                        case "Hour (24H)":
                        case "Hour (12H)":
                            formatPref.setEnabled(true);
                            formatPref.setValue("Number");
                            break;
                        case "Minute":
                        case "Second":
                            formatPref.setEnabled(true);
                            formatPref.setValue("Number with leading zeros");
                            break;
                        case "AM/PM":
                            formatPref.setEnabled(false);
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, String phoneId, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, phoneId, rowNum, itemNum);
            keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_value");
            preferences.putString(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_value", "Hour (24H)");
            keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_format");
            preferences.putString(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_format", "Number");

            return keys;
        }
    }
}
