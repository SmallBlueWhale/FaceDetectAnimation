package com.hyx.whale.facedetectanimation.FaceDetectView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import com.hyx.whale.facedetectanimation.R;

import java.util.ArrayList;


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
public class FaceDetectView extends SurfaceView implements SurfaceHolder.Callback, Animator.AnimatorListener {
    private float radiu;                                  //圆的半径
    private static final float CIRCLERADIU = 200;                                  //圆的半径
    private Point circleCenterPoint;                            //圆心位置
    private Point rectanglePoint1, rectanglePoint2;             //三角形的中心点位置
    private Point tanglePoint1, tanglePoint2, tanglePoint3;
    private Point tanglePoint4, tanglePoint5, tanglePoint6;
    private float rectangleWidth;                               //三角形的边长


    private int startAngle;                              //起点的角度
    private float nameSize;                                     //名字字体大小
    private int drawableID = R.mipmap.ic_launcher;            //动画完成后，显示的图片
    private int duration = 500;                               //动画时间
    /*
    * 圆的部分
    * */
    private Paint circlePaint;          //圆的画笔
    private Paint rectanglePaint;       //两个三角形的画笔
    private RectF rectF;                  //外接圆矩形
    private int rectLeft, rectTop, rectRight, rectBottom;

    private Path rectanglePath1;
    private Path rectanglePath2;


    private ObjectAnimator bcRotateAnimation;

    private AnimatorSet bcAnimatorSet;
    private AnimatorSet bcAnimatorSet1;
    private ObjectAnimator bcScaleAnimator1;
    private ObjectAnimator bcScaleAnimator2;
    private ObjectAnimator bcScaleAnimator3;
    private ObjectAnimator bcScaleAnimator4;


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
        initView();
        initData();
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
        radiu = CIRCLERADIU;
        rectF = new RectF();
        rectanglePath1 = new Path();
        rectanglePath2 = new Path();
        circleCenterPoint = new Point();
        rectanglePoint1 = new Point();
        rectanglePoint2 = new Point();
        tanglePoint1 = new Point();
        tanglePoint2 = new Point();
        tanglePoint3 = new Point();
        tanglePoint4 = new Point();
        tanglePoint5 = new Point();
        tanglePoint6 = new Point();
        circleCenterPoint.x = (int) radiu * 3 / 2;
        circleCenterPoint.y = (int) radiu * 3 / 2;

        initArcData();


        if (null == bcScaleAnimator1) {
            bcScaleAnimator1 = ObjectAnimator.ofFloat(this, "radiu", 0f, 1.04f);
            bcScaleAnimator1.setDuration(200);
            bcScaleAnimator1.setInterpolator(new LinearInterpolator());
        }

        if (null == bcScaleAnimator2) {
            bcScaleAnimator2 = ObjectAnimator.ofFloat(this, "radiu", 1.04f, 1f);
            bcScaleAnimator2.setDuration(66);
            bcScaleAnimator2.setInterpolator(new LinearInterpolator());
        }

        if (null == bcScaleAnimator3) {
            bcScaleAnimator3 = ObjectAnimator.ofFloat(this, "radiu", 1f, 1.02f);
            bcScaleAnimator3.setDuration(66);
            bcScaleAnimator3.setInterpolator(new LinearInterpolator());
        }

        if (null == bcScaleAnimator4) {
            bcScaleAnimator4 = ObjectAnimator.ofFloat(this, "radiu", 1.02f, 1f);
            bcScaleAnimator4.setDuration(66);
            bcScaleAnimator4.setInterpolator(new LinearInterpolator());
        }
        if(null == bcAnimatorSet){
            bcAnimatorSet = new AnimatorSet();
            ArrayList<Animator> animatorArrayList = new ArrayList<Animator>();
            animatorArrayList.add(bcScaleAnimator1);
            animatorArrayList.add(bcScaleAnimator2);
            animatorArrayList.add(bcScaleAnimator3);
            animatorArrayList.add(bcScaleAnimator4);
            bcAnimatorSet.playSequentially(animatorArrayList);
        }

