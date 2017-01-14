package rkr.wear.stringblockwatch;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class SettingsRowActivity extends SettingsCommon {

    private static int rowNum;
    private Boolean fabExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        rowNum = intent.getIntExtra("ROW_ID", 1);
        setContentView(R.layout.settings_fab);

        getFragmentManager().beginTransaction()
               .replace(R.id.fragment, new PreferencesFragment())
               .commit();

        int rowIndex = Util.GetRows(getApplicationContext()).indexOf(rowNum) + 1;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(String.format(getSupportActionBar().getTitle().toString(), rowIndex));

        fabExpanded = false;
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.settings_fab);
        final TextView timeButton = (TextView) findViewById(R.id.settings_fab_time);
        final TextView dateButton = (TextView) findViewById(R.id.settings_fab_date);
        final TextView textButton = (TextView) findViewById(R.id.settings_fab_text);
        final float scale = getResources().getDisplayMetrics().density;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fabExpanded){
                    fabExpanded = true;
                    ViewCompat.animate(fab).rotation(45.0F).setDuration(300).start();
                    ViewCompat.animate(timeButton).alpha(1).yBy(-175 * scale).setDuration(300);
                    ViewCompat.animate(dateButton).alpha(1).yBy(-125 * scale).setDuration(300);
                    ViewCompat.animate(textButton).alpha(1).yBy(-75 * scale).setDuration(300);
                    timeButton.setClickable(true);
                    dateButton.setClickable(true);
                    textButton.setClickable(true);
                } else {
                    fabExpanded = false;
                    ViewCompat.animate(fab).rotation(0.0F).setDuration(300).start();
                    ViewCompat.animate(timeButton).alpha(0).yBy(175 * scale).setDuration(300);
                    ViewCompat.animate(dateButton).alpha(0).yBy(125 * scale).setDuration(300);
                    ViewCompat.animate(textButton).alpha(0).yBy(75 * scale).setDuration(300);
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

                int itemNum = Util.AddRowItem(v.getContext(), rowNum, "Time");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsTimeActivity.PreferencesFragment.SaveDefaultSettings(editor, rowNum, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsTimeActivity.class);
                intent.putExtra("ROW_ID", rowNum);
                intent.putExtra("ITEM_ID", itemNum);
                v.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(v.getContext()), keys);
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(v.getContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);

                int itemNum = Util.AddRowItem(v.getContext(), rowNum, "Date");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsDateActivity.PreferencesFragment.SaveDefaultSettings(editor, rowNum, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsDateActivity.class);
                intent.putExtra("ROW_ID", rowNum);
                intent.putExtra("ITEM_ID", itemNum);
                v.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(v.getContext()), keys);
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(v.getContext()).unregisterOnSharedPreferenceChangeListener(SettingsRowActivity.this);

                int itemNum = Util.AddRowItem(v.getContext(), rowNum, "Text");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                HashSet<String> keys = SettingsTextActivity.PreferencesFragment.SaveDefaultSettings(editor, rowNum, itemNum);
                editor.commit();

                Intent intent = new Intent(v.getContext(), SettingsTextActivity.class);
                intent.putExtra("ROW_ID", rowNum);
                intent.putExtra("ITEM_ID", itemNum);
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
            float scale = getResources().getDisplayMetrics().density;

            fab.setRotation(0.0F);
            timeButton.setAlpha(0);
            timeButton.setY(175 * scale + timeButton.getY());
            dateButton.setAlpha(0);
            dateButton.setY(125 * scale + dateButton.getY());
            textButton.setAlpha(0);
            textButton.setY(75 * scale + textButton.getY());
            timeButton.setClickable(false);
            dateButton.setClickable(false);
            textButton.setClickable(false);
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
                        HashSet<String> keys = Util.DeleteRow(getApplicationContext(), rowNum);
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

    public static class PreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_row_content);

            PreferenceScreen screen = this.getPreferenceScreen();
            PreferenceCategory category = (PreferenceCategory)findPreference("row_general");

            ListPreference alignmentPref = new ListPreference(screen.getContext());
            alignmentPref.setKey("row_" + rowNum + "_align");
            alignmentPref.setEntryValues(R.array.align_modes);
            alignmentPref.setEntries(R.array.align_modes);
            alignmentPref.setTitle("Alignment");
            category.addPreference(alignmentPref);

            Preference paddingPref = new Preference(screen.getContext());
            paddingPref.setTitle("Padding");
            paddingPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager fm = getFragmentManager();
                    RowPaddingPickerActivity editPaddingDialog = new RowPaddingPickerActivity();
                    editPaddingDialog.rowNum = rowNum;
                    editPaddingDialog.show(fm, "pref_padding_picker");
                    return true;
                }
            });
            category.addPreference(paddingPref);

            Preference orderPref = new Preference(screen.getContext());
            orderPref.setTitle("Reorder");
            orderPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager fm = getFragmentManager();
                    RowOrderPickerActivity editOrderDialog = new RowOrderPickerActivity();
                    editOrderDialog.rowNum = rowNum;
                    editOrderDialog.show(fm, "row_order_picker");
                    return true;
                }
            });
            category.addPreference(orderPref);
        }

        @Override
        public void onResume() {
            super.onResume();

            PreferenceScreen screen = this.getPreferenceScreen();

            ArrayList<Integer> rowItems = Util.GetRowItems(screen.getContext(), rowNum);
            PreferenceCategory category = (PreferenceCategory)findPreference("row_items");
            category.removeAll();

            for (Integer item : rowItems)
            {
                Preference pref = new Preference(screen.getContext());
                String itemType = Util.GetRowItemType(screen.getContext(), rowNum, item);
                pref.setTitle(itemType);
                String itemValue = Util.GetRowItemValue(screen.getContext(), rowNum, item);
                if (itemValue != null)
                    pref.setSummary("{" + itemValue + "}");
                final int itemNum = item;
                final Class itemClass = Util.GetRowItemClass(itemType);

                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (itemClass == Object.class)
                            return false;

                        Intent intent = new Intent(getView().getContext(), itemClass);
                        intent.putExtra("ROW_ID", rowNum);
                        intent.putExtra("ITEM_ID", itemNum);
                        getView().getContext().startActivity(intent);
                        return true;
                    }
                });
                category.addPreference(pref);
            }
        }

        public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, int rowNum)
        {
            HashSet<String> keys = new HashSet<String>();
            keys.add("row_" + rowNum + "_align");
            preferences.putString("row_" + rowNum + "_align", "Center");
            keys.add("row_" + rowNum + "_padding_left");
            preferences.putString("row_" + rowNum + "_padding_left", "10");
            keys.add("row_" + rowNum + "_padding_right");
            preferences.putString("row_" + rowNum + "_padding_right", "10");
            keys.add("row_" + rowNum + "_padding_top");
            preferences.putString("row_" + rowNum + "_padding_top", "10");
            keys.add("row_" + rowNum + "_padding_bottom");
            preferences.putString("row_" + rowNum + "_padding_bottom", "10");

            //default keys
            keys.add("rows");

            return keys;
        }
    }
}
