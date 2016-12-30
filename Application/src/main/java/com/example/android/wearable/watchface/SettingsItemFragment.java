package com.example.android.wearable.watchface;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;


public class SettingsItemFragment extends PreferenceFragment {

    private PreferenceCategory category;
    private int rowNum;
    private int itemNum;

    //@Override
    public void onCreate(Bundle savedInstanceState, int rowNum, int itemNum) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_row_blank);

        this.itemNum = itemNum;
        this.rowNum = rowNum;

        PreferenceCategory configCategory = new PreferenceCategory(this.getPreferenceScreen().getContext());
        configCategory.setTitle("Configuration");
        this.getPreferenceScreen().addPreference(configCategory);

        this.category = new PreferenceCategory(this.getPreferenceScreen().getContext());
        this.category.setTitle("Font");
        this.getPreferenceScreen().addPreference(this.category);

        AddListPreference("Font Size", "text_size", R.array.font_size);
        AddMultiSelectListPreference("Font Type", "text_font", R.array.font_type);
        AddListPreference("Font Color", "text_color", R.array.font_color);

        this.category = configCategory;
    }

    public void AddEditTextPreference(String title, String key)
    {
        EditTextPreference pref = new EditTextPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey("row_" + rowNum + "_item_" + itemNum + "_" + key);
        category.addPreference(pref);
    }

    public void AddListPreference(String title, String key, int resource)
    {
        ListPreference pref = new ListPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey("row_" + rowNum + "_item_" + itemNum + "_" + key);
        pref.setEntries(resource);
        pref.setEntryValues(resource);
        category.addPreference(pref);
    }

    public void AddMultiSelectListPreference(String title, String key, int resource)
    {
        MultiSelectListPreference pref = new MultiSelectListPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey("row_" + rowNum + "_item_" + itemNum + "_" + key);
        pref.setEntries(resource);
        pref.setEntryValues(resource);
        category.addPreference(pref);
    }
}
