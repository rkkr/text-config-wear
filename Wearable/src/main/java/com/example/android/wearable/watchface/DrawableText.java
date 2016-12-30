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

    public void Draw(Canvas canvas, int startX, int startY)
    {
        canvas.drawText(text, startX, startY, paint);
    }

    public int width()
    {
        return (int) paint.measureText(text);
    }
}
