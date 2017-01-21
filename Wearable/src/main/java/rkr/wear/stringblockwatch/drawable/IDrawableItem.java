package rkr.wear.stringblockwatch.drawable;

import android.graphics.Canvas;

public interface IDrawableItem {

    void Draw(Canvas canvas, int startX, int startY, boolean ambient, boolean lowBit);
    String GetText(boolean ambient);
    int width();
    int height();
}
