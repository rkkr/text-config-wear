package com.example.android.wearable.watchface;

import android.os.Bundle;

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
            super.onCreate(savedInstanceState, rowNum, itemNum);

            AddListPreference("Time value", "time_item", R.array.time_item);
        }
    }
}
