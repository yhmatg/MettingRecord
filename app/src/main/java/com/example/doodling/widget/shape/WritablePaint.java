package com.example.doodling.widget.shape;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.io.Serializable;

/**
 * Created by user on 2016/10/25.
 */

public class WritablePaint extends Paint implements Serializable {

    public int mColor;
    public float mWidth;
    public boolean isEraser = false;

    public void loadPaint() {
        setAntiAlias(true);
        setColor(mColor);
        setStyle(Style.STROKE);
        setStrokeWidth(mWidth);
        setDither(true);
        setStrokeJoin(Join.ROUND);
        setStrokeCap(Cap.ROUND);
        if (isEraser) {
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            setAlpha(0);
            setStrokeWidth(100);
        }
    }

}
