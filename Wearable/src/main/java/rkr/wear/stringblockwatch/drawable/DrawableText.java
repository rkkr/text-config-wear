package rkr.wear.stringblockwatch.drawable;

import android.content.Context;

public class DrawableText extends DrawableItemCommon {

    private String text;

    public DrawableText(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        text = GetRowItemString(rowIndex, itemIndex, "value", "Text");
    }

    public String GetText(boolean ambient)
    {
        return text;
    }
}
