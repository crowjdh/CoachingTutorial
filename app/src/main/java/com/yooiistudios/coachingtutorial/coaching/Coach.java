package com.yooiistudios.coachingtutorial.coaching;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 17.
 *
 * Coach
 *  튜토리얼을 실행하는 유틸 클래스
 */
public class Coach {
    private static final String TAG_COACH_COVER = "tag_coach_cover";
    private static final String TAG_SPEECH_BUBBLE = "tag_speech_bubble";
    private WeakReference<Activity> mActivityWeakReference;
    private TargetSpecs mTargetSpecs;
    private HighlightCover mHighlightCover;
    private Point mMaxSpeechBubbleSize = new Point();

    private Coach(WeakReference<Activity> activityWeakReference, TargetSpecs targetSpecs) {
        mActivityWeakReference = activityWeakReference;
        mTargetSpecs = targetSpecs;
    }

    public static void start(Activity activity, final TargetSpecs targetSpecs) {
        new Coach(new WeakReference<>(activity), targetSpecs).start();
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

    private Activity getActivity() {
        return mActivityWeakReference.get();
    }

    private void initCoachCover() {
        removeCoachCover();

        mHighlightCover = new HighlightCover(getActivity());
        mHighlightCover.setBackgroundColor(Color.parseColor("#cc000000"));
        mHighlightCover.setTag(TAG_COACH_COVER);
        mHighlightCover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_UP) {
                    coachNext();
                }
                return true;
            }
        });
    }

    private void addCoachCover() {
        getActivity().addContentView(mHighlightCover, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void coachNextOnCoachCoverSizeFix() {
        mHighlightCover.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mHighlightCover.getViewTreeObserver().removeOnPreDrawListener(this);

                mMaxSpeechBubbleSize.x = (int) (mHighlightCover.getWidth() * 0.5);
                mMaxSpeechBubbleSize.y = (int) (mHighlightCover.getWidth() * 0.6);

                coachNext();
                return true;
            }
        });
    }

    private void coachNext() {
        if (!mTargetSpecs.hasNext()) {
            removeCoachCover();
            return;
        }
        final TargetSpec targetSpec = mTargetSpecs.next();
        View targetView = targetSpec.view;
        if (targetView.getWidth() > 0 && targetView.getHeight() > 0) {
            highlight(targetSpec);
        } else {
            targetView.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            targetSpec.view.getViewTreeObserver().removeOnPreDrawListener(this);
                            highlight(targetSpec);
                            return true;
                        }
                    });
        }
    }

    private void removeCoachCover() {
        if (mHighlightCover != null && mHighlightCover.getParent() != null) {
            ViewGroup parent = (ViewGroup) mHighlightCover.getParent();
            parent.removeView(mHighlightCover);
        }
    }

    private void highlight(TargetSpec targetSpec) {
        RectF holeRect = getHoleRect(targetSpec);
        showHole(targetSpec, holeRect);
        showSpeechBubble(targetSpec, holeRect);
    }

    @NonNull
    private RectF getHoleRect(TargetSpec targetSpec) {
        // TODO: consider scale, rotate, translation
        View targetView = targetSpec.view;
        Rect tempRect = new Rect();

        targetView.getGlobalVisibleRect(tempRect);
        RectF visibleRect = new RectF(tempRect);

        mHighlightCover.getGlobalVisibleRect(tempRect);
        visibleRect.offset(-tempRect.left, -tempRect.top);
        return visibleRect;
    }

    private void showHole(TargetSpec targetSpec, RectF holeRect) {
        mHighlightCover.makeHoleAt(holeRect, targetSpec.holeType);
    }

    private void showSpeechBubble(final TargetSpec targetSpec, final RectF holeRect) {
        final SpeechBubble bubble = addSpeechBubbleToCoachCover(targetSpec);

        bubble.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                bubble.getViewTreeObserver().removeOnPreDrawListener(this);
                adjustSpeechBubbleOnLayoutFix(targetSpec, holeRect, bubble);
                return false;
            }
        });
    }

    private void adjustSpeechBubbleOnLayoutFix(TargetSpec targetSpec, RectF holeRect, SpeechBubble bubble) {
        adjustBubbleHorizontally(holeRect, bubble);
        adjustBubbleVertically(targetSpec, holeRect, bubble);
    }

    @NonNull
    private SpeechBubble addSpeechBubbleToCoachCover(TargetSpec targetSpec) {
        removePreviousSpeechBubble();

        final SpeechBubble bubble = new SpeechBubble(getActivity());
        bubble.setTag(TAG_SPEECH_BUBBLE);
        bubble.setMessage(targetSpec.message);
        SpeechBubble.Direction direction =
                targetSpec.direction.verticalBias.equals(TargetSpec.Direction.VerticalBias.TOP)
                ? SpeechBubble.Direction.UPWARD : SpeechBubble.Direction.DOWNWARD;
        bubble.setDirection(direction);
        bubble.setMessageViewMaxWidth(mMaxSpeechBubbleSize.x);
        bubble.setMessageViewMaxHeight(mMaxSpeechBubbleSize.y);
        bubble.setTriangleCenterXRatio(getSpeechBubbleApexXRatio(targetSpec));
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

    private void adjustBubbleVertically(TargetSpec targetSpec, RectF holeRect, SpeechBubble bubble) {
        HighlightCover.LayoutParams lp = (HighlightCover.LayoutParams) bubble.getLayoutParams();
        float bubbleTop;
        float holeRadius;
        switch (targetSpec.direction.verticalBias) {
//                case CENTER:
//                    bubbleTop = holeRect.centerY() - bubble.getHeight() / 2;
//                    break;
            case BOTTOM:
                bubbleTop = holeRect.bottom;
                switch (targetSpec.holeType) {
                    case HALF_INSCRIBE:
                        holeRadius = Math.max(holeRect.width(), holeRect.height()) / 2;
                        bubbleTop += holeRadius - holeRect.height() / 2;
                        break;
                    case CIRCUMSCRIBE:
                        holeRadius = (float) Math.hypot(holeRect.width(), holeRect.height()) / 2;
                        bubbleTop += holeRadius - holeRect.height() / 2;
                        break;
                }
                break;
            case TOP:
            default:
                bubbleTop = holeRect.top - bubble.getHeight();
                switch (targetSpec.holeType) {
                    case HALF_INSCRIBE:
                        holeRadius = Math.max(holeRect.width(), holeRect.height()) / 2;
                        bubbleTop -= holeRadius - holeRect.height() / 2;
//                        bubbleTop -= Math.max(holeRect.width(), holeRect.height()) / 2;
                        break;
                    case CIRCUMSCRIBE:
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
}
