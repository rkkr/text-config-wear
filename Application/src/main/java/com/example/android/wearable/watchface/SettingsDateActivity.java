package com.example.android.wearable.watchface;

import android.content.Intent;
import android.os.Bundle;

public class SettingsDateActivity extends SettingsCommon {

    private static int rowNum;
    private static int itemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        rowNum = intent.getIntExtra("ROW_ID", 1);
        itemNum = intent.getIntExtra("ITEM_ID", 1);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
    }

    public static class PreferencesFragment extends SettingsItemFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState, rowNum, itemNum);

            AddListPreference("Date value", "item", R.array.date_item);
        }
    }
}
