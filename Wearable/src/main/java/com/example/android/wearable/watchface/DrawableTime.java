package com.example.android.wearable.watchface;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Calendar;

public class DrawableTime extends DrawableItemCommon {

    private String timeItem;
    private Calendar calendar;

    public DrawableTime(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        timeItem = GetRowItemString(rowIndex, itemIndex, "item");

        calendar = Calendar.getInstance();
    }

    public String GetText()
    {
        long now = System.currentTimeMillis();
        calendar.setTimeInMillis(now);
        //if (mAmbient)
        //    mCalendar.set(Calendar.SECOND, 0);

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
                text = Integer.toString(calendar.get(Calendar.MINUTE));
                break;
            case "Second":
                text = Integer.toString(calendar.get(Calendar.SECOND));
                break;
        }
        return text;
    }
}
