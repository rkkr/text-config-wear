package com.example.android.wearable.watchface;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.util.ArrayList;

public class SettingsRowActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static int rowNum;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("Settings", key + " = " + sharedPreferences.getString(key, ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        rowNum = intent.getIntExtra("ROW_ID", 1);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();

        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public static class PreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_row_content);

            PreferenceScreen screen = this.getPreferenceScreen();

            ArrayList<Integer> rowItems = Util.GetRowItems(screen.getContext(), rowNum);
            PreferenceCategory category = (PreferenceCategory)findPreference("row_items");

            for (Integer item : rowItems)
            {
                Preference pref = new Preference(screen.getContext());
                pref.setTitle(Util.GetRowItemType(screen.getContext(), rowNum, item));
                final int itemNum = item;
                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(getView().getContext(), SettingsTextActivity.class);
                        intent.putExtra("ROW_ID", rowNum);
                        intent.putExtra("ITEM_ID", itemNum);
                        getView().getContext().startActivity(intent);
                        return true;
                    }
                });
                category.addPreference(pref);
            }

            Preference pref = new Preference(screen.getContext());
            pref.setTitle("Add new...");
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager fm = getFragmentManager();
                    PrefTypePickerActivity editNameDialog = new PrefTypePickerActivity();
                    editNameDialog.rowNum = rowNum;
                    editNameDialog.show(fm, "pref_type_picker");
                    return true;
                }
            });
            category.addPreference(pref);
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
