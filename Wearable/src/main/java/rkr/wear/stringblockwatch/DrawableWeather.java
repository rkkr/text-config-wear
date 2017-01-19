package rkr.wear.stringblockwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

public class DrawableWeather extends DrawableItemCommon {

    private String value;
    private String units;
    private boolean showUnits;
    private Context context;
    private Calendar calendar;

    public DrawableWeather(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        this.context = context;
        value = GetRowItemString(rowIndex, itemIndex, "value", "Temperature");
        units = GetRowItemString(rowIndex, itemIndex, "units", "Celsius");
        showUnits = GetRowItemBoolean(rowIndex, itemIndex, "show_units", true);

        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
    }

    public String GetText(boolean ambient)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        switch (value) {
            case "Temperature":
                if (!prefs.contains("weather_temp"))
                    return "-";
                if (units.equals("Celsius")) {
                    if (showUnits)
                        return Integer.toString(prefs.getInt("weather_temp", 0)) + "°C";
                    else
                        return Integer.toString(prefs.getInt("weather_temp", 0));
                } else {
                    if (showUnits)
                        return Integer.toString(prefs.getInt("weather_temp", 0) * 9 / 5 + 32) + "°F";
                    else
                        return Integer.toString(prefs.getInt("weather_temp", 0) * 9 / 5 + 32);
                }
            case "Weather Description":
                return prefs.getString("weather_description", "-");
            case "Location":
                return prefs.getString("weather_city", "-");
            case "Pressure":
                if (showUnits)
                    return Integer.toString(prefs.getInt("weather_pressure", 0)) + "hpa";
                else
                    return Integer.toString(prefs.getInt("weather_pressure", 0));
            case "Humidity":
                if (showUnits)
                    return Integer.toString(prefs.getInt("weather_humidity", 0)) + "%";
                else
                    return Integer.toString(prefs.getInt("weather_humidity", 0));
            case "Wind Speed":
                if (showUnits)
                    return Float.toString(prefs.getFloat("weather_wind_speed", 0)) + "m/s";
                else
                    return Float.toString(prefs.getFloat("weather_wind_speed", 0));
            case "Sunrise":
                if (!prefs.contains("weather_sunrise"))
                    return "-";
                calendar.setTimeInMillis(prefs.getLong("weather_sunrise", 0) * 1000L);
                return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            case "Sunset":
                if (!prefs.contains("weather_sunset"))
                    return "-";
                calendar.setTimeInMillis(prefs.getLong("weather_sunset", 0) * 1000L);
                return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            case "Update Time":
                if (!prefs.contains("weather_update_time"))
                    return "Never";
                long updateTime = System.currentTimeMillis() - prefs.getLong("weather_update_time", 0);
                updateTime = updateTime / 1000 / 60;
                if (updateTime < 1)
                    return "Just now";
                if (updateTime < 60)
                    return updateTime + " minutes ago";
                return updateTime / 60 + " hours ago";
        }

        return "-";
    }

    protected static Calendar unixToCalendar(long unixTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime);
        return calendar;
    }
}
