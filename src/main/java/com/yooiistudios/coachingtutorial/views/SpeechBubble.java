package com.yooiistudios.coachingtutorial.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yooiistudios.coachingtutorial.DebugSettings;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 16.
 *
 * SpeechBubble
 *  말풍선 뷰
 */
public class SpeechBubble extends FrameLayout {
    public enum Direction { UPWARD, DOWNWARD }

    private static final int STROKE_COLOR = Color.parseColor("#22CCCCCC");
    private static final int BG_COLOR = Color.WHITE;
    private static final float STROKE_WIDTH_DP = 1.f;
    private static final int ROUND_RECT_RADIUS_DP = 3;
    private static final int TRIANGLE_WIDTH = 14;
    private static final int TRIANGLE_HEIGHT = 13;
    private static final int DEFAULT_MESSAGE_PADDING = 8;

    private Paint mBackgroundPaint = new Paint();
    private Paint mStrokePaint = new Paint();
    private RectF mDrawBound = new RectF();
    private Path mOutline = new Path();
    private TextView mMessageView;

    private float mRectRoundRadius;
    private float mStrokeWidth;
    private float mHalfOfTriangleWidth;
    private float mTriangleHeight;
    private float mTriangleCenterXRatio = 0.5f;
    private float mTriangleCenterX;
    private Direction mDirection = Direction.UPWARD;
    private int mMessagePadding;

    public SpeechBubble(Context context) {
        super(context);
        init();
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
        calculateOutlinePathWhenPossible();
        refreshMessageViewMargins();
        invalidate();
    }

    public void setTriangleCenterXRatio(float ratio) {
        mTriangleCenterXRatio = ratio;
        calculateOutlinePathWhenPossible();
        invalidate();
    }

    /**
     * Returns current center x of the triangle. If you want to get this value after
     * the method {@link #setTriangleCenterXRatio(float ratio) setTriangleCenterXRatio},
     * you must wait for layout invalidates.
     * @return center x coordinate of triangle
     */
    public float getTriangleCenterX() {
        return mTriangleCenterX;
    }

    public void setMessage(CharSequence message) {
        mMessageView.setText(message);
    }

    public void setMessageColor(int color) {
        mMessageView.setTextColor(color);
    }

    public void setMessagePadding(int padding) {
        mMessagePadding = padding;
        refreshMessageViewMargins();
    }

    public void setMessageViewMaxWidth(int width) {
        mMessageView.setMaxWidth(width);
    }

    public void setMessageViewMaxHeight(int height) {
        mMessageView.setMaxHeight(height);
    }

    private void init() {
//        ViewCompat.setElevation(this, 80);
        if (DebugSettings.isDebug()) {
            setBackgroundColor(Color.CYAN);
        }
        setWillNotDraw(false);
        initSizes();
        initPaints();
        initMessageView();

        setMinimumWidth((int) (mHalfOfTriangleWidth * 3));
    }

    private void initSizes() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mStrokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH_DP, displayMetrics);
        mRectRoundRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, ROUND_RECT_RADIUS_DP, displayMetrics);
        float triangleWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TRIANGLE_WIDTH, displayMetrics);
        mHalfOfTriangleWidth = triangleWidth / 2;
        mTriangleHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TRIANGLE_HEIGHT, displayMetrics);
        mMessagePadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MESSAGE_PADDING, displayMetrics);
    }

    private void initPaints() {
        mBackgroundPaint.setColor(BG_COLOR);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);

        mStrokePaint.setColor(STROKE_COLOR);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
    }

    private void initMessageView() {
        mMessageView = new TextView(getContext());
        if (DebugSettings.isDebug()) {
            mMessageView.setBackgroundColor(Color.RED);
            mMessageView.setText("qwerasdf");
        }
//        applyMessagePadding();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mMessageView.setLayoutParams(lp);
        refreshMessageViewMargins();
        addView(mMessageView);
    }

