package com.example.android.wearable.watchface;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Calendar;

public class DrawableDate extends DrawableItemCommon {

    private String timeItem;
    private Calendar calendar;

    public DrawableDate(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        timeItem = GetRowItemString(rowIndex, itemIndex, "item");

        calendar = Calendar.getInstance();
    }

    public String GetText()
    {
        long now = System.currentTimeMillis();
        calendar.setTimeInMillis(now);

        String text = "";
        switch (timeItem) {
            case "Year (YYYY)":
                text = Integer.toString(calendar.get(Calendar.YEAR));
                break;
            case "Year (YY)":
                text = Integer.toString(calendar.get(Calendar.YEAR) % 100);
                break;
            case "Month":
                text = Integer.toString(calendar.get(Calendar.MONTH));
                break;
            case "Day of Month":
                text = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
                break;
            case "Weekday":
                text = WeekdayToString(calendar.get(Calendar.DAY_OF_WEEK));
                break;
        }
        return text;
    }

    private String WeekdayToString(int weekday)
    {
        switch (weekday) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
        }
        return "";
    }
}
