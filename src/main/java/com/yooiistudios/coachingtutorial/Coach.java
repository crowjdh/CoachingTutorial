package com.yooiistudios.coachingtutorial;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.yooiistudios.coachingtutorial.models.TargetSpec;
import com.yooiistudios.coachingtutorial.models.TargetSpecs;
import com.yooiistudios.coachingtutorial.views.HighlightCover;
import com.yooiistudios.coachingtutorial.views.SpeechBubble;

import java.lang.ref.WeakReference;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 17.
 *
 * Coach
 *  튜토리얼을 실행하는 유틸 클래스
 */
public class Coach implements HighlightCover.OnEventListener {
    private static final String TAG_COACH_COVER = "tag_coach_cover";
    private static final String TAG_SPEECH_BUBBLE = "tag_speech_bubble";
    private static final int DEFAULT_HOLE_PADDING_DP = 7;
    private static final Callback NULL_LISTENER = new NullCallback();

    private WeakReference<Activity> mActivityWeakReference;
    private TargetSpecs mTargetSpecs;
    private TargetSpec mCurrentTargetSpec;
    private HighlightCover mHighlightCover;
    private Point mMaxSpeechBubbleSize = new Point();
    private Callback mCallback;

    private Coach(Activity activity, TargetSpecs targetSpecs) {
        this(activity, targetSpecs, NULL_LISTENER);
    }

    private Coach(Activity activity, TargetSpecs targetSpecs, Callback callback) {
        mActivityWeakReference = new WeakReference<>(activity);
        mTargetSpecs = targetSpecs;
        mCallback = callback;
    }

    public static Coach start(Activity activity, final TargetSpec targetSpec) {
        TargetSpecs targetSpecs = new TargetSpecs();
        targetSpecs.add(targetSpec);
        return start(activity, targetSpecs);
    }

    public static Coach start(Activity activity, final TargetSpecs targetSpecs) {
        Coach coach = new Coach(activity, targetSpecs);
        coach.start();
        return coach;
    }

    public static Coach start(Activity activity, final TargetSpecs targetSpecs, Callback callback) {
        Coach coach = new Coach(activity, targetSpecs, callback);
        coach.start();
        return coach;
    }

    private void start() {
        boolean hasInvalidArguments = mTargetSpecs.size() == 0 || getActivity() == null;
        if (hasInvalidArguments || mHighlightCover != null) {
            // ignore
            return;
        }
        initCoachCover();
        addCoachCover();
        coachNextOnCoachCoverSizeFix();
    }

    public void proceed() {
        coachNext();
    }

    private Activity getActivity() {
        return mActivityWeakReference.get();
    }

    private void initCoachCover() {
        removeCoachCover();

        mHighlightCover = new HighlightCover(getActivity(), this);
        mHighlightCover.setBackgroundColor(Color.parseColor("#cc000000"));
        mHighlightCover.setTag(TAG_COACH_COVER);
//        mHighlightCover.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getActionMasked();
//                if (action == MotionEvent.ACTION_UP) {
//                    coachNext();
//                }
//                return true;
//            }
//        });
    }

