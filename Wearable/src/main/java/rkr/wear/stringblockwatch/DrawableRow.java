package rkr.wear.stringblockwatch;


import android.content.Context;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

public class DrawableRow {

    private int height;
    private int paddingTop;
    private int paddingLeft;
    private int paddingRight;
    private int paddingBottom;
    private ArrayList<IDrawableItem> drawableItems;
    private Context context;
    public String alignment;

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
        paddingTop = Integer.parseInt(GetRowString(rowIndex, "padding_top", "0"));
        paddingLeft = Integer.parseInt(GetRowString(rowIndex, "padding_left", "0"));
        paddingRight = Integer.parseInt(GetRowString(rowIndex, "padding_right", "0"));
        paddingBottom = Integer.parseInt(GetRowString(rowIndex, "padding_bottom", "0"));
        alignment = GetRowString(rowIndex, "align", "Center");
    }

    public void Draw(Canvas canvas, int startX, int startY, boolean interactive, boolean lowBit)
    {
        startX += paddingLeft;
        startY -= paddingBottom;
        for(IDrawableItem item : drawableItems)
        {
            item.Draw(canvas, startX, startY, interactive, lowBit);
            startX += item.width();
        }
    }

    public int width()
    {
        int width = paddingLeft + paddingRight;
        for(IDrawableItem item : drawableItems)
            width += item.width();
        return width;
    }

    public int height()
    {
        return height + paddingBottom + paddingTop;
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

    public String GetRowString(int rowNum, String key)
    {
        return GetRowString(rowNum, key, "");
    }

    public String GetRowString(int rowNum, String key, String defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_" + key, defaultValue);
    }
}
