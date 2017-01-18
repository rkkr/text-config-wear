package rkr.wear.stringblockwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class DrawableScreen {

    private ArrayList<DrawableRow> drawableRows;
    private int totalHeight;
    private int color;
    private Context context;

    public DrawableScreen(Context context)
    {
        if (!PreferenceManager.getDefaultSharedPreferences(context).contains("rows")) {
            //We are started for the first time or app settings have been cleared
            ImportWatch(context.getResources().openRawResource(R.raw.watch_sample1), context);
        }

        this.context = context;
        drawableRows = new ArrayList<DrawableRow>();
        ArrayList<Integer> rows = GetRows();
        for (Integer row : rows)
            drawableRows.add(new DrawableRow(context, row));

        totalHeight = 0;
        for (DrawableRow drawableRow : drawableRows)
            totalHeight += drawableRow.height();

        color = GetScreenColor(Color.BLACK);
    }

    public void Draw(Canvas canvas, Rect bounds, boolean isRound, boolean ambient, boolean lowBit)
    {
        if (ambient)
            canvas.drawColor(Color.BLACK);
        else
            canvas.drawColor(color);

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

    public int GetScreenColor(int defaultValue)
    {
        String text = PreferenceManager.getDefaultSharedPreferences(context).getString("wallpaper_color", "");
        try {
            return (int) Long.parseLong(text.replaceFirst("0x", ""), 16);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean NeedsLocationAccess() {
        for (DrawableRow row : drawableRows)
            for (IDrawableItem item : row.drawableItems)
                if (item instanceof DrawableWeather)
                    return true;
        return false;
    }

    private void ImportWatch(InputStream inputStream, Context context)
    {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();

        try {
            for (Reader in = new InputStreamReader(inputStream, "UTF-8");; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }

            String contents = out.toString();
            JSONObject jObject = new JSONObject(contents);

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.clear();

            for (Iterator<String> key = jObject.keys(); key.hasNext(); ) {
                String _key = key.next();
                Object value = jObject.get(_key);

                if (value instanceof JSONArray) {
                    JSONArray array = (JSONArray)value;
                    HashSet<String> _value = new HashSet<String>();
                    for (int i=0; i<array.length(); i++)
                        _value.add(array.getString(i));
                    editor.putStringSet(_key, _value);
                } else {
                    editor.putString(_key, (String) value);
                }
            }

            editor.commit();
            //Intent intent = new Intent("string.block.watch.FORCE_SYNC");
            //context.sendBroadcast(intent);
        }
        catch (UnsupportedEncodingException e) {
            //Toast.makeText(context, "Failed to read file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //Toast.makeText(context, "Failed to read file", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            //Toast.makeText(context, "Failed to parse file", Toast.LENGTH_SHORT).show();
        }

    }
}
