package com.example.doodling.paintType;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class BasePaint {

    protected WritablePaint mPaint;
    protected int paintColor;
    protected float startX;
    protected float startY;
    protected float stopX;
    protected float stopY;
    protected int strokeWidth;

    protected Paint paint = new Paint();

    protected BasePaint(float x, float y, int strokeWidth, int paintColor) {
        this.startX = x;
        this.startY = y;
        this.stopX = x;
        this.stopY = y;
        this.strokeWidth = strokeWidth;
        this.paintColor = paintColor;
    }

    public void touchDown(float mStartX,float mStartY){
        startX=mStartX;
        startY=mStartY;
    }
    public void touchUp(float endX,float endY){
        stopX=endX;
        stopY=endY;
    }

    public abstract void onDraw(Canvas canvas);

    public abstract void onMove(float moveX, float moveY);

    public WritablePaint getPaint(){
        mPaint.mColor = paintColor;
        mPaint.mWidth = strokeWidth;
        return mPaint;
    }

    public float getStartX(){
        return startX;
    }
    public float getStartY(){
        return startY;
    }
    public float getEndX(){return stopX;}
    public float getEndY(){return startY;}
}
