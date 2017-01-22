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
                return String.format(Locale.US, "Steps: %d", prefs.getInt("fit_steps", 0));
            case "Distance":
                return String.format(Locale.US, "Distance: %.0f", prefs.getFloat("fit_distance", 0));
        }

        return "-";
    }
}
