package com.example.android.wearable.watchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

public class DrawableText {

    private Context context;
    Paint paint;
    int height;
    String text;

    public DrawableText(Context context, int rowIndex, int itemIndex)
    {
        this.context = context;
        text = GetRowItemString(rowIndex, itemIndex, "text_value");
        String color = GetRowItemString(rowIndex, itemIndex, "text_color");
        String size = GetRowItemString(rowIndex, itemIndex, "text_size").substring(0, 2);
        height = Integer.parseInt(size);

        paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setAntiAlias(true);
        paint.setTextSize(height);
    }

    public void Draw(Canvas canvas, int startX, int startY)
    {
        canvas.drawText(text, startX, startY, paint);
    }

    public int width()
    {
        return (int) paint.measureText(text);
    }

    private String GetRowItemString(int rowNum, int itemNum, String key)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_" + key, "");
    }
}
