package com.example.android.wearable.watchface;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class SettingsTextActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static int rowNum;
    private static int itemNum;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        try {
            Log.d("Settings", key + " = " + sharedPreferences.getString(key, ""));
        } catch (Exception e) {

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        rowNum = intent.getIntExtra("ROW_ID", 1);
        itemNum = intent.getIntExtra("ITEM_ID", 1);

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

            addPreferencesFromResource(R.xml.pref_row_blank);

            //for (int i=0; i < getPreferenceScreen().getPreferenceCount(); i++)
            //{
            //    Preference pref = getPreferenceScreen().getPreference(i);
            //    pref.setKey("row_" + rowNum + "_item_" + itemNum + pref.getKey());
            //
            //}

            PreferenceScreen screen = this.getPreferenceScreen();

            EditTextPreference pref1 = new EditTextPreference(screen.getContext());
            pref1.setTitle("Text");
            pref1.setKey("row_" + rowNum + "_item_" + itemNum + "_text_value");
            screen.addPreference(pref1);

            ListPreference pref2 = new ListPreference(screen.getContext());
            pref2.setTitle("Font Size");
            pref2.setKey("row_" + rowNum + "_item_" + itemNum + "_text_size");
            pref2.setEntries(R.array.font_size);
            pref2.setEntryValues(R.array.font_size);
            screen.addPreference(pref2);

            MultiSelectListPreference pref3 = new MultiSelectListPreference(screen.getContext());
            pref3.setTitle("Font Type");
            pref3.setKey("row_" + rowNum + "_item_" + itemNum + "_text_font");
            pref3.setEntries(R.array.font_type);
            pref3.setEntryValues(R.array.font_type);
            screen.addPreference(pref3);

            ListPreference pref4 = new ListPreference(screen.getContext());
            pref4.setTitle("Font Color");
            pref4.setKey("row_" + rowNum + "_item_" + itemNum + "_text_color");
            pref4.setEntries(R.array.font_color);
            pref4.setEntryValues(R.array.font_color);
            screen.addPreference(pref4);
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
