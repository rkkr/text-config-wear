package rkr.wear.stringblockwatch.block;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import java.util.HashSet;

import rkr.wear.stringblockwatch.common.PrefOrderPickerActivity;
import rkr.wear.stringblockwatch.R;
import rkr.wear.stringblockwatch.common.SettingsManager;
import rkr.wear.stringblockwatch.common.SettingsSharedFragment;


public class SettingsItemFragment extends SettingsSharedFragment {

    public PreferenceCategory defaultCategory;
    private int rowNum;
    private int itemNum;
    private SettingsManager mSettings;

    //@Override
    public void onCreate(Bundle savedInstanceState, String phoneId, final int rowNum, final int itemNum) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_row_blank);
        super.Create(phoneId);

        this.itemNum = itemNum;
        this.rowNum = rowNum;
        this.mSettings = new SettingsManager(this.getPreferenceScreen().getContext(), phoneId);

        PreferenceCategory category = new PreferenceCategory(this.getPreferenceScreen().getContext());
        category.setTitle("Configuration");
        this.getPreferenceScreen().addPreference(category);
        defaultCategory = category;

        category = new PreferenceCategory(this.getPreferenceScreen().getContext());
        category.setTitle("Display");
        this.getPreferenceScreen().addPreference(category);

        AddSwitchPreference(category, "Hide on idle mode", "hide_idle");
        Preference pref = AddPreference(category, "Reorder");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = getFragmentManager();
                PrefOrderPickerActivity editOrderDialog = new PrefOrderPickerActivity();
                editOrderDialog.itemNum = itemNum;
                editOrderDialog.rowNum = rowNum;
                editOrderDialog.mSettings = mSettings;
                editOrderDialog.show(fm, "pref_order_picker");
                return true;
            }
        });

        category = new PreferenceCategory(this.getPreferenceScreen().getContext());
        category.setTitle("Font");
        this.getPreferenceScreen().addPreference(category);

        AddListPreference(category, "Font Size", "text_size", R.array.font_size);
        AddMultiSelectListPreference(category, "Font Type", "text_font", R.array.font_type);
        AddListPreference(category, "Font Color", "text_color", R.array.font_color, R.array.font_color_value);
    }

    public Preference AddPreference(PreferenceCategory category, String title)
    {
        return super.AddPreference(category, title);
    }

    public SwitchPreference AddSwitchPreference(PreferenceCategory category, String title, String key)
    {
        return super.AddSwitchPreference(category, title, phoneId + "_row_" + rowNum + "_item_" + itemNum + "_" + key);
    }

    public EditTextPreference AddEditTextPreference(PreferenceCategory category, String title, String key)
    {
        return super.AddEditTextPreference(category, title, phoneId + "_row_" + rowNum + "_item_" + itemNum + "_" + key);
    }

    public ListPreference AddListPreference(PreferenceCategory category, String title, String key, int resource)
    {
        return super.AddListPreference(category, title, phoneId + "_row_" + rowNum + "_item_" + itemNum + "_" + key, resource, resource);
    }

    public ListPreference AddListPreference(PreferenceCategory category, String title, String key, int resource, int valueResource)
    {
        return super.AddListPreference(category, title, phoneId + "_row_" + rowNum + "_item_" + itemNum + "_" + key, resource, valueResource);
    }

    public MultiSelectListPreference AddMultiSelectListPreference(PreferenceCategory category, String title, String key, int resource)
    {
        return super.AddMultiSelectListPreference(category, title, phoneId + "_row_" + rowNum + "_item_" + itemNum + "_" + key, resource);
    }

    public static HashSet<String> SaveDefaultSettings(SharedPreferences.Editor preferences, String phoneId, int rowNum, int itemNum)
    {
        HashSet<String> keys = new HashSet<>();

        keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_text_size");
        preferences.putString(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_text_size", "40");
        keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_text_font");
        preferences.putStringSet(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_text_font", new HashSet<String>());
        keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_text_color");
        preferences.putString(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_text_color", "0xFFFFFFFF");

        //Default values for added row, shouldn't be here?
        keys.add(phoneId + "_row_" + rowNum + "_items");
        keys.add(phoneId + "_row_" + rowNum + "_item_" + itemNum + "_type");

        return keys;
    }
}
