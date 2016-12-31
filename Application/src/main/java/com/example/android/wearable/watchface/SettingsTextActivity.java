package com.example.android.wearable.watchface;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.HashSet;

public class SettingsTextActivity extends SettingsItemCommon {
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

            AddEditTextPreference("Text", "text_value");

        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, int rowNum, int itemNum)
        {
            HashSet<String> keys = SettingsItemFragment.SaveDefaultSettings(preferences, rowNum, itemNum);
            keys.add("row_" + rowNum + "_item_" + itemNum + "_text_value");
            preferences.putString("row_" + rowNum + "_item_" + itemNum + "_text_value", "Text");

            return keys;
        }
    }
}
