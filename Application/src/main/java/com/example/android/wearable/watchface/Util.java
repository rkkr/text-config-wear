package com.example.android.wearable.watchface;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;

class Util {

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
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("row_" + rowNum + "_items", rowItems).apply();
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
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("row_" + rowNum + "_item_" + max + "_type", itemType).apply();

        return max;
    }

    public static String GetRowItemType(Context context, int rowNum, int itemNum)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_type", "");
    }

}
