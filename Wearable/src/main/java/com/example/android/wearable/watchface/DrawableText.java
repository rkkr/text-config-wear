package com.example.android.wearable.watchface;

import android.content.Context;
import android.graphics.Canvas;

public class DrawableText extends DrawableItemCommon {

    private String text;

    public DrawableText(Context context, int rowIndex, int itemIndex)
    {
        super(context, rowIndex, itemIndex);
        text = GetRowItemString(rowIndex, itemIndex, "text_value");
    }

    public String GetText()
    {
        return text;
    }
}
