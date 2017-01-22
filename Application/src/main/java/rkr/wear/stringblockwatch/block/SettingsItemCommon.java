package rkr.wear.stringblockwatch.block;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;

import rkr.wear.stringblockwatch.R;
import rkr.wear.stringblockwatch.common.SettingsCommon;

public class SettingsItemCommon extends SettingsCommon {

    public int mRowId;
    public int mItemId;
    public String mWatchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mRowId = intent.getIntExtra("ROW_ID", 1);
        mItemId = intent.getIntExtra("ITEM_ID", 1);
        mWatchId = intent.getStringExtra("PHONE_ID");
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
            HashSet<String> keys = mSettings.DeleteRowItem(mRowId, mItemId);
            onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), keys);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
