package rkr.wear.stringblockwatch.block;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.HashSet;

public class SettingsTextActivity extends SettingsItemCommon {
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

            AddEditTextPreference(defaultCategory, "Text", "value");
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, String phoneId, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, phoneId, rowNum, itemNum);
            keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_value");
            preferences.putString(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_value", "Text");

            return keys;
        }
    }
}
