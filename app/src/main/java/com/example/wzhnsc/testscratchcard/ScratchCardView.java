package com.example.wzhnsc.testscratchcard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 刮刮卡视图
 */
public class ScratchCardView extends View {
    private Bitmap mBgBmp;
    private Bitmap mFgBmp;
    private Paint  mPaint;
    private Canvas mCanvas;
    private Path   mPath;

    private int      mForegroundColor = Color.GRAY;
    private Drawable mBackgroundDrawable;
    private int      mStrokeWidth = 50;

    public ScratchCardView(Context context) {
        super(context);
        init(null, 0);
    }

    public ScratchCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScratchCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a  = getContext().obtainStyledAttributes(attrs, R.styleable.ScratchCardView, defStyle, 0);
        mForegroundColor    = a.getColor(R.styleable.ScratchCardView_foregroundColor, mForegroundColor);
        mBackgroundDrawable = a.getDrawable(R.styleable.ScratchCardView_backgroundDrawable);
        mStrokeWidth        = a.getInt(R.styleable.ScratchCardView_strokeWidth, mStrokeWidth);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAlpha(0); // 画笔透明度为零
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mBgBmp = drawable2Bmp(mBackgroundDrawable);
        mFgBmp = Bitmap.createBitmap(mBgBmp.getWidth(), mBgBmp.getHeight(), Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas(mFgBmp);
        mCanvas.drawColor(mForegroundColor);

        mPath = new Path();
    }

    private Bitmap drawable2Bmp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width  = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        drawable.setBounds(0, 0, width, height);
        drawable.draw(new Canvas(bmp));

        return bmp;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
        }

        mCanvas.drawPath(mPath, mPaint);

        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBgBmp, 0, 0, null);
        canvas.drawBitmap(mFgBmp, 0, 0, null);
    }
}
