package com.example.android.wearable.watchface;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class DrawableScreen {

    private ArrayList<DrawableRow> rows;
    private int totalHeight;
    private String color;
    private Context context;

    public DrawableScreen(Context context)
    {
        this.context = context;

        rows = new ArrayList<DrawableRow>();
        for (int i=1; i<=2; i++) //5 rows hardcoded for now
            rows.add(new DrawableRow(context, i));

        totalHeight = 0;
        for (DrawableRow row : rows)
            totalHeight += row.height;

        color = PreferenceManager.getDefaultSharedPreferences(context).getString("wallpaper_color", "Black");
    }

    public void Draw(Canvas canvas, Rect bounds)
    {
        canvas.drawColor(Color.parseColor(color));

        int startY = (bounds.height() - totalHeight) / 2;
        for (DrawableRow row : rows)
        {
            int startX = (bounds.width() - row.width()) / 2;
            row.Draw(canvas, startX, startY);
            startY += row.height;
        }
    }

    //private String GetString(String key)
    //{
    //    return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    //}
}
