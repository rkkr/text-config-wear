package rkr.wear.stringblockwatch.common;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.util.Log;


public class SettingsSharedFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public String mWatchId;
    public SettingsManager mSettings;

    public Preference AddPreference(PreferenceCategory category, String title)
    {
        Preference pref = new Preference(category.getContext());
        pref.setTitle(title);
        category.addPreference(pref);
        return  pref;
    }

    public SwitchPreference AddSwitchPreference(PreferenceCategory category, String title, String key)
    {
        SwitchPreference pref = new SwitchPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey(key);
        category.addPreference(pref);
        return pref;
    }

    public EditTextPreference AddEditTextPreference(PreferenceCategory category, String title, String key)
    {
        EditTextPreference pref = new EditTextPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey(key);
        category.addPreference(pref);
        return pref;
    }

    public ListPreference AddListPreference(PreferenceCategory category, String title, String key, int resource)
    {
        return AddListPreference(category, title, key, resource, resource);
    }

    public ListPreference AddListPreference(PreferenceCategory category, String title, String key, int resource, int valueResource)
    {
        ListPreference pref = new ListPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey(key);
        pref.setEntries(resource);
        pref.setEntryValues(valueResource);
        category.addPreference(pref);
        return pref;
    }

    public MultiSelectListPreference AddMultiSelectListPreference(PreferenceCategory category, String title, String key, int resource)
    {
        MultiSelectListPreference pref = new MultiSelectListPreference(category.getContext());
        pref.setTitle(title);
        pref.setKey(key);
        pref.setEntries(resource);
        pref.setEntryValues(resource);
        category.addPreference(pref);
        return pref;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWatchId == null)
            Log.e("SettingsSharedFragment", "watchID == null");
        this.mSettings = new SettingsManager(getPreferenceScreen().getContext(), mWatchId);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        initSummary(getPreferenceScreen());
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
    }

    private void initSummary(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                initSummary(preferenceGroup.getPreference(i));
            }
        } else {
            updatePrefSummary(preference);
        }
    }

    private void updatePrefSummary(Preference preference) {
        if (preference instanceof ListPreference) {
            ListPreference listPref = (ListPreference) preference;
            preference.setSummary(listPref.getEntry());
        }
        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) preference;
            preference.setSummary(editTextPref.getText());
        }
    }
}
