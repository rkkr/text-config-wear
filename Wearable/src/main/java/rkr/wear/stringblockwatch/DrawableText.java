package rkr.wear.stringblockwatch;

import android.content.Context;

public class DrawableText extends DrawableItemCommon {

    private String text;

    public DrawableText(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        text = GetRowItemString(rowIndex, itemIndex, "text_value");
    }

    public String GetText(boolean ambient)
    {
        return text;
    }
}
