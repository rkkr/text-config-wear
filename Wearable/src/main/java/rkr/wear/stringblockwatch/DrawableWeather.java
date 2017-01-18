package rkr.wear.stringblockwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DrawableWeather extends DrawableItemCommon {

    private String value;
    private Context context;

    public DrawableWeather(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        this.context = context;
        value = GetRowItemString(rowIndex, itemIndex, "value", "Temperature");
    }

    public String GetText(boolean ambient)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        switch (value) {
            case "Temperature":
                if (!prefs.contains("weather_temp"))
                    return "-";
                return Integer.toString(prefs.getInt("weather_temp", 0));
            case "Weather description":
                return prefs.getString("weather_description", "-");
            case "Location":
                return prefs.getString("weather_city", "-");
        }

        return "-";
    }
}
