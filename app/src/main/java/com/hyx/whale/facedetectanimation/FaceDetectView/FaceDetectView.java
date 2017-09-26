package com.hyx.whale.facedetectanimation.FaceDetectView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hyx.whale.facedetectanimation.R;


/**
 * funtion :
 * author  :smallbluewhale.
 * date    :2017/9/21.
 * version :1.0.
 */

/*
* 三角形颜色是不变的，圆形的线框颜色从蓝色变成白色
* 动画总共有四个过程
* 1.整体先稍微变小
* 2.开始转圈圈
* 3.整体变小，并且颜色变成白色，然后开始显示白色
* 4.逆时针转动
* */
public class FaceDetectView extends SurfaceView implements SurfaceHolder.Callback {
    private float radiu = 100;                                  //圆的半径
    private Point circleCenterPoint;                            //圆心位置
    private Point rectanglePoint1 , rectanglePoint2;                               //三角形的中心点位置
    private float rectangleWidth;                               //三角形的边长


    private int startAngle;                              //起点的角度
    private float nameSize;                                     //名字字体大小
    private int   drawableID = R.mipmap.ic_launcher;            //动画完成后，显示的图片
    private int   duration = 500;                               //动画时间
    /*
    * 圆的部分
    * */
    private Paint circlePaint;          //圆的画笔
    private Paint rectanglePaint;       //两个三角形的画笔
    private RectF rectF;                  //外接圆矩形
    private int rectLeft, rectTop, rectRight, rectBottom;
    private FaceDetectThread faceDetectThread;

    private Path linePath;

    private SurfaceHolder mSurfaceHolder;   //绘图的canvas
    private Canvas canvas;
    private boolean isAnimationEnd;

    public FaceDetectView(Context context) {
        super(context);
    }

    public FaceDetectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceDetectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initData();
        initView();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置长宽
        setMeasuredDimension(getFaceDetectViewWidth(widthMeasureSpec), getFaceDetectViewHeight(heightMeasureSpec));
    }


    private int getFaceDetectViewHeight(int heightMeasureSpec) {
        int height = 0;
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (int) (3 * radiu);
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }
        return height;
    }

    private int getFaceDetectViewWidth(int widthMeasureSpec) {
        int width = 0;
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else {
            width = (int) (3 * radiu);
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return width;
    }

    private void initPaint() {
        //圆形画笔
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStrokeWidth(2);


        linePath = new Path();
        rectanglePaint = new Paint();
        rectanglePaint.setAntiAlias(true);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setStrokeWidth(10);
        rectanglePaint.setColor(Color.YELLOW);
    }

    public FaceDetectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    private void initView() {
        mSurfaceHolder = getHolder();
        //绑定回调方法
        mSurfaceHolder.addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);      //保持屏幕不休眠
    }

    private void initData() {
        faceDetectThread = new FaceDetectThread();
        circleCenterPoint = new Point();
        rectanglePoint1 = new Point();
        rectanglePoint2 = new Point();
        startAngle = 210;
        circleCenterPoint.x = (int)radiu * 3 / 2;
        circleCenterPoint.y = (int)radiu * 3 / 2;
        rectF = new RectF(-radiu + circleCenterPoint.x, -radiu + circleCenterPoint.y, radiu + circleCenterPoint.x, radiu + circleCenterPoint.y);
        caculateRectanglePosition();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (faceDetectThread == null) {
            faceDetectThread = new FaceDetectThread();
        }
        faceDetectThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (faceDetectThread != null) {
            faceDetectThread.isRunning = false;
            faceDetectThread = null;
        }
    }


    private class FaceDetectThread extends Thread {
        private boolean isRunning = true;

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                draw();
                /* 动画结束之后主动结束这个动画，这样性能会好点*/
                if (isAnimationEnd) {
                    faceDetectThread = null;
                    isRunning = false;
                }
            }
        }
    }

    private void draw() {
        try {
            synchronized (mSurfaceHolder) {
                canvas = mSurfaceHolder.lockCanvas();
                drawArc(canvas);
            }
        } catch (Exception e) {

        } finally {
            if (canvas != null) {
                //结束之后销毁这个view
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawArc(Canvas canvas) {
        canvas.drawArc(rectF, startAngle + 10, 160, false, circlePaint);
        canvas.drawArc(rectF, startAngle + 160 + 30, 160, false, circlePaint);
        canvas.drawPoint(rectanglePoint1.x , rectanglePoint1.y , rectanglePaint);
        canvas.drawPoint(rectanglePoint2.x , rectanglePoint2.y  , rectanglePaint);
//        linePath.moveTo();
    }

    /*
     * 根据三角形角度以及圆心坐标计算出两个三角形中心坐标
     * */
    private void caculateRectanglePosition() {
        rectanglePoint1.x = (int)( circleCenterPoint.x + Math.cos(startAngle * Math.PI  / 180) * radiu);
        //y坐标和我们平时的坐标反过来
        rectanglePoint1.y = (int)( circleCenterPoint.y + Math.sin(startAngle * Math.PI  / 180) * radiu);
        rectanglePoint2.x = (int)( circleCenterPoint.x + Math.cos((startAngle + 180) * Math.PI  / 180) * radiu);
        //y坐标和我们平时的坐标反过来
        rectanglePoint2.y = (int)( circleCenterPoint.y + Math.sin((startAngle + 180) * Math.PI  / 180) * radiu);

    }
}
