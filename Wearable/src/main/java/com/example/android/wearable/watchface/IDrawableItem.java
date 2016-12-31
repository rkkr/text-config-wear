package com.example.android.wearable.watchface;


import android.graphics.Canvas;

public interface IDrawableItem {

    void Draw(Canvas canvas, int startX, int startY);
    String GetText();
    int width();
    int height();
}
