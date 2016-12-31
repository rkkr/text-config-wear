package com.example.android.wearable.watchface;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Dictionary;
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

    public static class PreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_row_content);
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
    }
}