        if (null == bcRotateAnimation) {
            bcRotateAnimation = ObjectAnimator.ofInt(this, "startAngle", 0, 360);
            bcRotateAnimation.setDuration(866);
            bcRotateAnimation.setInterpolator(new LinearInterpolator());
        }

        if(null == bcAnimatorSet1){
            bcAnimatorSet1 = new AnimatorSet();
            ArrayList<Animator> animatorArrayList = new ArrayList<Animator>();
            animatorArrayList.add(bcRotateAnimation);
            bcAnimatorSet1.playSequentially(animatorArrayList);
            bcAnimatorSet1.setStartDelay(800);
        }
    }


    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        draw();
    }

    public void setRadiu(float radiu) {
        this.radiu = CIRCLERADIU * radiu;
        draw();
    }

    private void initArcData() {
        rectanglePoint1 = caculateRectanglePosition(circleCenterPoint, startAngle, radiu);
        rectanglePoint2 = caculateRectanglePosition(circleCenterPoint, startAngle + 180, radiu);

        tanglePoint1 = caculateRectanglePosition(rectanglePoint1, startAngle + 180, 50);
        tanglePoint2 = caculateRectanglePosition(rectanglePoint1, startAngle + 120 + 180, 50);
        tanglePoint3 = caculateRectanglePosition(rectanglePoint1, startAngle + 240 + 180, 50);
        rectanglePath1.reset();
        rectanglePath1.moveTo(tanglePoint1.x, tanglePoint1.y);
        rectanglePath1.lineTo(tanglePoint2.x, tanglePoint2.y);
        rectanglePath1.lineTo(tanglePoint3.x, tanglePoint3.y);
        rectanglePath1.close();

        tanglePoint4 = caculateRectanglePosition(rectanglePoint2, startAngle, 50);
        tanglePoint5 = caculateRectanglePosition(rectanglePoint2, startAngle + 120, 50);
        tanglePoint6 = caculateRectanglePosition(rectanglePoint2, startAngle + 240, 50);
        rectanglePath2.reset();
        rectanglePath2.moveTo(tanglePoint4.x, tanglePoint4.y);
        rectanglePath2.lineTo(tanglePoint5.x, tanglePoint5.y);
        rectanglePath2.lineTo(tanglePoint6.x, tanglePoint6.y);
        rectanglePath2.close();

        rectF.set(-radiu + circleCenterPoint.x, -radiu + circleCenterPoint.y, radiu + circleCenterPoint.x, radiu + circleCenterPoint.y);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (bcAnimatorSet != null && bcAnimatorSet1 != null) {
            bcAnimatorSet.start();
            bcAnimatorSet1.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (bcRotateAnimation != null) {
            bcRotateAnimation.end();
        }
    }


    private void draw() {
        try {
            synchronized (mSurfaceHolder) {
                canvas = mSurfaceHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    Log.e("TAG" , "radiu:" + radiu);
                    Log.e("TAG" , "startAngle:" + startAngle);
                    initArcData();
                    drawArc(canvas);
                }
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
        canvas.drawPoint(rectanglePoint1.x, rectanglePoint1.y, rectanglePaint);
        canvas.drawPoint(rectanglePoint2.x, rectanglePoint2.y, rectanglePaint);
        canvas.drawPath(rectanglePath1, rectanglePaint);
        canvas.drawPath(rectanglePath2, rectanglePaint);
        Log.e("drawArc", "drawArc: " + startAngle);
    }

    /*
     * 根据三角形角度以及圆心坐标计算出两个三角形中心坐标
     * */
    private Point caculateRectanglePosition(Point centerPoint, int angle, float circleRadiu) {
        Point point = new Point();
        point.x = (int) (centerPoint.x + Math.cos(angle * Math.PI / 180) * circleRadiu);
        //y坐标和我们平时的坐标反过来
        point.y = (int) (centerPoint.y + Math.sin(angle * Math.PI / 180) * circleRadiu);
        return point;
    }


    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {

    }

    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {

    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
