package rkr.wear.stringblockwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends SettingsCommon {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_fab);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, new PreferencesFragment())
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.settings_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(view.getContext()).unregisterOnSharedPreferenceChangeListener(MainActivity.this);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit();
                int rowNum = Util.AddRow(view.getContext());
                HashSet<String> keys = SettingsRowActivity.PreferencesFragment.SaveDefaultSettings(editor, rowNum);
                editor.commit();

                Intent intent = new Intent(view.getContext(), SettingsRowActivity.class);
                intent.putExtra("ROW_ID", rowNum);
                view.getContext().startActivity(intent);
                onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(view.getContext()), keys);
            }
        });

        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).contains("rows")) {
            //We are started for the first time or app settings have been cleared
            ImportActivity.ImportWatch(getApplicationContext().getResources().openRawResource(R.raw.watch_sample1), getApplicationContext());
        }
    }

    public static class PreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            Preference advanced = findPreference("advanced");
            advanced.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getView().getContext(), AdvancedActivity.class);
                    getView().getContext().startActivity(intent);
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();

            PreferenceScreen screen = this.getPreferenceScreen();

            Preference importSample = findPreference("import");
            importSample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getView().getContext(), ImportActivity.class);
                    getView().getContext().startActivity(intent);
                    return true;
                }
            });

            ArrayList<Integer> rows = Util.GetRows(getView().getContext());
            PreferenceCategory category = (PreferenceCategory)findPreference("rows");
            category.removeAll();

            for (final Integer item : rows)
            {
                Preference pref = new Preference(screen.getContext());
                pref.setTitle("Row");

                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(getView().getContext(), SettingsRowActivity.class);
                        intent.putExtra("ROW_ID", item);
                        getView().getContext().startActivity(intent);
                        return true;
                    }
                });
                category.addPreference(pref);
            }
        }
    }
}
