package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class CustomGauge extends View {

	private static final int DEFAULT_LONG_POINTER_SIZE = 1;
	
	private Paint mPaint;
	private float mStrokeWidth;
	private int mStrokeColor;
	private RectF mRect;
	private String mStrokeCap;
	private float mStartAngel;
	private float mSweepAngel;
	private float mStartValue;
	private float mEndValue;
	private float mValue;
    private String mTitle = "";
    private float mTitleSize;
	private double mPointAngel;
	private float mRectLeft;
	private float mRectTop;
	private float mRectRight;
	private float mRectBottom;
	private float mPoint;
	private int mPointColor;
	private int mPointSize;
	private int mPointStartColor;
	private int mPointEndColor;
    private int preferedSize = 200;

    private int mPadding = 20;


    private Paint titlePaint;


    private GestureDetector mDetector;

	public CustomGauge(Context context) {
		super(context);
		init();
	}
	public CustomGauge(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomGauge, 0, 0);
		
		// stroke style
		mStrokeWidth = a.getDimension(R.styleable.CustomGauge_strokeWidth, 10);
		mStrokeColor = a.getColor(R.styleable.CustomGauge_strokeColor, getResources().getColor(android.R.color.darker_gray));
		mStrokeCap = a.getString(R.styleable.CustomGauge_strokeCap);
		
		// angel start and sweep (opposite direction 0, 270, 180, 90)
		mStartAngel = a.getInt(R.styleable.CustomGauge_startAngel, 0);
		mSweepAngel = a.getInt(R.styleable.CustomGauge_sweepAngel, 360);
		
		// scale (from mStartValue to mEndValue)
		mStartValue = a.getInt(R.styleable.CustomGauge_startValue, 0);
		mEndValue = a.getInt(R.styleable.CustomGauge_endValue, 100);
		
		// pointer size and color
		mPointSize = a.getInt(R.styleable.CustomGauge_pointSize, 0);
		mPointStartColor = a.getColor(R.styleable.CustomGauge_pointStartColor, getResources().getColor(android.R.color.white));
		mPointEndColor = a.getColor(R.styleable.CustomGauge_pointEndColor, getResources().getColor(android.R.color.white));
		
		// calculating one point sweep
		mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));


        mTitle = a.getString(R.styleable.CustomGauge_titleValue);
        if(mTitle == null){
            mTitle = "TEST";
        }

        mTitleSize = a.getDimension(R.styleable.CustomGauge_titleSize, 14);

		a.recycle();
		init();
	}

	
	private void init() {
		//main Paint
		mPaint = new Paint();
	    mPaint.setColor(mStrokeColor);
	    mPaint.setStrokeWidth(mStrokeWidth);
	    mPaint.setAntiAlias(true);
	    if (!TextUtils.isEmpty(mStrokeCap)) {
	    	if (mStrokeCap.equals("BUTT"))
	    			mPaint.setStrokeCap(Paint.Cap.BUTT);
	    	else if (mStrokeCap.equals("ROUND"))
	    		mPaint.setStrokeCap(Paint.Cap.ROUND);
	    } else
	    	mPaint.setStrokeCap(Paint.Cap.BUTT);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mRect = new RectF();
	    
	    mValue = mStartValue;
	    mPoint = mStartAngel;



        titlePaint = new Paint();
        titlePaint.setColor(mStrokeColor);
        titlePaint.setAntiAlias(true);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(mTitleSize);
        titlePaint.setTextScaleX(1.0f);


        class mListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        }
        mDetector = new GestureDetector(CustomGauge.this.getContext(), new mListener());
	}


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                result = false;
            }
        }
        return result;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.d("CustomG", "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
        Log.d("CustomG", "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);

        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return preferedSize;
    }


	@Override
	protected void onDraw(Canvas canvas) {

        Log.d("CustomG", "Width: " + getWidth());
        Log.d("CustomG", "Height: " + getHeight());

		super.onDraw(canvas);

		float paddingLeft = getPaddingLeft();
		float paddingRight= getPaddingRight();
		float paddingTop = getPaddingTop();
		float paddingBottom = getPaddingBottom();
		float width = getWidth() - (paddingLeft+paddingRight);
		float height = getHeight() - (paddingTop+paddingBottom);
		float radius = (width > height ? width/2 : height/2);



	    
		mRectLeft = width/2 - radius + paddingLeft;
		mRectTop = height/2 - radius + paddingTop;
		mRectRight = width/2 - radius + paddingLeft + width;
		mRectBottom = height/2 - radius + paddingTop + height;

		
		mRect.set(mRectLeft, mRectTop, mRectRight, mRectBottom);



        canvas.drawText(mTitle, (mRectLeft + mRectRight) / 2, (mRectTop + mRectBottom) / 2 + 5.0f, titlePaint);

	    
		mPaint.setColor(mStrokeColor);
		mPaint.setShader(null);
		canvas.drawArc(mRect, mStartAngel, mSweepAngel, false, mPaint);
		mPaint.setColor(mPointStartColor);

        int colorArray[] = {mPointEndColor, mPointStartColor};
        float pointArray[] = {0.2f, 0.6f};

		mPaint.setShader(new LinearGradient(0, 0 , getWidth(), getHeight(), colorArray, pointArray, Shader.TileMode.CLAMP));
		if (mPointSize>0) {//if size of pointer is defined
			if (mPoint > mStartAngel + mPointSize/2) {
				canvas.drawArc(mRect, mPoint - mPointSize/2, mPointSize, false, mPaint);
			}
			else { //to avoid excedding start/zero point
				canvas.drawArc(mRect, mPoint, mPointSize, false, mPaint);
			}
		}
		else { //draw from start point to value point (long pointer)
			if (mValue==mStartValue) //use non-zero default value for start point (to avoid lack of pointer for start/zero value)
				canvas.drawArc(mRect, mStartAngel, DEFAULT_LONG_POINTER_SIZE, false, mPaint);
			else
				canvas.drawArc(mRect, mStartAngel, mPoint - mStartAngel, false, mPaint);
		}



	    
	}

    public void update(){
        invalidate();
    }

	public void setValue(float value) {
        if(value > mEndValue){
            mValue = mEndValue;
        }
        else if(value < mStartValue){
            mValue = mStartValue;
        }
        else {
            mValue = value;
        }
		mPoint = (float)(mStartAngel + (mValue-mStartValue) * mPointAngel);
		//invalidate();
	}
	
	public float getValue() {
		return mValue;
	}

    public void setInitialMaxValue(float max){
        mEndValue = max;
        // calculating one point sweep
        mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));
    }

    public void setMaxValue(float max){
        mEndValue = Math.max(max, mEndValue);
        // calculating one point sweep
        mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));
    }

    public float getMaxValue() {
        return mEndValue;
    }

    public void setInitialMinValue(float min){
        mStartValue = min;
        // calculating one point sweep
        mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));
    }

    public void setMinValue(float min){
        mStartValue = Math.min(min, mStartValue);
        // calculating one point sweep
        mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));
    }

    public float getMinValue() {
        return mStartValue;
    }


    public void setTitle(String title){
        mTitle = title;
    }



    void setStrokeWidth(int width){
        mStrokeWidth = width;
    }

    void setStrokeColor(int color){
        mStrokeColor = color;
    }

    void setStrokeCap(String cap){
        mStrokeCap = cap;
    }

    void setStartAngle(int angle){
        mStartAngel = angle;
    }

    void setSweepAngel(int angle){
        mSweepAngel = angle;
        // calculating one point sweep
        mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));
    }


    void setPointSize(int size){
        mPointSize = size;
    }

    void setPointStartColor(int color){
        mPointStartColor = color;
    }

    void setPointEndColor(int color){
        mPointEndColor = color;
    }

    void setViewPadding(int pad){
        mPadding = pad;
    }



}
