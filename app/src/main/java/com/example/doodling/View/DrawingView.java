package com.example.doodling.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.doodling.paintType.ActionType;
import com.example.doodling.paintType.BasePaint;
import com.example.doodling.paintType.CirclePaint;
import com.example.doodling.paintType.LinePaint;
import com.example.doodling.paintType.PathPaint;
import com.example.doodling.paintType.RectPaint;
import com.example.doodling.paintType.ShapeResource;
import com.example.doodling.paintType.Type;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder = null;
    private Paint paint;//画笔
    private BasePaint mPaint = null;
    private int paintColor = Color.BLACK;//画笔颜色
    private int paintStroke = 5;//画笔大小
    private boolean erase = false;//橡皮擦
    private Bitmap bitmap;//位图
    private List<BasePaint> mBasePaint;//绘图轨迹
    private ActionType  mActionType = ActionType.Path;
    private Bitmap oldBitmap;

    private static final int MAX_CACHE_STEP = 50;

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        initPaint();
    }

    private void init() {
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        this.setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }


    private void initPaint() {
        paint = new Paint();

        paint.setColor(paintColor);
        paint.setStrokeWidth(paintStroke);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        surfaceHolder.unlockCanvasAndPost(canvas);
        mBasePaint = new ArrayList<>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setAction(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                //20191029 start
                if (oldBitmap != null) {
                    canvas.drawBitmap(oldBitmap, 0, 0, new Paint());
                }
                //20191029 start
                for (BasePaint basePaint : mBasePaint) {
                    basePaint.onDraw(canvas);
                }
                mPaint.onMove(x, y);
                mPaint.onDraw(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mBasePaint.add(mPaint);
                if (mActionType != null) {
                    SaveDrawPath();
                }
                mPaint = null;
                break;

            default:
                break;
        }
        return true;
    }

    private void SaveDrawPath() {

//        if (mBasePaint == null) {
//            mBasePaint = new ArrayList<>(MAX_CACHE_STEP);
//        } else if (mBasePaint.size() == MAX_CACHE_STEP) {
//            mBasePaint.remove(0);
//        }
//        Paint cachePaint = new Paint(paint);
//        PathDrawingInfo info = new PathDrawingInfo();
//        info.path = cachePath;
//        info.paint = cachePaint;
//        mBasePaint.add(info);
//        erase = true;
    }

    //每次ACTION_UP事件保存路径参数
    public void saveShapeResource(ShapeResource resource,int type){
        resource.mType = type;
        resource.mStartX = (mPaint).getStartX();
        resource.mStartY = (mPaint).getStartY();
        resource.mEndX = ( mPaint).getEndX();
        resource.mEndY = ( mPaint).getEndY();
        resource.mPaint = mPaint.getPaint();
    }

    /**
     * 设置画笔颜色
     * @param color
     */
    public void setColor(String color){
        this.paintColor=Color.parseColor(color);
        paint.setColor(paintColor);

    }

    /**
     * 设置画笔大小
     * @param stoke
     */
    public void setStoke(int stoke){
        this.paintStroke=stoke;
    }

    /**
     * 画笔形状
     * @param type
     */
    public void setType(ActionType type){
        this.mActionType=type;
    }

    public void setErase(boolean mErase){
        this.erase=mErase;
        if(erase){
            int mColor=Color.WHITE;
            paintColor=mColor;
        }else {
            paintColor=paint.getColor();
            paint.setXfermode(null);
        }
    }

    //获取当前涂鸦画
    public Bitmap getBitmap(Bitmap mBitmap){
        oldBitmap=mBitmap;
        Bitmap newbmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);//结合后的bitmap
        bitmap=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);//背景图上增加的涂鸦
        Canvas canvas=new Canvas(newbmp);//创建画布
        canvas.drawColor(Color.TRANSPARENT);

        if(mBitmap!=null) {
            canvas.drawBitmap(mBitmap, 0, 0, paint);//背景bitmap
        }
        for(BasePaint action:mBasePaint){
            action.onDraw(canvas);
        }
        canvas.drawBitmap(bitmap,0,0,paint);//新增涂鸦的bitmap
        return newbmp;
    }

    public byte[] BitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    /**
     * 得到当前画笔的类型，并进行实例化
     *
     * @param x
     * @param y
     */
    private void setAction(float x, float y) {
        switch (mActionType) {
            case Path:
                mPaint = new PathPaint(x, y, paintStroke, paintColor);
                break;
            case Line:
                mPaint = new LinePaint(x, y, paintStroke, paintColor);
                break;
            case Rect:
                mPaint = new RectPaint(x, y, paintStroke, paintColor);
                break;
            case Circle:
                mPaint = new CirclePaint(x, y, paintStroke, paintColor);
                break;
            default:
                break;
        }
    }

    //清除
    public void reset(){
        if(mBasePaint!=null&& mBasePaint.size()>0){
            mBasePaint.clear();
            Canvas canvas=surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for(BasePaint action:mBasePaint){
                action.onDraw(canvas);
            }
            //20191029 start
            //oldBitmap = null;
            //20191029 end
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    //20191029 start
//    Bitmap oldBitmap;
//    public void drawOldBitmap(Bitmap bitmap){
//        reset();
//        Canvas canvas=surfaceHolder.lockCanvas();
//        canvas.drawBitmap(bitmap,0,0,new Paint());
//        oldBitmap = bitmap;
//        surfaceHolder.unlockCanvasAndPost(canvas);
//    }
    //20191029 end

}
