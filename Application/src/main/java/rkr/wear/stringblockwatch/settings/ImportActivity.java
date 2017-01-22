package rkr.wear.stringblockwatch.settings;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rkr.wear.stringblockwatch.R;
import rkr.wear.stringblockwatch.common.SettingsCommon;

public class ImportActivity extends SettingsCommon {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_fab);

        final PreferencesFragment fragment = new PreferencesFragment();
        fragment.mWatchId = mWatchId;
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
                editNameDialog.mWatchId = mWatchId;
                editNameDialog.show(fm, "watch_name_picker");
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Not needed here
    }

    public static void RenameWatch(String oldName, String newName, Context context) {
        File oldFile = GetWatchFile(context, oldName);
        if (!oldFile.exists()) {
            Log.e("ImportActivity", "File " + oldName + " not found");
            return;
        }
        File newFile = GetWatchFile(context, newName);
        if (newFile.exists()) {
            Log.e("ImportActivity", "File " + newName + " already exists");
            return;
        }
        oldFile.renameTo(newFile);

        MediaScannerConnection.scanFile(context, new String[] {oldFile.getAbsolutePath(), newFile.getAbsolutePath()}, null, null);
    }

    public static void DeleteWatch(String fileName, Context context) {
        File file = GetWatchFile(context, fileName);
        if (!file.exists()) {
            Log.e("ImportActivity", "File " + fileName + " not found");
            return;
        }
        file.delete();
    }

    public static void ImportWatch(String fileName, Context context, String mWatchId) {
        try {
            File file = GetWatchFile(context, fileName);
            if (!file.exists()) {
                Log.e("ImportActivity", "File " + fileName + " not found");
                return;
            }

            FileInputStream inputStream = new FileInputStream(file);
            ImportWatch(inputStream, context, mWatchId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void ImportCommonSettings(Context context)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.contains("common_idle_mode_color"))
            editor.putString("common_idle_mode_color", "Gray Scale");
        if (!prefs.contains("common_peek_mode"))
            editor.putString("common_peek_mode", "Black");
        if (!prefs.contains("common_wallpaper_color"))
            editor.putString("common_wallpaper_color", "0xFF000000");
        editor.commit();
    }

    public static void ImportWatch(InputStream inputStream, Context context, String mWatchId)
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
            for (String key : PreferenceManager.getDefaultSharedPreferences(context).getAll().keySet())
                if (key.startsWith(mWatchId))
                    editor.remove(key);

            for (Iterator<String> keys = jObject.keys(); keys.hasNext(); ) {
                String key = keys.next();
                Object value = jObject.get(key);
                key = mWatchId + "_" + key;

                if (value instanceof JSONArray) {
                    JSONArray array = (JSONArray) value;
                    HashSet<String> _value = new HashSet<String>();
                    for (int i = 0; i < array.length(); i++)
                        _value.add(array.getString(i));
                    editor.putStringSet(key, _value);
                } if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                } if (value instanceof String) {
                    editor.putString(key, (String) value);
                } else {
                    Log.e("ImportActivity", "Unsupported item to import");
                }
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

    public static void ExportWatch(String fileName, Context context, String mWatchId)
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, "External storage not available", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jObject = new JSONObject();
        String settingsJson;
        Map<String, ?> settings = PreferenceManager.getDefaultSharedPreferences(context).getAll();
        try {
            for (String key : settings.keySet()) {
                if (key.startsWith(mWatchId))
                    continue;
                Object item = settings.get(key);
                if (item instanceof Set) {
                    JSONArray array = new JSONArray((Set<String>) item);
                    jObject.put(key, array);
                } else {
                    jObject.put(key, item);
                }
            }
            settingsJson = jObject.toString(2);
            //Log.d("Saving watch", settingsJson);

            File file = GetWatchFile(context, fileName);
            FileOutputStream outputStream = new FileOutputStream(file, false);
            outputStream.write(settingsJson.getBytes());
            outputStream.close();

            MediaScannerConnection.scanFile(context, new String[] {file.getAbsolutePath()}, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File GetWatchFile(Context context, String fileName) {
        File folder = new File(context.getExternalFilesDir(null), "saved_watches");
        if (!folder.exists())
            folder.mkdirs();
        return new File(folder, fileName);
    }

    public static ArrayList<String> ListSavedWatches(Context context)
    {
        ArrayList<String> watches = new ArrayList<>();

        File folder = new File(context.getExternalFilesDir(null), "saved_watches");
        if (folder.exists())
            for (String file : folder.list())
                watches.add(file);

        return watches;
    }

    public static class PreferencesFragment extends PreferenceFragment {

        private Context mContext;
        public String mWatchId;

        public RefreshCallback mRefreshCallback = new RefreshCallback() {
            @Override
            public void onRefreshCallback() {
                PreferenceCategory category = (PreferenceCategory)findPreference("watches_saved");
                category.removeAll();

                List<String> watches = ListSavedWatches(mContext);
                for (final String watch : watches) {
                    if (!watch.endsWith(".json"))
                        continue;
                    Preference pref = new Preference(mContext);
                    pref.setTitle(watch.substring(0, watch.lastIndexOf(".json")));

                    pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            FragmentManager fm = getFragmentManager();
                            WatchActionPickerActivity actionDialog = new WatchActionPickerActivity();
                            actionDialog.fileName = watch;
                            actionDialog.mWatchId = mWatchId;
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

            Preference sample = findPreference("watch_sample_1");
            sample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ImportWatch(getResources().openRawResource(R.raw.watch_sample1), preference.getContext(), mWatchId);
                    return true;
                }
            });
            sample = findPreference("watch_sample_2");
            sample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ImportWatch(getResources().openRawResource(R.raw.watch_sample2), preference.getContext(), mWatchId);
                    return true;
                }
            });
            sample = findPreference("watch_sample_3");
            sample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ImportWatch(getResources().openRawResource(R.raw.watch_sample3), preference.getContext(), mWatchId);
                    return true;
                }
            });

            mContext = this.getPreferenceScreen().getContext();
            mRefreshCallback.onRefreshCallback();
        }
    }

    public interface RefreshCallback {
        public void onRefreshCallback();
    }
}
