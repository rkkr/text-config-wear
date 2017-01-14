package rkr.wear.stringblockwatch;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Calendar;

public class DrawableDate extends DrawableItemCommon {

    private String dateValue;
    private String dateFormat;
    private Calendar calendar;

    public DrawableDate(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        dateValue = GetRowItemString(rowIndex, itemIndex, "value", "Year (YYYY)");
        dateFormat = GetRowItemString(rowIndex, itemIndex, "format", "Number");

        calendar = Calendar.getInstance();
    }

    public String GetText(boolean ambient)
    {
        long now = System.currentTimeMillis();
        calendar.setTimeInMillis(now);

        switch (dateValue) {
            case "Year (YYYY)":
                return FormatNumber(calendar.get(Calendar.YEAR), 4);
            case "Year (YY)":
                return FormatNumber(calendar.get(Calendar.YEAR) % 100, 2);
            case "Month":
                return FormatMonth(calendar.get(Calendar.MONTH));
            case "Weekday":
                return FormatWeekday(calendar.get(Calendar.DAY_OF_WEEK));
            case "Day of Month":
                return FormatNumber(calendar.get(Calendar.DAY_OF_MONTH), 2);
            case "Day of Year":
                return FormatNumber(calendar.get(Calendar.DAY_OF_YEAR), 3);
            case "Week of Year":
                return FormatNumber(calendar.get(Calendar.WEEK_OF_YEAR), 2);
        }
        return "";
    }

    private String FormatMonth(int number)
    {
        switch (dateFormat) {
            case "Number":
                return String.format("%d", number + 1);
            case "Number with leading zeros":
                return String.format("%02d", number + 1);
            case "Word Full":
                return MonthToString(number);
            case "Word Short":
                return MonthToString(number).substring(0, 3);
        }
        return "";
    }

    private String FormatNumber(int number, int leadingZeros)
    {
        switch (dateFormat) {
            case "Number":
                return String.format("%d", number);
            case "Number with leading zeros":
                return String.format("%0" + leadingZeros + "d", number);
            case "Word":
                return NumberWordConverter.convert(number);
        }
        return "";
    }

    private String FormatWeekday(int number)
    {
        switch (dateFormat) {
            case "Word Full":
                return WeekdayToString(number);
            case "Word Short":
                return WeekdayToString(number).substring(0, 3);
        }
        return "";
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
