package rkr.wear.stringblockwatch.drawable;

import android.content.Context;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DrawableTime extends DrawableItemCommon {

    private String timeValue;
    private String timeFormat;
    private Calendar calendar;

    public DrawableTime(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        timeValue = GetRowItemString(rowIndex, itemIndex, "value", "Hour (24H)");
        timeFormat = GetRowItemString(rowIndex, itemIndex, "format", "Number");

        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
    }

    public String GetText(boolean ambient)
    {
        long now = System.currentTimeMillis();
        calendar.setTimeInMillis(now);
        if (ambient)
            calendar.set(Calendar.SECOND, 0);

        switch (timeValue)
        {
            case "Hour (12H)":
                return FormatNumber(calendar.get(Calendar.HOUR));
            case "Hour (24H)":
                return FormatNumber(calendar.get(Calendar.HOUR_OF_DAY));
            case "AM/PM":
                return  (int)calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
            case "Minute":
                return FormatNumber(calendar.get(Calendar.MINUTE));
            case "Second":
                return FormatNumber(calendar.get(Calendar.SECOND));
        }
        return "";
    }

    private String FormatNumber(int number)
    {
        switch (timeFormat) {
            case "Number":
                return String.format(Locale.US, "%d", number);
            case "Number with leading zeros":
                return String.format(Locale.US, "%02d", number);
            case "Word":
                return NumberWordConverter.convert(number);
        }
        return "";
    }
}
