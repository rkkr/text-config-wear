package rkr.wear.stringblockwatch;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import java.util.HashSet;


public class SettingsItemFragment extends PreferenceFragment {

    private PreferenceCategory category;
    private int rowNum;
    private int itemNum;

    //@Override
    public void onCreate(Bundle savedInstanceState, final int rowNum, final int itemNum) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_row_blank);

        this.itemNum = itemNum;
        this.rowNum = rowNum;

        PreferenceCategory configCategory = new PreferenceCategory(this.getPreferenceScreen().getContext());
        configCategory.setTitle("Configuration");
        this.getPreferenceScreen().addPreference(configCategory);

        this.category = new PreferenceCategory(this.getPreferenceScreen().getContext());
        this.category.setTitle("Display");
        this.getPreferenceScreen().addPreference(this.category);

        AddSwitchPreference("Hide on idle mode", "hide_idle");
        Preference pref = AddPreference("Reorder");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = getFragmentManager();
                PrefOrderPickerActivity editOrderDialog = new PrefOrderPickerActivity();
                editOrderDialog.itemNum = itemNum;
                editOrderDialog.rowNum = rowNum;
                editOrderDialog.show(fm, "pref_order_picker");
                return true;
            }
        });

        this.category = new PreferenceCategory(this.getPreferenceScreen().getContext());
        this.category.setTitle("Font");
        this.getPreferenceScreen().addPreference(this.category);

        AddListPreference("Font Size", "text_size", R.array.font_size);
        AddMultiSelectListPreference("Font Type", "text_font", R.array.font_type);
        AddListPreference("Font Color", "text_color", R.array.font_color);

        this.category = configCategory;
    }

    public Preference AddPreference(String title)
    {
        Preference pref = new Preference(category.getContext());
        pref.setTitle(title);
        category.addPreference(pref);
        return  pref;
    }

    public void AddSwitchPreference(String title, String key)
    {
        SwitchPreference pref = new SwitchPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey("row_" + rowNum + "_item_" + itemNum + "_" + key);
        category.addPreference(pref);
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

    public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, int rowNum, int itemNum)
    {
        HashSet<String> keys = new HashSet<>();

        keys.add("row_" + rowNum + "_item_" + itemNum + "_text_size");
        preferences.putString("row_" + rowNum + "_item_" + itemNum + "_text_size", "40");
        keys.add("row_" + rowNum + "_item_" + itemNum + "_text_font");
        preferences.putStringSet("row_" + rowNum + "_item_" + itemNum + "_text_font", new HashSet<String>());
        keys.add("row_" + rowNum + "_item_" + itemNum + "_text_color");
        preferences.putString("row_" + rowNum + "_item_" + itemNum + "_text_color", "White");

        //Default values for added row, shouldn't be here?
        keys.add("row_" + rowNum + "_items");
        keys.add("row_" + rowNum + "_item_" + itemNum + "_type");

        return keys;
    }
}
