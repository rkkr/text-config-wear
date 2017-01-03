package rkr.wear.stringblockwatch;

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

    public String GetText(boolean ambient)
    {
        long now = System.currentTimeMillis();
        calendar.setTimeInMillis(now);

        String text = "";
        switch (timeItem) {
            case "Year (YYYY)":
                text = String.format("%d", calendar.get(Calendar.YEAR));
                break;
            case "Year (YY)":
                text = String.format("%02d", calendar.get(Calendar.YEAR) % 100);
                break;
            case "Month (Number)":
                text = Integer.toString(calendar.get(Calendar.MONTH) + 1);
                break;
            case "Month (Text)":
                text = MonthToString(calendar.get(Calendar.MONTH));
                break;
            case "Month (Text Short)":
                text = MonthToString(calendar.get(Calendar.MONTH)).substring(0, 3);
                break;
            case "Day of Month":
                text = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
                break;
            case "Weekday":
                text = WeekdayToString(calendar.get(Calendar.DAY_OF_WEEK));
                break;
            case "Weekday (Short)":
                text = WeekdayToString(calendar.get(Calendar.DAY_OF_WEEK)).substring(0, 3);
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

    private String MonthToString(int month)
    {
        switch (month) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
        }
        return "";
    }
}
