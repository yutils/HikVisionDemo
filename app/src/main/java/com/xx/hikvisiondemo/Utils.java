package com.xx.hikvisiondemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Utils {
    //向bitmap右上角中添加文字
    public static Bitmap addTextToBitmap(Bitmap bitmap, String text) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        canvas.drawText(text, width  - paint.measureText(text) - 30,  50+30, paint);
        return textBitmap;
    }
}
