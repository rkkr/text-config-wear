package rkr.wear.stringblockwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class DrawableScreen {

    private ArrayList<DrawableRow> drawableRows;
    private int totalHeight;
    private String color;
    private Context context;

    public DrawableScreen(Context context)
    {
        this.context = context;
        drawableRows = new ArrayList<DrawableRow>();
        ArrayList<Integer> rows = GetRows();
        for (Integer row : rows)
            drawableRows.add(new DrawableRow(context, row));

        totalHeight = 0;
        for (DrawableRow drawableRow : drawableRows)
            totalHeight += drawableRow.height();

        color = PreferenceManager.getDefaultSharedPreferences(context).getString("wallpaper_color", "Black");
    }

    public void Draw(Canvas canvas, Rect bounds, boolean isRound, boolean ambient, boolean lowBit)
    {
        if (ambient)
            canvas.drawColor(Color.BLACK);
        else
            canvas.drawColor(Color.parseColor(color));

        int startY = (bounds.height() - totalHeight) / 2;
        for (DrawableRow drawableRow : drawableRows)
        {
            startY += drawableRow.height();
            int startX = 0;
            int roundBasel = 0;
            if (isRound) {
                int position = startY > bounds.height() / 2 ? startY : startY - drawableRow.height();
                roundBasel = (int) (bounds.width() - Math.sqrt(Math.pow(bounds.width(), 2) - Math.pow(position * 2 - bounds.width(), 2))) / 2;
            }
            switch (drawableRow.alignment) {
                case "Center":
                    startX = (bounds.width() - drawableRow.width()) / 2;
                    break;
                case "Left":
                    startX = roundBasel;
                    break;
                case "Right":
                    startX = bounds.width() - drawableRow.width() - roundBasel;
                    break;
            }

            drawableRow.Draw(canvas, startX, startY, ambient, lowBit);
        }
    }

    private ArrayList<Integer> GetRows()
    {
        String rowItems = PreferenceManager.getDefaultSharedPreferences(context).getString("rows", "");
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (!rowItems.equals(""))
            for (String item: rowItems.split(","))
                list.add(Integer.parseInt(item));
        return list;
    }
}
