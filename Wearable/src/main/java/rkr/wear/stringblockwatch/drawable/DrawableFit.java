package rkr.wear.stringblockwatch.drawable;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

public class DrawableFit extends DrawableItemCommon {

    private String value;
    private Context context;

    public DrawableFit(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        this.context = context;
        value = GetRowItemString(rowIndex, itemIndex, "value", "Steps");
    }

    public String GetText(boolean ambient)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        switch (value) {
            case "Steps":
                return String.format(Locale.US, "%d", prefs.getInt("fit_steps", 0));
            case "Distance":
                return String.format(Locale.US, "%.0fm", prefs.getFloat("fit_distance", 0));
            case "Active time":
                int totalMinutes = prefs.getInt("fit_activity_time", 0) / 1000 / 60;
                if (totalMinutes <= 60)
                    return String.format(Locale.US, "%dmin", totalMinutes);
                else
                    return String.format(Locale.US, "%dh %dmin", totalMinutes / 60, totalMinutes % 60);
            case "Calories":
                return String.format(Locale.US, "%.0fkcal", prefs.getFloat("fit_calories", 0));
        }

        return "-";
    }
}
