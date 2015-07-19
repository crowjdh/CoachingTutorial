package com.yooiistudios.coachingtutorial.coaching;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 16.
 *
 * SpeechBubble
 *  말풍선 뷰
 */
public class SpeechBubble extends View {
    private static final int STROKE_COLOR = Color.parseColor("#22CCCCCC");
//    private static final int STROKE_COLOR = Color.CYAN;
    private static final int BG_COLOR = Color.WHITE;
    private static final float STROKE_WIDTH_DP = 1.f;
    private static final int ROUND_RECT_RADIUS_DP = 3;
    private static final int TRIANGLE_WIDTH = 14;
    private static final int TRIANGLE_HEIGHT = 13;

    private Paint mBackgroundPaint = new Paint();
    private Paint mStrokePaint = new Paint();
    private RectF mDrawBound = new RectF();
    private Path mOutline = new Path();

    private float mRectRoundRadius;
    private float mStrokeWidth;
    private float mHalfOfTriangleWidth;
    private float mTriangleHeight;
    private float mTriangleCenterX;

    public SpeechBubble(Context context) {
        super(context);
        init();
    }

    public SpeechBubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeechBubble(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpeechBubble(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
//        ViewCompat.setElevation(this, 80);
        initSizes();
        initPaints();
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
    }

    private void initPaints() {
        mBackgroundPaint.setColor(BG_COLOR);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mStrokePaint.setColor(STROKE_COLOR);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();

        int width = w - widthPadding;
        int height = h - heightPadding;

        mTriangleCenterX = getPaddingLeft() + width / 2;
        setDrawBound(width, height);
        setOutlinePath();
    }

    private void setDrawBound(int width, int height) {
        mDrawBound = new RectF(
                getPaddingLeft(), getPaddingTop(),
                getPaddingLeft() + width, getPaddingTop() + height
        );
    }

    private void setOutlinePath() {
        RectF triangleBoundRect = new RectF(
                mTriangleCenterX - mHalfOfTriangleWidth,
                mDrawBound.bottom - mTriangleHeight,
                mTriangleCenterX + mHalfOfTriangleWidth,
                mDrawBound.bottom
        );

        RectF messageRect = new RectF(mDrawBound);
        messageRect.bottom -= triangleBoundRect.height();
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

        mOutline.moveTo(messageRect.left, messageInnerRect.top);
        mOutline.arcTo(leftTopArcRect, 180, 90);
        mOutline.lineTo(messageInnerRect.right, messageRect.top);
        mOutline.arcTo(rightTopArcRect, 270, 90);
        mOutline.lineTo(messageRect.right, messageInnerRect.bottom);
        mOutline.arcTo(rightBottomArcRect, 0, 90);

        // triangle
        mOutline.lineTo(triangleBoundRect.right, triangleBoundRect.top);
        mOutline.lineTo(triangleBoundRect.centerX(), triangleBoundRect.bottom);
        mOutline.lineTo(triangleBoundRect.left, triangleBoundRect.top);
        mOutline.lineTo(messageInnerRect.left, messageRect.bottom);

        mOutline.arcTo(leftBottomArcRect, 90, 90);
        mOutline.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mOutline, mBackgroundPaint);
        canvas.drawPath(mOutline, mStrokePaint);
    }
}
