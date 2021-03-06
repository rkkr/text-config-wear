package rkr.wear.stringblockwatch.row;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import rkr.wear.stringblockwatch.R;
import rkr.wear.stringblockwatch.block.SettingsFitActivity;
import rkr.wear.stringblockwatch.common.SettingsCommon;
import rkr.wear.stringblockwatch.block.SettingsDateActivity;
import rkr.wear.stringblockwatch.common.SettingsSharedFragment;
import rkr.wear.stringblockwatch.block.SettingsTextActivity;
import rkr.wear.stringblockwatch.block.SettingsTimeActivity;
import rkr.wear.stringblockwatch.block.SettingsWeatherActivity;
import rkr.wear.stringblockwatch.common.SettingsManager;

public class SettingsRowActivity extends SettingsCommon {

    private int mRowId;
    private Boolean fabExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mRowId = intent.getIntExtra("ROW_ID", 1);
        setContentView(R.layout.settings_fab);

        PreferencesFragment fragment = new PreferencesFragment();
        fragment.mWatchId = mWatchId;
        fragment.mRowId = mRowId;

        getFragmentManager().beginTransaction()
               .replace(R.id.fragment, fragment)
               .commit();

        int rowIndex = mSettings.GetRows().indexOf(mRowId) + 1;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(String.format(getSupportActionBar().getTitle().toString(), rowIndex));

