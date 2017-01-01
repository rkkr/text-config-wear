package com.example.android.wearable.watchface;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Calendar;
import java.util.TimeZone;

public class DrawableTime extends DrawableItemCommon {

    private String timeItem;
    private Calendar calendar;

    public DrawableTime(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        timeItem = GetRowItemString(rowIndex, itemIndex, "item");

        calendar = Calendar.getInstance();
    }

    public String GetText(boolean ambient)
    {
        long now = System.currentTimeMillis();
        calendar.setTimeInMillis(now);
        calendar.setTimeZone(TimeZone.getDefault());
        if (ambient)
            calendar.set(Calendar.SECOND, 0);

        String text = "";
        switch (timeItem)
        {
            case "Hour (12H)":
                text = Integer.toString(calendar.get(Calendar.HOUR));
                break;
            case "Hour (24H)":
                text = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
                break;
            case "AM/PM":
                text = (int)calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                break;
            case "Minute":
                text = String.format("%02d", calendar.get(Calendar.MINUTE));
                break;
            case "Second":
                text = String.format("%02d", calendar.get(Calendar.SECOND));
                break;
        }
        return text;
    }
}
