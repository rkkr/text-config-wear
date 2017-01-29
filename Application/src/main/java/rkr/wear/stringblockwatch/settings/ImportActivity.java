package rkr.wear.stringblockwatch.settings;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    private static final int STORAGE_PERMISSION_REQUEST = 1;
    private static final int FILE_REQUEST = 2;
    private static  final String TAG = "ImportActivity";

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_import) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                startActivityForResult(intent, FILE_REQUEST);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_REQUEST);
            } else {
                //don't ask again?
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Opening file: " + data.getDataString());
            final Uri uri = data.getData();

            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Downloading...");
            progress.setCancelable(false);
            progress.show();

            new Thread(new Runnable() {
                public void run() {

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        String result = ImportWatch(inputStream, getApplicationContext(), mWatchId);
                        if (result == null)
                            toastInUiThread("File imported");
                        else
                            toastInUiThread(result);
                    } catch (FileNotFoundException e) {
                        toastInUiThread("File not found");
                    }
                    progress.dismiss();
                }
            }).start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Not needed here
    }

    public void toastInUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void RenameWatch(String oldName, String newName, Context context) {
        File oldFile = GetWatchFile(context, oldName);
        if (!oldFile.exists()) {
            Log.e(TAG, "File " + oldName + " not found");
            return;
        }
        File newFile = GetWatchFile(context, newName);
        if (newFile.exists()) {
            Log.e(TAG, "File " + newName + " already exists");
            return;
        }
        oldFile.renameTo(newFile);

        MediaScannerConnection.scanFile(context, new String[] {oldFile.getAbsolutePath(), newFile.getAbsolutePath()}, null, null);
    }

    public static void DeleteWatch(String fileName, Context context) {
        File file = GetWatchFile(context, fileName);
        if (!file.exists()) {
            Log.e(TAG, "File " + fileName + " not found");
            return;
        }
        file.delete();
    }

    public static String ImportWatch(String fileName, Context context, String mWatchId) {
        String result = null;
        try {
            File file = GetWatchFile(context, fileName);
            if (!file.exists()) {
                Log.e(TAG, "File " + fileName + " not found");
                return "File " + fileName + " not found";
            }

            FileInputStream inputStream = new FileInputStream(file);
            result = ImportWatch(inputStream, context, mWatchId);
        } catch (FileNotFoundException e) {
            return "File " + fileName + " not found";
        }
        return result;
    }

    public static String ImportWatch(InputStream inputStream, Context context, String mWatchId)
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
                } else if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                } else if (value instanceof Long) {
                    editor.putLong(key, (Long) value);
                } else if (value instanceof String) {
                    editor.putString(key, (String) value);
                } else {
                    Log.e(TAG, "Unsupported item to import: " + value.getClass());
                }
            }

            editor.commit();
            Intent intent = new Intent("string.block.watch.FORCE_SYNC");
            context.sendBroadcast(intent);
        }
        catch (UnsupportedEncodingException e) {
            return "Invalid file encoding";
        } catch (IOException e) {
            return "Failed to read file";
        } catch (JSONException e) {
            return "Failed to decode file contents";
        }
        return null;
    }

    public static void ExportWatch(String fileName, Context context, String mWatchId)
    {
        //if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
        //    Toast.makeText(context, "External storage not available", Toast.LENGTH_LONG).show();
        //    return;
        //}

        JSONObject jObject = new JSONObject();
        String settingsJson;
        Map<String, ?> settings = PreferenceManager.getDefaultSharedPreferences(context).getAll();
        try {
            for (String key : settings.keySet()) {
                if (!key.startsWith(mWatchId))
                    continue;
                Object item = settings.get(key);
                key = key.replaceFirst(mWatchId + "_", "");
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

            MediaScannerConnection.scanFile(context, new String[] {file.getAbsolutePath()}, new String[] {"application/json"}, null);
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
                if (file.endsWith(".json"))
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
                if (watches.isEmpty()) {
                    Preference pref = new Preference(mContext);
                    pref.setSummary("Click + button to save current configuration");
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
                    String result = ImportWatch(getResources().openRawResource(R.raw.watch_sample1), preference.getContext(), mWatchId);
                    if (result != null)
                        Toast.makeText(preference.getContext(), result, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            sample = findPreference("watch_sample_2");
            sample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String result = ImportWatch(getResources().openRawResource(R.raw.watch_sample2), preference.getContext(), mWatchId);
                    if (result != null)
                        Toast.makeText(preference.getContext(), result, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            sample = findPreference("watch_sample_3");
            sample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String result = ImportWatch(getResources().openRawResource(R.raw.watch_sample3), preference.getContext(), mWatchId);
                    if (result != null)
                        Toast.makeText(preference.getContext(), result, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            sample = findPreference("watch_sample_4");
            sample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String result = ImportWatch(getResources().openRawResource(R.raw.watch_sample4), preference.getContext(), mWatchId);
                    if (result != null)
                        Toast.makeText(preference.getContext(), result, Toast.LENGTH_LONG).show();
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
