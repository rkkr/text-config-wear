package rkr.wear.stringblockwatch.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import rkr.wear.stringblockwatch.block.SettingsDateActivity;
import rkr.wear.stringblockwatch.block.SettingsTextActivity;
import rkr.wear.stringblockwatch.block.SettingsTimeActivity;
import rkr.wear.stringblockwatch.block.SettingsWeatherActivity;
import rkr.wear.stringblockwatch.block.SettingsFitActivity;

public class SettingsManager {

    private String watchId;
    private Context context;

    public SettingsManager(Context context, String watchId) {
        this.context = context;
        this.watchId = watchId;
        if (watchId == null)
            Log.e("SettingManager", "Phone ID unavailable");
    }

    public boolean HasSettings()
    {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(watchId + "_rows");
    }

    public ArrayList<Integer> GetRows()
    {
        String rowItems = PreferenceManager.getDefaultSharedPreferences(context).getString(watchId + "_rows", "");
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (!rowItems.equals(""))
            for (String item: rowItems.split(","))
                list.add(Integer.parseInt(item));
        return list;
    }

    public void SaveRows(ArrayList<Integer> list)
    {
        String rows = TextUtils.join(",", list);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(watchId + "_rows", rows).commit();
    }

    public ArrayList<Integer> GetRowItems(int rowNum)
    {
        String rowItems = PreferenceManager.getDefaultSharedPreferences(context).getString(watchId + "_row_" + rowNum + "_items", "");
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (!rowItems.equals(""))
            for (String item: rowItems.split(","))
                list.add(Integer.parseInt(item));
        return list;
    }

    public void SaveRowItems(int rowNum, ArrayList<Integer> list)
    {
        String rowItems = TextUtils.join(",", list);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(watchId + "_row_" + rowNum + "_items", rowItems).commit();
    }

    public int AddRow()
    {
        ArrayList<Integer> items = GetRows();
        int max = 0;
        for (Integer item : items)
            if (item > max)
                max = item;
        max++;
        items.add(max);
        SaveRows(items);

        return max;
    }

    public int AddRowItem(int rowNum, String itemType)
    {
        ArrayList<Integer> items = GetRowItems(rowNum);
        int max = 0;
        for (Integer item : items)
            if (item > max)
                max = item;
        max++;
        items.add(max);
        SaveRowItems(rowNum, items);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(watchId + "_row_" + rowNum + "_item_" + max + "_type", itemType).commit();

        return max;
    }

    public HashSet<String> DeleteRowItem(int rowNum, int itemNum)
    {
        ArrayList<Integer> rowItems = GetRowItems(rowNum);
        HashSet<String> keys = new HashSet<String>();
        rowItems.remove(rowItems.indexOf(itemNum));
        SaveRowItems(rowNum, rowItems);
        keys.add(watchId + "_row_" + rowNum + "_items");

        Set<String> prefKeys = PreferenceManager.getDefaultSharedPreferences(context).getAll().keySet();
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
        for (String key : prefKeys)
            if (key.startsWith(watchId + "_row_" + rowNum + "_item_" + itemNum)) {
                preferences.remove(key);
                keys.add(key);
            }

        preferences.commit();
        return keys;
    }

    public HashSet<String> DeleteRow(int rowNum)
    {
        ArrayList<Integer> rows = GetRows();
        HashSet<String> keys = new HashSet<String>();
        rows.remove(rows.indexOf(rowNum));
        SaveRows(rows);
        keys.add(watchId + "_rows");

        Set<String> prefKeys = PreferenceManager.getDefaultSharedPreferences(context).getAll().keySet();
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
        for (String key : prefKeys)
            if (key.startsWith(watchId + "_row_" + rowNum + "_")) {
                preferences.remove(key);
                keys.add(key);
            }

        preferences.commit();
        return keys;
    }

    public String GetRowItemType(int rowNum, int itemNum)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(watchId + "_row_" + rowNum + "_item_" + itemNum + "_type", "");
    }

    public String GetRowItemValue(int rowNum, int itemNum)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(watchId + "_row_" + rowNum + "_item_" + itemNum + "_value", null);
    }

    public static Class GetRowItemClass(String itemType)
    {
        switch (itemType)
        {
            case "Text":
                return SettingsTextActivity.class;
            case "Time":
                return SettingsTimeActivity.class;
            case "Date":
                return SettingsDateActivity.class;
            case "Weather":
                return SettingsWeatherActivity.class;
            case "Fit":
                return SettingsFitActivity.class;
            default:
                return Object.class;
        }
    }
}
