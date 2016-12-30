package com.example.android.wearable.watchface;


import android.content.Context;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

public class DrawableRow {

    int height;
    ArrayList<IDrawableItem> drawableItems;
    private Context context;

    public DrawableRow(Context context, int rowIndex)
    {
        this.context = context;

        drawableItems = new ArrayList<IDrawableItem>();
        ArrayList<Integer> rowItems = GetRowItems(rowIndex);
        for (Integer itemNum : rowItems) {
            String itemType = GetRowItemType(rowIndex, itemNum);
            switch (itemType)
            {
                case "Text":
                    drawableItems.add(new DrawableText(context, rowIndex, itemNum));
                    break;
                case "Time":
                    drawableItems.add(new DrawableTime(context, rowIndex, itemNum));
                    break;
                case "Date":
                    drawableItems.add(new DrawableDate(context, rowIndex, itemNum));
                    break;
                default:
                    Log.e("StringWatch", "Unknown item type: " + itemType);
            }
        }

        height = 0;
        for(IDrawableItem item : drawableItems)
            if (height < item.height())
                height = item.height();
    }

    public void Draw(Canvas canvas, int startX, int startY)
    {
        for(IDrawableItem item : drawableItems)
        {
            item.Draw(canvas, startX, startY);
            startX += item.width();
        }
    }

    public int width()
    {
        int width = 0;
        for(IDrawableItem item : drawableItems)
            width += item.width();
        return width;
    }

    private ArrayList<Integer> GetRowItems(int rowNum)
    {
        String rowItems = PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_items", "");
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (!rowItems.equals(""))
            for (String item: rowItems.split(","))
                list.add(Integer.parseInt(item));
        return list;
    }

    private String GetRowItemType(int rowNum, int itemNum)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_type", "");
    }
}
