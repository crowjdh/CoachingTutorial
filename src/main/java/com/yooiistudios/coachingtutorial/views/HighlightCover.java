package com.yooiistudios.coachingtutorial.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.yooiistudios.coachingtutorial.BuildConfig;
import com.yooiistudios.coachingtutorial.DebugSettings;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 17.
 *
 * HighlightCover
 *  원형으로 하이라이트를 주는 커버 레이아웃
 */
public class HighlightCover extends FrameLayout {
    public interface OnEventListener {
        void onClickHighlight();
    }
    public enum HoleType {
        CIRCLE_INSCRIBE,
        CIRCLE_HALF_INSCRIBE,
        CIRCLE_CIRCUMSCRIBE,
        CAPSULE_INSCRIBE
    }

    private static final int BG_COLOR = Color.parseColor("#99000000");

    private int mBackgroundColor;
    private Paint mBackgroundPaint = new Paint();
    private RectF mDrawBound = new RectF();
    private RectF mHoleRect = new RectF();
    private Path mInverseHolePath = new Path();
    private HoleType mHoleType;

    private OnEventListener mOnEventListener;
    private TouchRegion mStartTouchRegion;
    private boolean mHasTouchRegionChanged = false;

    // Debug
    private Paint mDebugRectPaint = new Paint();

    public HighlightCover(Context context, OnEventListener listener) {
        super(context);
        init(listener);
    }

    private void init(OnEventListener listener) {
        setWillNotDraw(false);
        setBackgroundColor(BG_COLOR);
        mBackgroundPaint.setAntiAlias(true);
        mOnEventListener = listener;
    }

    public void makeHoleAt(final RectF holeRect, HoleType holeType) {
        mHoleRect = holeRect;
        mHoleType = holeType;
        updateInverseHolePath();
        invalidate();
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();

        int width = w - widthPadding;
        int height = h - heightPadding;

        updateDrawBound(width, height);
    }

    private void updateDrawBound(int width, int height) {
        mDrawBound = new RectF(
                getPaddingLeft(), getPaddingTop(),
                getPaddingLeft() + width, getPaddingTop() + height
        );
    }

    private void updateInverseHolePath() {
        if (!mHoleRect.isEmpty()) {
            mInverseHolePath.reset();
            mInverseHolePath.addRect(mDrawBound, Path.Direction.CW);

            float radius;
            switch (mHoleType) {
                case CIRCLE_INSCRIBE:
                case CAPSULE_INSCRIBE:
                    radius = Math.min(mHoleRect.width(), mHoleRect.height()) / 2;
                    break;
                case CIRCLE_HALF_INSCRIBE:
                    radius = Math.max(mHoleRect.width(), mHoleRect.height()) / 2;
                    break;
                case CIRCLE_CIRCUMSCRIBE:
                default:
                    radius = (float) (Math.hypot(mHoleRect.width(), mHoleRect.height()) / 2);
                    break;
            }
            if (mHoleType.equals(HoleType.CAPSULE_INSCRIBE)) {
                RectF leftCapRect = new RectF(
                        mHoleRect.left,
                        mHoleRect.top,
                        mHoleRect.left + 2 * radius,
                        mHoleRect.bottom
                );
                RectF rightCapRect = new RectF(
                        mHoleRect.right - 2 * radius,
                        mHoleRect.top,
                        mHoleRect.right,
                        mHoleRect.bottom
                );
                RectF innerRect = new RectF(
                        mHoleRect.left + radius,
                        mHoleRect.top,
                        mHoleRect.right - radius,
                        mHoleRect.bottom
                );
                mInverseHolePath.moveTo(innerRect.left, innerRect.top);

                mInverseHolePath.lineTo(innerRect.right, innerRect.top);
                mInverseHolePath.arcTo(rightCapRect, 270, 180);
                mInverseHolePath.lineTo(innerRect.left, innerRect.bottom);
                mInverseHolePath.arcTo(leftCapRect, 90, 180);
            } else {
                mInverseHolePath.addCircle(mHoleRect.centerX(), mHoleRect.centerY(), radius, Path.Direction.CW);
            }
            mInverseHolePath.setFillType(Path.FillType.EVEN_ODD);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mInverseHolePath.isEmpty()) {
            canvas.drawPath(mInverseHolePath, mBackgroundPaint);

            // Debug
            if (DebugSettings.isDebug()) {
                mDebugRectPaint.setColor(Color.RED);
                mDebugRectPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(mHoleRect, mDebugRectPaint);
            }
        } else {
            canvas.drawColor(mBackgroundColor);
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        Log.i("coachingtutorial", "onInterceptTouchEvent");
////        return determineConsume(event);
//        return super.onInterceptTouchEvent(event);
//    }
//
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        Log.i("coachingtutorial", "onTouchEvent");
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            mOnEventListener.onClickHighlight();
        }
//        return !isInHole;
//        return true;
        return determineConsume(event);
//        return super.onTouchEvent(event);
    }

//    private boolean determineNextHighlight(MotionEvent event) {
//        boolean showNextHighlight = false;
//        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
//            showNextHighlight = mStartTouchRegion.isIn() && !mHasTouchRegionChanged;
//        }
//
//        return showNextHighlight;
//    }

    private boolean determineConsume(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (BuildConfig.DEBUG_MODE) {
            Log.i("coachingtutorial", "x: " + x + ", y: " + y);
            Log.i("coachingtutorial", "holeRect: " + mHoleRect.toShortString());
            Log.i("coachingtutorial", "contains: " + mHoleRect.contains(x, y));
        }

        boolean isInHole = mHoleRect.contains(x, y);
//        boolean consume = true;

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartTouchRegion = TouchRegion.fromBoolean(isInHole);
                mHasTouchRegionChanged = false;
//                        consume = !isInHole;
                break;
            case MotionEvent.ACTION_MOVE:
                TouchRegion touchRegion = TouchRegion.fromBoolean(isInHole);
                mHasTouchRegionChanged = !touchRegion.equals(mStartTouchRegion);
                break;
        }
        boolean consume = mStartTouchRegion.equals(TouchRegion.OUT) || mHasTouchRegionChanged;
        if (BuildConfig.DEBUG_MODE) {
            Log.i("coachingtutorial", "consume: " + consume);
        }

        return consume;
    }

    private enum TouchRegion {
        IN, OUT;

        public boolean isIn() {
            return this.equals(IN);
        }

        public boolean isOut() {
            return !isIn();
        }

        private static TouchRegion fromBoolean(boolean isInHole) {
            return isInHole ? TouchRegion.IN : TouchRegion.OUT;
        }
    }
}
