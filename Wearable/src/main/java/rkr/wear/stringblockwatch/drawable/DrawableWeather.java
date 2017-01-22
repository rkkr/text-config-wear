package rkr.wear.stringblockwatch.drawable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;
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

        if (value.equals("Condition") && units.equals("Icon")) {
            Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "weathericons_regular_webfont.ttf");
            paint.setTypeface(weatherFont);
        }

    }

    public String GetText(boolean ambient)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        switch (value) {
            case "Temperature":
                if (prefs.contains("weather_temp"))
                    switch (units) {
                        case "Celsius":
                            return Integer.toString(prefs.getInt("weather_temp", 0)) + (showUnits ? "°C" : "");
                        case "Fahrenheit":
                            return Integer.toString(prefs.getInt("weather_temp", 0) * 9 / 5 + 32) + (showUnits ? "°F" : "");
                    }
                break;
            case "Condition":
                switch (units) {
                    case "Icon":
                        return toIconCode(prefs.getString("weather_icon", "-"));
                    case "Word":
                        return prefs.getString("weather_description", "-");
                }
                break;
            case "Location":
                switch (units) {
                    case "City":
                        return prefs.getString("weather_city", "-");
                    case "Coordinates":
                        if (prefs.contains("weather_lat") && prefs.contains("weather_lon")) {
                            float lat = prefs.getFloat("weather_lat", 0);
                            float lon = prefs.getFloat("weather_lon", 0);
                            return String.format(Locale.US, "Lat:%.2f Lon:%.2f", lat, lon);
                        }
                }
                break;
            case "Pressure":
                if (prefs.contains("weather_pressure"))
                    return Integer.toString(prefs.getInt("weather_pressure", 0)) + (showUnits ? "hpa" : "");
                break;
            case "Humidity":
                if (prefs.contains("weather_humidity"))
                    return Integer.toString(prefs.getInt("weather_humidity", 0)) + (showUnits ? "%" : "");
                break;
            case "Wind Speed":
                switch (units) {
                    case "Meters per second":
                        return String.format(Locale.US, "%.1f%s", prefs.getFloat("weather_wind_speed", 0), showUnits ? "m/s" : "");
                    case "Kilometers per hour":
                        return String.format(Locale.US, "%.0f%s", prefs.getFloat("weather_wind_speed", 0) * 3.6F, showUnits ? "mk/h" : "");
                    case "Miles per hour":
                        return String.format(Locale.US, "%.0f%s", prefs.getFloat("weather_wind_speed", 0) * 2.2369356F, showUnits ? "mph" : "");
                }
                break;
            case "Sunrise":
                calendar.setTimeInMillis(prefs.getLong("weather_sunrise", 0) * 1000L);
                if (prefs.contains("weather_sunrise"))
                    switch (units) {
                        case "Time (24)":
                            return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                        case "Time (12)":
                            return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
                        case "Time remaining":
                            long remaining = calendar.getTimeInMillis() - System.currentTimeMillis();
                            while (remaining < 0)
                                remaining += 1000 * 60 * 60 * 24;
                            long minutes = remaining / 1000 / 60 % 60;
                            long hours = remaining / 1000 / 60 / 60 % 24;
                            return String.format(Locale.US, "%d:%02d", hours, minutes);
                    }
                break;
            case "Sunset":
                calendar.setTimeInMillis(prefs.getLong("weather_sunset", 0) * 1000L);
                if (prefs.contains("weather_sunset"))
                    switch (units) {
                        case "Time (24)":
                            return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                        case "Time (12)":
                            return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
                        case "Time remaining":
                            long remaining = calendar.getTimeInMillis() - System.currentTimeMillis();
                            while (remaining < 0)
                                remaining += 1000 * 60 * 60 * 24;
                            long minutes = remaining / 1000 / 60 % 60;
                            long hours = remaining / 1000 / 60 / 60 % 24;
                            return String.format(Locale.US, "%d:%02d", hours, minutes);
                }
                break;
            case "Update Time":
                if (!prefs.contains("weather_update_time"))
                    return "Never";
                long updateTime = System.currentTimeMillis() - prefs.getLong("weather_update_time", 0);
                updateTime = updateTime / 1000 / 60;
                if (updateTime < 2)
                    return "Just now";
                if (updateTime < 60)
                    return updateTime + " minutes ago";
                if (updateTime < 2 * 60)
                    return "1 hour ago";
                return updateTime / 60 + " hours ago";
        }

        return "-";
    }

    private static String toIconCode(String code) {
        switch(code) {
            case "01d":
                return "\uf00d";
            case "01n":
                return "\uf02e";
            case "02d":
                return "\uf002";
            case "02n":
                return "\uf086";
            case "03d":
            case "03n":
                return "\uf041";
            case "04d":
            case "04n":
                return "\uf013";
            case "09d":
                return "\uf009";
            case "09n":
                return "\uf037";
            case "10d":
                return "\uf008";
            case "10n":
                return "\uf036";
            case "11d":
                return "\uf010";
            case "11n":
                return "\uf02d";
            case "13d":
                return "\uf00a";
            case "13n":
                return "\uf02a";
            case "50d":
                return "\uf003";
            case "50n":
                return "\uf04a";
            default:
                Log.e("DrawableWeather", "Unknown icon code: " + code);
        }
        return code;
    }
}
