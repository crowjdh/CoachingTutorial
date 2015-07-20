package com.yooiistudios.coachingtutorial.coaching;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.FrameLayout;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 17.
 *
 * HighlightCover
 *  원형으로 하이라이트를 주는 커버 레이아웃
 */
public class HighlightCover extends FrameLayout {
    public enum HoleType {
        INSCRIBE,
        HALF_INSCRIBE,
        CIRCUMSCRIBE
    }

    private static final int BG_COLOR = Color.parseColor("#bb000000");
    private static final boolean DEBUG = true;

    private int mBackgroundColor;
    private Paint mBackgroundPaint = new Paint();
    private RectF mDrawBound = new RectF();
    private RectF mHoleRect = new RectF();
    private Path mInverseHolePath = new Path();
    private HoleType mHoleType;

    // Debug
    private Paint mDebugRectPaint = new Paint();

    public HighlightCover(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setBackgroundColor(BG_COLOR);
        mBackgroundPaint.setAntiAlias(true);
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
            float radius;
            switch (mHoleType) {
                case INSCRIBE:
                    radius = Math.min(mHoleRect.width(), mHoleRect.height()) / 2;
                    break;
                case HALF_INSCRIBE:
                    radius = Math.max(mHoleRect.width(), mHoleRect.height()) / 2;
                    break;
                case CIRCUMSCRIBE:
                default:
                    radius = (float) (Math.hypot(mHoleRect.width(), mHoleRect.height()) / 2);
                    break;
            }
            mInverseHolePath.reset();

            mInverseHolePath.addRect(mDrawBound, Path.Direction.CW);
            mInverseHolePath.addCircle(mHoleRect.centerX(), mHoleRect.centerY(), radius, Path.Direction.CW);
            mInverseHolePath.setFillType(Path.FillType.EVEN_ODD);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mInverseHolePath.isEmpty()) {
            canvas.drawPath(mInverseHolePath, mBackgroundPaint);

            // Debug
            if (DEBUG) {
                mDebugRectPaint.setColor(Color.RED);
                mDebugRectPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(mHoleRect, mDebugRectPaint);
            }
        } else {
            canvas.drawColor(mBackgroundColor);
        }
    }
}
