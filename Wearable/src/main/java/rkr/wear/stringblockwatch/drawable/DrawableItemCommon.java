package rkr.wear.stringblockwatch.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public abstract class DrawableItemCommon implements IDrawableItem {

    private Context context;
    public Paint paint;
    private int height;
    private boolean hideIdle;
    private int color;
    private int colorAmbient;
    private Set<String> font;
    private boolean forceCaps;

    public DrawableItemCommon(Context context, int rowIndex, int itemIndex)
    {
        this.context = context;
        color = GetRowItemColor(rowIndex, itemIndex, "text_color", Color.WHITE);
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
        font = GetRowItemSet(rowIndex, itemIndex, "text_font");

        paint = new Paint();
        paint.setTextSize(height);
        int textType = Typeface.NORMAL;
        if (font.contains("Bold"))
            textType += Typeface.BOLD;
        if (font.contains("Italic"))
            textType += Typeface.ITALIC;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, textType));
        forceCaps = font.contains("All Caps");
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

        paint.setAntiAlias(!lowBit || !ambient);
        paint.setColor(ambient ? (lowBit ? Color.WHITE : colorAmbient): color);
        String text = GetText(ambient);
        if (forceCaps)
            text = text.toUpperCase();
        canvas.drawText(text, startX, startY, paint);
    }

    public String GetRowItemString(int rowNum, int itemNum, String key, String defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_" + key, defaultValue);
    }

    public int GetRowItemColor(int rowNum, int itemNum, String key, int defaultValue)
    {
        String text = PreferenceManager.getDefaultSharedPreferences(context).getString("row_" + rowNum + "_item_" + itemNum + "_" + key, "");
        try {
            return (int) Long.parseLong(text.replaceFirst("0x", ""), 16);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int GetRowItemInt(int rowNum, int itemNum, String key, int defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("row_" + rowNum + "_item_" + itemNum + "_" + key, defaultValue);
    }

    public boolean GetRowItemBoolean(int rowNum, int itemNum, String key, boolean defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("row_" + rowNum + "_item_" + itemNum + "_" + key, defaultValue);
    }

    public Set<String> GetRowItemSet(int rowNum, int itemNum, String key)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet("row_" + rowNum + "_item_" + itemNum + "_" + key, new HashSet<String>());
    }
}
