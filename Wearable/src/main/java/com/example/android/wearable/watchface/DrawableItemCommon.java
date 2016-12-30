package com.example.android.wearable.watchface;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

public abstract class DrawableItemCommon implements IDrawableItem {

    private Context context;
    public Paint paint;
    private int height;

    public DrawableItemCommon(Context context, int rowIndex, int itemIndex)
    {
        this.context = context;
        String color = GetRowItemString(rowIndex, itemIndex, "text_color");
        String size = GetRowItemString(rowIndex, itemIndex, "text_size");
        height = Integer.parseInt(size);

        paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setAntiAlias(true);
        paint.setTextSize(height);
    }

    public int height()
    {
        return height;
    }

    public String GetRowItemString(int rowNum, int itemNum, String key)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_" + key, "");
    }
}