        fabExpanded = false;
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.settings_fab);
        final TextView timeButton = (TextView) findViewById(R.id.settings_fab_time);
        final TextView dateButton = (TextView) findViewById(R.id.settings_fab_date);
        final TextView textButton = (TextView) findViewById(R.id.settings_fab_text);
        final TextView weatherButton = (TextView) findViewById(R.id.settings_fab_weather);
        final TextView fitButton = (TextView) findViewById(R.id.settings_fab_fit);
        final float scale = getResources().getDisplayMetrics().density;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fabExpanded){
                    fabExpanded = true;
                    ViewCompat.animate(fab).rotation(45.0F).setDuration(300).start();
                    ViewCompat.animate(fitButton).alpha(1).yBy(-275 * scale).setDuration(300);
                    ViewCompat.animate(weatherButton).alpha(1).yBy(-225 * scale).setDuration(300);
                    ViewCompat.animate(timeButton).alpha(1).yBy(-175 * scale).setDuration(300);
                    ViewCompat.animate(dateButton).alpha(1).yBy(-125 * scale).setDuration(300);
                    ViewCompat.animate(textButton).alpha(1).yBy(-75 * scale).setDuration(300);
                    fitButton.setClickable(true);
                    weatherButton.setClickable(true);
                    timeButton.setClickable(true);
                    dateButton.setClickable(true);
                    textButton.setClickable(true);
                } else {
                    fabExpanded = false;
                    ViewCompat.animate(fab).rotation(0.0F).setDuration(300).start();
                    ViewCompat.animate(fitButton).alpha(0).yBy(275 * scale).setDuration(300);
                    ViewCompat.animate(weatherButton).alpha(0).yBy(225 * scale).setDuration(300);
                    ViewCompat.animate(timeButton).alpha(0).yBy(175 * scale).setDuration(300);
                    ViewCompat.animate(dateButton).alpha(0).yBy(125 * scale).setDuration(300);
                    ViewCompat.animate(textButton).alpha(0).yBy(75 * scale).setDuration(300);
                    fitButton.setClickable(false);
                    weatherButton.setClickable(false);
                    timeButton.setClickable(false);
                    dateButton.setClickable(false);
                    textButton.setClickable(false);
                }
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(v.getContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);

                int itemNum = mSettings.AddRowItem(mRowId, "Time");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsTimeActivity.PreferencesFragment.SaveDefaultSettings(editor, mWatchId, mRowId, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsTimeActivity.class);
                intent.putExtra("ROW_ID", mRowId);
                intent.putExtra("ITEM_ID", itemNum);
                intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                v.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(v.getContext()), keys);
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(v.getContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);

                int itemNum = mSettings.AddRowItem(mRowId, "Date");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsDateActivity.PreferencesFragment.SaveDefaultSettings(editor, mWatchId, mRowId, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsDateActivity.class);
                intent.putExtra("ROW_ID", mRowId);
                intent.putExtra("ITEM_ID", itemNum);
                intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                v.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(v.getContext()), keys);
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(v.getContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);

                int itemNum = mSettings.AddRowItem(mRowId, "Text");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsTextActivity.PreferencesFragment.SaveDefaultSettings(editor, mWatchId, mRowId, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsTextActivity.class);
                intent.putExtra("ROW_ID", mRowId);
                intent.putExtra("ITEM_ID", itemNum);
                intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                v.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(v.getContext()), keys);
            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(v.getContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);

                int itemNum = mSettings.AddRowItem(mRowId, "Weather");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsWeatherActivity.PreferencesFragment.SaveDefaultSettings(editor, mWatchId, mRowId, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsWeatherActivity.class);
                intent.putExtra("ROW_ID", mRowId);
                intent.putExtra("ITEM_ID", itemNum);
                intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                v.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(v.getContext()), keys);
            }
        });

        fitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(v.getContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);

                int itemNum = mSettings.AddRowItem(mRowId, "Fit");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsFitActivity.PreferencesFragment.SaveDefaultSettings(editor, mWatchId, mRowId, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsFitActivity.class);
                intent.putExtra("ROW_ID", mRowId);
                intent.putExtra("ITEM_ID", itemNum);
                intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                v.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(v.getContext()), keys);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (fabExpanded) {
            fabExpanded = false;
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.settings_fab);
            TextView timeButton = (TextView) findViewById(R.id.settings_fab_time);
            TextView dateButton = (TextView) findViewById(R.id.settings_fab_date);
            TextView textButton = (TextView) findViewById(R.id.settings_fab_text);
            TextView weatherButton = (TextView) findViewById(R.id.settings_fab_weather);
            TextView fitButton = (TextView) findViewById(R.id.settings_fab_fit);
            float scale = getResources().getDisplayMetrics().density;

            fab.setRotation(0.0F);
            fitButton.setAlpha(0);
            fitButton.setY(275 * scale + fitButton.getY());
            weatherButton.setAlpha(0);
            weatherButton.setY(225 * scale + weatherButton.getY());
            timeButton.setAlpha(0);
            timeButton.setY(175 * scale + timeButton.getY());
            dateButton.setAlpha(0);
            dateButton.setY(125 * scale + dateButton.getY());
            textButton.setAlpha(0);
            textButton.setY(75 * scale + textButton.getY());
            timeButton.setClickable(false);
            dateButton.setClickable(false);
            textButton.setClickable(false);
            weatherButton.setClickable(false);
            fitButton.setClickable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_delete) {
            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to delete row?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);
                        HashSet<String> keys = mSettings.DeleteRow(mRowId);
                        onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), keys);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PreferencesFragment extends SettingsSharedFragment {

        public int mRowId;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_row_content);

            PreferenceCategory category = (PreferenceCategory)findPreference("row_general");

            AddListPreference(category, "Alignment", mWatchId + "_row_" + mRowId + "_align", R.array.align_modes);
            Preference paddingPref = AddPreference(category, "Padding");
            paddingPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager fm = getFragmentManager();
                    RowPaddingPickerActivity editPaddingDialog = new RowPaddingPickerActivity();
                    editPaddingDialog.rowNum = mRowId;
                    editPaddingDialog.mWatchId = mWatchId;
                    editPaddingDialog.show(fm, "pref_padding_picker");
                    return true;
                }
            });

            Preference orderPref = AddPreference(category, "Reorder");
            orderPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager fm = getFragmentManager();
                    RowOrderPickerActivity editOrderDialog = new RowOrderPickerActivity();
                    editOrderDialog.rowNum = mRowId;
                    editOrderDialog.mSettings = mSettings;
                    editOrderDialog.show(fm, "row_order_picker");
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();

            PreferenceScreen screen = this.getPreferenceScreen();

            ArrayList<Integer> rowItems = mSettings.GetRowItems(mRowId);
            PreferenceCategory category = (PreferenceCategory)findPreference("row_items");
            category.removeAll();

            for (Integer item : rowItems)
            {
                Preference pref = new Preference(screen.getContext());
                String itemType = mSettings.GetRowItemType(mRowId, item);
                pref.setTitle(itemType);
                String itemValue = mSettings.GetRowItemValue(mRowId, item);
                if (itemValue != null)
                    pref.setSummary("{" + itemValue + "}");
                final int itemNum = item;
                final Class itemClass = SettingsManager.GetRowItemClass(itemType);

                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (itemClass == Object.class)
                            return false;

                        Intent intent = new Intent(getView().getContext(), itemClass);
                        intent.putExtra("ROW_ID", mRowId);
                        intent.putExtra("ITEM_ID", itemNum);
                        intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mWatchId);
                        getView().getContext().startActivity(intent);
                        return true;
                    }
                });
                category.addPreference(pref);
            }
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, int rowNum, String mWatchId)
        {
            HashSet<String> keys = new HashSet<String>();
            keys.add(mWatchId + "_row_" + rowNum + "_align");
            preferences.putString(mWatchId + "_row_" + rowNum + "_align", "Center");
            keys.add(mWatchId + "_row_" + rowNum + "_padding_left");
            preferences.putString(mWatchId + "_row_" + rowNum + "_padding_left", "10");
            keys.add(mWatchId + "_row_" + rowNum + "_padding_right");
            preferences.putString(mWatchId + "_row_" + rowNum + "_padding_right", "10");
            keys.add(mWatchId + "_row_" + rowNum + "_padding_top");
            preferences.putString(mWatchId + "_row_" + rowNum + "_padding_top", "10");
            keys.add(mWatchId + "_row_" + rowNum + "_padding_bottom");
            preferences.putString(mWatchId + "_row_" + rowNum + "_padding_bottom", "10");

            //default keys
            keys.add(mWatchId + "_rows");

            return keys;
        }
    }
}
