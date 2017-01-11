package rkr.wear.stringblockwatch;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImportActivity extends SettingsCommon {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_fab);

        final PreferencesFragment fragment = new PreferencesFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.settings_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                WatchNamePickerActivity editNameDialog = new WatchNamePickerActivity();
                editNameDialog.setOnRefreshCallback(fragment.mRefreshCallback);
                editNameDialog.show(fm, "watch_name_picker");
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Not needed here
    }

    public static void RenameWatch(String oldName, String newName, Context context) {
        try {
            oldName = Base64.encodeToString(oldName.getBytes("UTF-8"), Base64.DEFAULT);
            newName = Base64.encodeToString(newName.getBytes("UTF-8"), Base64.DEFAULT);

            File folder = new File(context.getFilesDir(), "saved_watches");
            if (!folder.exists()) {
                Log.e("ImportActivity", "Application folder not found");
                return;
            }
            File oldFile = new File(folder, oldName);
            if (!oldFile.exists()) {
                Log.e("ImportActivity", "File " + oldName + " not found");
                return;
            }
            File newFile = new File(folder, newName);
            if (newFile.exists()) {
                Log.e("ImportActivity", "File " + newName + " already exists");
                return;
            }
            oldFile.renameTo(newFile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void DeleteWatch(String fileName, Context context) {
        try {
            fileName = Base64.encodeToString(fileName.getBytes("UTF-8"), Base64.DEFAULT);

            File folder = new File(context.getFilesDir(), "saved_watches");
            if (!folder.exists()) {
                Log.e("ImportActivity", "Application folder not found");
                return;
            }
            File file = new File(folder, fileName);
            if (!file.exists()) {
                Log.e("ImportActivity", "File " + fileName + " not found");
                return;
            }

            file.delete();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void ImportWatch(String fileName, Context context) {
        try {
            fileName = Base64.encodeToString(fileName.getBytes("UTF-8"), Base64.DEFAULT);

            File folder = new File(context.getFilesDir(), "saved_watches");
            if (!folder.exists()) {
                Log.e("ImportActivity", "Application folder not found");
                return;
            }
            File file = new File(folder, fileName);
            if (!file.exists()) {
                Log.e("ImportActivity", "File " + fileName + " not found");
                return;
            }

            FileInputStream inputStream = new FileInputStream(file);
            ImportWatch(inputStream, context);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void ImportWatch(InputStream inputStream, Context context)
    {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();

        try {
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }

            String contents = out.toString();
            JSONObject jObject = new JSONObject(contents);

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.clear();

            for (Iterator<String> key = jObject.keys(); key.hasNext(); ) {
                String _key = key.next();
                Object value = jObject.get(_key);

                editor.putString(_key, (String)value);
            }

            editor.commit();
            Intent intent = new Intent("string.block.watch.FORCE_SYNC");
            context.sendBroadcast(intent);
        }
        catch (UnsupportedEncodingException e) {
            Toast.makeText(context, "Failed to read file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Failed to read file", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(context, "Failed to parse file", Toast.LENGTH_SHORT).show();
        }
    }

    public static void ExportWatch(String fileName, Context context)
    {
        JSONObject jObject = new JSONObject();
        String settingsJson;
        Map<String, ?> settings = PreferenceManager.getDefaultSharedPreferences(context).getAll();
        try {
            fileName = Base64.encodeToString(fileName.getBytes("UTF-8"), Base64.DEFAULT);

            for (String key : settings.keySet()) {
                jObject.put(key, settings.get(key));
            }
            settingsJson = jObject.toString(2);

            File folder = new File(context.getFilesDir(), "saved_watches");
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(folder, fileName);
            FileOutputStream outputStream = new FileOutputStream(file, false);
            outputStream.write(settingsJson.getBytes());
            outputStream.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> ListSavedWatched(Context context)
    {
        ArrayList<String> watches = new ArrayList<>();

        File folder = new File(context.getFilesDir(), "saved_watches");
        if (folder.exists())
            for (String file : folder.list())
                try {
                    watches.add(new String(Base64.decode(file.getBytes(), Base64.DEFAULT), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

        return watches;
    }

    public static class PreferencesFragment extends PreferenceFragment {

        private PreferenceScreen mScreen;
        public RefreshCallback mRefreshCallback = new RefreshCallback() {
            @Override
            public void onRefreshCallback() {
                PreferenceCategory category = (PreferenceCategory)findPreference("watches_saved");
                category.removeAll();

                List<String> watches = ListSavedWatched(mScreen.getContext());
                for (final String watch : watches) {
                    Preference pref = new Preference(mScreen.getContext());
                    pref.setTitle(watch);

                    pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            FragmentManager fm = getFragmentManager();
                            WatchActionPickerActivity actionDialog = new WatchActionPickerActivity();
                            actionDialog.fileName = watch;
                            actionDialog.setOnRefreshCallback(mRefreshCallback);
                            actionDialog.show(fm, "watch_action_picker");
                            return true;
                        }
                    });
                    category.addPreference(pref);
                }
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_import);

            Preference advanced = findPreference("watch_sample_1");
            advanced.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ImportWatch(getResources().openRawResource(R.raw.watch_sample1), preference.getContext());
                    return true;
                }
            });

            mScreen = this.getPreferenceScreen();
            mRefreshCallback.onRefreshCallback();
        }
    }

    public interface RefreshCallback {
        public void onRefreshCallback();
    }
}
