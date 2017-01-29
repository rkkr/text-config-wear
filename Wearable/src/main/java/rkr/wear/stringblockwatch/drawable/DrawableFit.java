package rkr.wear.stringblockwatch.drawable;

import android.content.Context;

import java.util.Locale;

import rkr.wear.stringblockwatch.DigitalWatchFaceService;

public class DrawableFit extends DrawableItemCommon {

    private String value;

    public DrawableFit(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        value = GetRowItemString(rowIndex, itemIndex, "value", "Steps");
    }

    public String GetText(boolean ambient)
    {
        switch (value) {
            case "Steps":
                return String.format(Locale.US, "%d", DigitalWatchFaceService.fitSteps);
            case "Distance":
                return String.format(Locale.US, "%.0fm", DigitalWatchFaceService.fitDistance);
            case "Active time":
                int totalMinutes = DigitalWatchFaceService.fitActivityTime / 1000 / 60;
                if (totalMinutes <= 60)
                    return String.format(Locale.US, "%dmin", totalMinutes);
                else
                    return String.format(Locale.US, "%dh %dmin", totalMinutes / 60, totalMinutes % 60);
            case "Calories":
                return String.format(Locale.US, "%.0fkcal", DigitalWatchFaceService.fitCalories);
        }

        return "-";
    }
}
