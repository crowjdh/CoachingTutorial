package com.yooiistudios.coachingtutorial.coaching;

import android.app.Activity;
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
 * description
 */
public class Coach {
    private static final String TAG_COACH_COVER = "tag_coach_cover";
    private static final String TAG_SPEECH_BUBBLE = "tag_speech_bubble";
    private WeakReference<Activity> mActivityWeakReference;
    private TargetSpecs mTargetSpecs;
    private CoachCover mCoachCover;
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
        if (hasInvalidArguments || mCoachCover != null) {
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

        mCoachCover = new CoachCover(getActivity());
        mCoachCover.setTag(TAG_COACH_COVER);
        mCoachCover.setOnTouchListener(new View.OnTouchListener() {
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
        getActivity().addContentView(mCoachCover, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void coachNextOnCoachCoverSizeFix() {
        mCoachCover.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mCoachCover.getViewTreeObserver().removeOnPreDrawListener(this);

                mMaxSpeechBubbleSize.x = (int) (mCoachCover.getWidth() * 0.5);
                mMaxSpeechBubbleSize.y = (int) (mCoachCover.getWidth() * 0.6);

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
        if (mCoachCover != null && mCoachCover.getParent() != null) {
            ViewGroup parent = (ViewGroup) mCoachCover.getParent();
            parent.removeView(mCoachCover);
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

        mCoachCover.getGlobalVisibleRect(tempRect);
        visibleRect.offset(-tempRect.left, -tempRect.top);
        return visibleRect;
    }

    private void showHole(TargetSpec targetSpec, RectF holeRect) {
        mCoachCover.makeHoleAt(holeRect, targetSpec.holeType);
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
        CoachCover.LayoutParams lp = new CoachCover.LayoutParams(
                CoachCover.LayoutParams.WRAP_CONTENT,
                CoachCover.LayoutParams.WRAP_CONTENT
        );
        mCoachCover.addView(bubble, lp);
        return bubble;
    }

    private void removePreviousSpeechBubble() {
        View previousBubble = mCoachCover.findViewWithTag(TAG_SPEECH_BUBBLE);
        if (previousBubble != null) {
            mCoachCover.removeView(previousBubble);
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
        CoachCover.LayoutParams lp = (CoachCover.LayoutParams) bubble.getLayoutParams();
        float bubbleLeft = holeRect.centerX() - bubble.getTriangleCenterX();

        lp.leftMargin = Math.max((int) bubbleLeft, 0);
        bubble.setLayoutParams(lp);
    }

    private void adjustBubbleVertically(TargetSpec targetSpec, RectF holeRect, SpeechBubble bubble) {
        CoachCover.LayoutParams lp = (CoachCover.LayoutParams) bubble.getLayoutParams();
        float bubbleTop;
        switch (targetSpec.direction.verticalBias) {
//                case CENTER:
//                    bubbleTop = holeRect.centerY() - bubble.getHeight() / 2;
//                    break;
            case BOTTOM:
                bubbleTop = holeRect.bottom;
                break;
            case TOP:
            default:
                bubbleTop = holeRect.top - bubble.getHeight();
                break;
        }

        lp.topMargin = Math.max((int) bubbleTop, 0);
        bubble.setLayoutParams(lp);
    }
}
