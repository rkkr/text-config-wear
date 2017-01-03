package rkr.wear.stringblockwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

public abstract class DrawableItemCommon implements IDrawableItem {

    private Context context;
    public Paint paint;
    private int height;
    private boolean hideIdle;
    private int color;
    private int colorAmbient;

    public DrawableItemCommon(Context context, int rowIndex, int itemIndex)
    {
        this.context = context;
        color = Color.parseColor(GetRowItemString(rowIndex, itemIndex, "text_color", "White"));
        switch (PreferenceManager.getDefaultSharedPreferences(context).getString("idle_mode_color", "White")) {
            case "White":
                colorAmbient = Color.WHITE;
                break;
            case "Color":
                colorAmbient = color;
                break;
            case "Gray Scale":
                int avg = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
                colorAmbient = Color.rgb(avg, avg, avg);
                break;
        }
        hideIdle = GetRowItemBoolean(rowIndex, itemIndex, "hide_idle", false);
        height = Integer.parseInt(GetRowItemString(rowIndex, itemIndex, "text_size", "40"));

        paint = new Paint();
        paint.setTextSize(height);
    }

    public int height()
    {
        return height;
    }

    public int width()
    {
        return (int) paint.measureText(GetText(true));
    }

    public void Draw(Canvas canvas, int startX, int startY, boolean ambient, boolean lowBit)
    {
        if (ambient && hideIdle)
            return;

        paint.setAntiAlias(lowBit && ambient);
        paint.setColor(ambient ? colorAmbient: color);
        canvas.drawText(GetText(ambient), startX, startY, paint);
    }

    public String GetRowItemString(int rowNum, int itemNum, String key, String defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_" + key, defaultValue);
    }

    public int GetRowItemInt(int rowNum, int itemNum, String key, int defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("row_" + rowNum + "_item_" + itemNum + "_" + key, defaultValue);
    }

    public boolean GetRowItemBoolean(int rowNum, int itemNum, String key, boolean defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("row_" + rowNum + "_item_" + itemNum + "_" + key, defaultValue);
    }
}
