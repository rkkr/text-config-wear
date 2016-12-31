package com.example.android.wearable.watchface;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;

public class SettingsItemCommon extends SettingsCommon {

    public static int rowNum;
    public static int itemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        rowNum = intent.getIntExtra("ROW_ID", 1);
        itemNum = intent.getIntExtra("ITEM_ID", 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_delete) {
            PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
            HashSet<String> keys = Util.DeleteRowItem(getApplicationContext(), rowNum, itemNum);
            onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), keys);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