//    private void applyMessagePadding() {
//        mMessageView.setPadding(mMessagePadding, mMessagePadding, mMessagePadding, mMessagePadding);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();

        int width = w - widthPadding;
        int height = h - heightPadding;

        setDrawBound(width, height);
        refreshMessageViewMargins();
        calculateOutlinePath(width);
    }

    private void calculateOutlinePathWhenPossible() {
        if (getWidth() > 0) {
            int widthPadding = getPaddingLeft() + getPaddingRight();
            int width = getWidth() - widthPadding;
            calculateOutlinePath(width);
        }
    }

    private void calculateOutlinePath(int width) {
        mTriangleCenterX = calculateTriangleCenterX(width);
        setOutlinePath();
    }

    private float calculateTriangleCenterX(int width) {
        return getPaddingLeft() + width * mTriangleCenterXRatio;
    }

    private void setDrawBound(int width, int height) {
        mDrawBound = new RectF(
                getPaddingLeft(), getPaddingTop(),
                getPaddingLeft() + width, getPaddingTop() + height
        );
    }

    private void setOutlinePath() {
        RectF triangleBoundRect = new RectF();
        RectF messageRect = new RectF(mDrawBound);

        switch (mDirection) {
            case DOWNWARD:
                triangleBoundRect.set(
                        mTriangleCenterX - mHalfOfTriangleWidth,
                        mDrawBound.top,
                        mTriangleCenterX + mHalfOfTriangleWidth,
                        mDrawBound.top + mTriangleHeight
                );
                messageRect.top += triangleBoundRect.height();
                break;
            case UPWARD:
            default:
                triangleBoundRect.set(
                        mTriangleCenterX - mHalfOfTriangleWidth,
                        mDrawBound.bottom - mTriangleHeight,
                        mTriangleCenterX + mHalfOfTriangleWidth,
                        mDrawBound.bottom
                );
                messageRect.bottom -= triangleBoundRect.height();
                break;
        }
//        RectF triangleBoundRect = new RectF(
//                mTriangleCenterX - mHalfOfTriangleWidth,
//                mDrawBound.bottom - mTriangleHeight,
//                mTriangleCenterX + mHalfOfTriangleWidth,
//                mDrawBound.bottom
//        );
        RectF messageInnerRect = new RectF(messageRect);
        messageInnerRect.inset(mRectRoundRadius, mRectRoundRadius);
        RectF ovalInboundRect = new RectF(messageInnerRect);
        ovalInboundRect.inset(mRectRoundRadius, mRectRoundRadius);

        RectF leftTopArcRect = new RectF(
                messageRect.left, messageRect.top, ovalInboundRect.left, ovalInboundRect.top);
        RectF rightTopArcRect = new RectF(
                ovalInboundRect.right, messageRect.top, messageRect.right, ovalInboundRect.top);
        RectF rightBottomArcRect = new RectF(
                ovalInboundRect.right, ovalInboundRect.bottom, messageRect.right, messageRect.bottom);
        RectF leftBottomArcRect = new RectF(
                messageRect.left, ovalInboundRect.bottom, ovalInboundRect.left, messageRect.bottom);

        mOutline.reset();
        mOutline.moveTo(messageRect.left, messageInnerRect.top);
        mOutline.arcTo(leftTopArcRect, 180, 90);
        // triangle(on downward)
        if (mDirection.equals(Direction.DOWNWARD)) {
            mOutline.lineTo(triangleBoundRect.left, triangleBoundRect.bottom);
            mOutline.lineTo(triangleBoundRect.centerX(), triangleBoundRect.top);
            mOutline.lineTo(triangleBoundRect.right, triangleBoundRect.bottom);
        }
        mOutline.lineTo(messageInnerRect.right, messageRect.top);
        mOutline.arcTo(rightTopArcRect, 270, 90);
        mOutline.lineTo(messageRect.right, messageInnerRect.bottom);
        mOutline.arcTo(rightBottomArcRect, 0, 90);
        // triangle(on upward)
        if (mDirection.equals(Direction.UPWARD)) {
            mOutline.lineTo(triangleBoundRect.right, triangleBoundRect.top);
            mOutline.lineTo(triangleBoundRect.centerX(), triangleBoundRect.bottom);
            mOutline.lineTo(triangleBoundRect.left, triangleBoundRect.top);
        }
        mOutline.lineTo(messageInnerRect.left, messageRect.bottom);
        mOutline.arcTo(leftBottomArcRect, 90, 90);
        mOutline.close();
    }

    private void refreshMessageViewMargins() {
        LayoutParams lp = (LayoutParams)mMessageView.getLayoutParams();
        int radius = (int) mRectRoundRadius + mMessagePadding;
        if (mDirection.equals(Direction.UPWARD)) {
            lp.setMargins(radius, radius, radius, radius + (int) mTriangleHeight);
        } else {
            lp.setMargins(radius, radius + (int) mTriangleHeight, radius, radius);
        }
        mMessageView.setLayoutParams(lp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mOutline, mBackgroundPaint);
        canvas.drawPath(mOutline, mStrokePaint);
    }
}
