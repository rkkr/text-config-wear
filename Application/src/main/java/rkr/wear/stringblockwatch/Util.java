package rkr.wear.stringblockwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Util {

    public static ArrayList<Integer> GetRows(Context context)
    {
        String rowItems = PreferenceManager.getDefaultSharedPreferences(context).getString("rows", "");
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (!rowItems.equals(""))
            for (String item: rowItems.split(","))
                list.add(Integer.parseInt(item));
        return list;
    }

    public static void SaveRows(Context context, ArrayList<Integer> list)
    {
        String rows = TextUtils.join(",", list);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("rows", rows).commit();
    }

    public static ArrayList<Integer> GetRowItems(Context context, int rowNum)
    {
        String rowItems = PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_items", "");
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (!rowItems.equals(""))
            for (String item: rowItems.split(","))
                list.add(Integer.parseInt(item));
        return list;
    }

    public static void SaveRowItems(Context context, int rowNum, ArrayList<Integer> list)
    {
        String rowItems = TextUtils.join(",", list);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("row_" + rowNum + "_items", rowItems).commit();
    }

    public static int AddRow(Context context)
    {
        ArrayList<Integer> items = GetRows(context);
        int max = 0;
        for (Integer item : items)
            if (item > max)
                max = item;
        max++;
        items.add(max);
        SaveRows(context, items);

        return max;
    }

    public static int AddRowItem(Context context, int rowNum, String itemType)
    {
        ArrayList<Integer> items = GetRowItems(context, rowNum);
        int max = 0;
        for (Integer item : items)
            if (item > max)
                max = item;
        max++;
        items.add(max);
        SaveRowItems(context, rowNum, items);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("row_" + rowNum + "_item_" + max + "_type", itemType).commit();

        return max;
    }

    public static HashSet<String> DeleteRowItem(Context context, int rowNum, int itemNum)
    {
        ArrayList<Integer> rowItems = GetRowItems(context, rowNum);
        HashSet<String> keys = new HashSet<String>();
        rowItems.remove(rowItems.indexOf(itemNum));
        SaveRowItems(context, rowNum, rowItems);
        keys.add("row_" + rowNum + "_items");

        Set<String> prefKeys = PreferenceManager.getDefaultSharedPreferences(context).getAll().keySet();
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
        for (String key : prefKeys)
            if (key.startsWith("row_" + rowNum + "_item_" + itemNum)) {
                preferences.remove(key);
                keys.add(key);
            }

        preferences.commit();
        return keys;
    }

    public static HashSet<String> DeleteRow(Context context, int rowNum)
    {
        ArrayList<Integer> rows = GetRows(context);
        HashSet<String> keys = new HashSet<String>();
        rows.remove(rows.indexOf(rowNum));
        SaveRows(context, rows);
        keys.add("rows");

        Set<String> prefKeys = PreferenceManager.getDefaultSharedPreferences(context).getAll().keySet();
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
        for (String key : prefKeys)
            if (key.startsWith("row_" + rowNum + "_")) {
                preferences.remove(key);
                keys.add(key);
            }

        preferences.commit();
        return keys;
    }

    public static String GetRowItemType(Context context, int rowNum, int itemNum)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_type", "");
    }

    public static String GetRowItemValue(Context context, int rowNum, int itemNum)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_value", null);
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
            default:
                return Object.class;
        }
    }
}
