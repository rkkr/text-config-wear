package rkr.wear.stringblockwatch.block;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;

import java.util.HashSet;

import rkr.wear.stringblockwatch.R;

public class SettingsFitActivity extends SettingsItemCommon {
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

            AddListPreference(defaultCategory, "Fit value", "value", R.array.fit_value);
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, String phoneId, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, phoneId, rowNum, itemNum);
            keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_value");
            preferences.putString(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_value", "Steps");

            return keys;
        }
    }
}