    private void addCoachCover() {
        getActivity().addContentView(mHighlightCover, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void coachNextOnCoachCoverSizeFix() {
        mHighlightCover.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int width = mHighlightCover.getWidth();
                int height = mHighlightCover.getHeight();
                if (width > 0 && height > 0) {
                    mHighlightCover.getViewTreeObserver().removeOnPreDrawListener(this);

                    mMaxSpeechBubbleSize.x = (int) (width * 0.9);
                    mMaxSpeechBubbleSize.y = (int) (height * 0.6);

                    coachNext();
                }

                return true;
            }
        });
    }

    private void coachNext() {
        notifyCurrentHighlightDone();
        if (!mTargetSpecs.hasNext()) {
            removeCoachCover();
            mCallback.notifyDone();
            // notify
            return;
        }
        mCurrentTargetSpec = mTargetSpecs.next();
        final View targetView = mCurrentTargetSpec.views[0];
        if (targetView.getWidth() > 0 && targetView.getHeight() > 0) {
            highlight();
        } else {
            targetView.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            targetView.getViewTreeObserver().removeOnPreDrawListener(this);
                            highlight();
                            return true;
                        }
                    });
        }
    }

    private void notifyCurrentHighlightDone() {
        if (mCurrentTargetSpec != null) {
            mCallback.onDone(mCurrentTargetSpec);
        }
    }

    private void removeCoachCover() {
        if (mHighlightCover != null && mHighlightCover.getParent() != null) {
            ViewGroup parent = (ViewGroup) mHighlightCover.getParent();
            parent.removeView(mHighlightCover);
        }
    }

    private void highlight() {
        RectF holeRect = getHoleRect();
        showHole(holeRect);
        showSpeechBubble(holeRect);
    }

    @NonNull
    private RectF getHoleRect() {
        // TODO: consider scale, rotate, translation
        Rect tempRect = new Rect();
        RectF tempRectF = new RectF();

        RectF visibleRect = new RectF();
        for (View targetView : mCurrentTargetSpec.views) {
            targetView.getGlobalVisibleRect(tempRect);
            tempRectF.set(tempRect);

            visibleRect.union(tempRectF);
        }

        mHighlightCover.getGlobalVisibleRect(tempRect);
        visibleRect.offset(-tempRect.left, -tempRect.top);

        float holePaddingDp;
        if (mCurrentTargetSpec.holePaddingDp > 0) {
            holePaddingDp = mCurrentTargetSpec.holePaddingDp;
        } else {
            holePaddingDp = DEFAULT_HOLE_PADDING_DP;
        }
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float holePadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, holePaddingDp, displayMetrics);
        visibleRect.inset(-holePadding, -holePadding);
        return visibleRect;
    }

    private void showHole(RectF holeRect) {
        mHighlightCover.makeHoleAt(holeRect, mCurrentTargetSpec.holeType);
    }

    private void showSpeechBubble(final RectF holeRect) {
        final SpeechBubble bubble = addSpeechBubbleToCoachCover();

        bubble.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                bubble.getViewTreeObserver().removeOnPreDrawListener(this);
                adjustSpeechBubbleOnLayoutFix(holeRect, bubble);
                return false;
            }
        });
    }

    private void adjustSpeechBubbleOnLayoutFix(RectF holeRect, SpeechBubble bubble) {
        adjustBubbleHorizontally(holeRect, bubble);
        adjustBubbleVertically(holeRect, bubble);
    }

    @NonNull
    private SpeechBubble addSpeechBubbleToCoachCover() {
        removePreviousSpeechBubble();

        final SpeechBubble bubble = new SpeechBubble(getActivity());
        bubble.setTag(TAG_SPEECH_BUBBLE);
        bubble.setMessage(mCurrentTargetSpec.message);
        SpeechBubble.Direction direction =
                mCurrentTargetSpec.direction.verticalBias.equals(TargetSpec.Direction.VerticalBias.TOP)
                ? SpeechBubble.Direction.UPWARD : SpeechBubble.Direction.DOWNWARD;
        bubble.setDirection(direction);
        bubble.setMessageViewMaxWidth(mMaxSpeechBubbleSize.x);
        bubble.setMessageViewMaxHeight(mMaxSpeechBubbleSize.y);
        bubble.setTriangleCenterXRatio(getSpeechBubbleApexXRatio(mCurrentTargetSpec));
//        if (true) {
//            bubble.setPadding(75, 0, 15, 0);
//            bubble.setBackgroundColor(Color.CYAN);
//        }
        HighlightCover.LayoutParams lp = new HighlightCover.LayoutParams(
                HighlightCover.LayoutParams.WRAP_CONTENT,
                HighlightCover.LayoutParams.WRAP_CONTENT
        );
        mHighlightCover.addView(bubble, lp);
        return bubble;
    }

    private void removePreviousSpeechBubble() {
        View previousBubble = mHighlightCover.findViewWithTag(TAG_SPEECH_BUBBLE);
        if (previousBubble != null) {
            mHighlightCover.removeView(previousBubble);
        }
    }

    private float getSpeechBubbleApexXRatio(TargetSpec targetSpec) {
        float apexXRatio;
        switch (targetSpec.direction.horizontalBias) {
            case CENTER:
                apexXRatio = 0.5f;
                break;
            case RIGHT:
                apexXRatio = 0.2f;
                break;
            case LEFT:
            default:
                apexXRatio = 0.8f;
                break;
        }

        return apexXRatio;
    }

    private void adjustBubbleHorizontally(RectF holeRect, SpeechBubble bubble) {
        HighlightCover.LayoutParams lp = (HighlightCover.LayoutParams) bubble.getLayoutParams();
        float bubbleLeft = holeRect.centerX() - bubble.getTriangleCenterX();

//        lp.leftMargin = Math.max((int) bubbleLeft, 0);
        lp.leftMargin = (int) bubbleLeft;
        bubble.setLayoutParams(lp);
    }

    private void adjustBubbleVertically(RectF holeRect, SpeechBubble bubble) {
        HighlightCover.LayoutParams lp = (HighlightCover.LayoutParams) bubble.getLayoutParams();
        float bubbleTop;
        float holeRadius;
        switch (mCurrentTargetSpec.direction.verticalBias) {
//                case CENTER:
//                    bubbleTop = holeRect.centerY() - bubble.getHeight() / 2;
//                    break;
            case BOTTOM:
                bubbleTop = holeRect.bottom;
                switch (mCurrentTargetSpec.holeType) {
                    case CIRCLE_HALF_INSCRIBE:
                        holeRadius = Math.max(holeRect.width(), holeRect.height()) / 2;
                        bubbleTop += holeRadius - holeRect.height() / 2;
                        break;
                    case CIRCLE_CIRCUMSCRIBE:
                        holeRadius = (float) Math.hypot(holeRect.width(), holeRect.height()) / 2;
                        bubbleTop += holeRadius - holeRect.height() / 2;
                        break;
                }
                break;
            case TOP:
            default:
                bubbleTop = holeRect.top - bubble.getHeight();
                switch (mCurrentTargetSpec.holeType) {
                    case CIRCLE_HALF_INSCRIBE:
                        holeRadius = Math.max(holeRect.width(), holeRect.height()) / 2;
                        bubbleTop -= holeRadius - holeRect.height() / 2;
//                        bubbleTop -= Math.max(holeRect.width(), holeRect.height()) / 2;
                        break;
                    case CIRCLE_CIRCUMSCRIBE:
                        holeRadius = (float) Math.hypot(holeRect.width(), holeRect.height()) / 2;
                        bubbleTop -= holeRadius - holeRect.height() / 2;
                        break;
                }
                break;
        }

//        lp.topMargin = Math.max((int) bubbleTop, 0);
        lp.topMargin = (int) bubbleTop;
        bubble.setLayoutParams(lp);
    }

    @Override
    public void onClickHighlight() {
        coachNext();
    }

    public static abstract class Callback {
        private String mTag;

        public Callback(String tag) {
            mTag = tag;
        }

        private void notifyDone() {
            onAllDone(mTag);
        }

        public abstract void onDone(TargetSpec targetSpec);

        public abstract void onAllDone(String tag);
    }

    private static class NullCallback extends Callback {
        public NullCallback() {
            super("");
        }

        @Override
        public void onDone(TargetSpec ignored) { }

        @Override
        public void onAllDone(String ignored) { }
    }
}
