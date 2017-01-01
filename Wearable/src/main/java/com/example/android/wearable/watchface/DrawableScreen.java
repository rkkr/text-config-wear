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

    public DrawableScreen(Context context)
    {
        rows = new ArrayList<DrawableRow>();
        for (int i=1; i<=5; i++) //5 rows hardcoded for now
            rows.add(new DrawableRow(context, i));

        totalHeight = 0;
        for (DrawableRow row : rows)
            totalHeight += row.height();

        color = PreferenceManager.getDefaultSharedPreferences(context).getString("wallpaper_color", "Black");
    }

    public void Draw(Canvas canvas, Rect bounds, boolean isRound, boolean ambient, boolean lowBit)
    {
        if (ambient)
            canvas.drawColor(Color.BLACK);
        else
            canvas.drawColor(Color.parseColor(color));

        int startY = (bounds.height() - totalHeight) / 2;
        for (DrawableRow row : rows)
        {
            startY += row.height();
            int startX = 0;
            int roundBasel = 0;
            if (isRound) {
                int position = startY > bounds.height() / 2 ? startY : startY - row.height();
                roundBasel = (int) (bounds.width() - Math.sqrt(Math.pow(bounds.width(), 2) - Math.pow(position * 2 - bounds.width(), 2))) / 2;
            }
            switch (row.alignment) {
                case "Center":
                    startX = (bounds.width() - row.width()) / 2;
                    break;
                case "Left":
                    startX = roundBasel;
                    break;
                case "Right":
                    startX = bounds.width() - row.width() - roundBasel;
                    break;
            }

            row.Draw(canvas, startX, startY, ambient, lowBit);
        }
    }
}
