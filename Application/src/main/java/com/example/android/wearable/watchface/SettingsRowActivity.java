package com.example.android.wearable.watchface;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.ArrayList;

public class SettingsRowActivity extends SettingsCommon {

    private static int rowNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        rowNum = intent.getIntExtra("ROW_ID", 1);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
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
}
