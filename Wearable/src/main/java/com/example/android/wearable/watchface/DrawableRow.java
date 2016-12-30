package com.example.android.wearable.watchface;


import android.content.Context;
import android.graphics.Canvas;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class DrawableRow {

    int height;
    ArrayList<DrawableText> drawableItems;
    private Context context;

    public DrawableRow(Context context, int rowIndex)
    {
        this.context = context;

        drawableItems = new ArrayList<DrawableText>();
        ArrayList<Integer> rowItems = GetRowItems(rowIndex);
        for (Integer itemNum : rowItems)
            drawableItems.add(new DrawableText(context, rowIndex, itemNum));

        height = 0;
        for(DrawableText item : drawableItems)
            if (height < item.height)
                height = item.height;
    }

    public void Draw(Canvas canvas, int startX, int startY)
    {
        for(DrawableText item : drawableItems)
        {
            item.Draw(canvas, startX, startY);
            startX += item.width();
        }
    }

    public int width()
    {
        int width = 0;
        for(DrawableText item : drawableItems)
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
}
