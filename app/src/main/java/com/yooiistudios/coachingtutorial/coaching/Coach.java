package com.yooiistudios.coachingtutorial.coaching;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private WeakReference<Activity> mActivityWeakReference;
    private TargetSpecs mTargetSpecs;
    private CoachCover mCoachCover;

    private Coach(WeakReference<Activity> activityWeakReference, TargetSpecs targetSpecs) {
        mActivityWeakReference = activityWeakReference;
        mTargetSpecs = targetSpecs;
    }

    public static void start(Activity activity, final TargetSpecs targetSpecs) {
        new Coach(new WeakReference<>(activity), targetSpecs).start();
    }

    private void start() {
        Activity activity = mActivityWeakReference.get();
        if (mTargetSpecs.size() == 0 || activity == null) {
            // ignore
            return;
        }

        initCoachCover(activity);
        addCoachCover(activity);

        coachNext();
    }

    private void initCoachCover(Activity activity) {
        mCoachCover = new CoachCover(activity);
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

    private void addCoachCover(Activity activity) {
        activity.addContentView(mCoachCover, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void coachNext() {
        if (!mTargetSpecs.hasNext()) {
            ViewGroup parent = (ViewGroup) mCoachCover.getParent();
            parent.removeView(mCoachCover);
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

    private void highlight(TargetSpec targetSpec) {
        // TODO: consider scale, rotate, translation
        View targetView = targetSpec.view;
        Rect tempRect = new Rect();

        targetView.getGlobalVisibleRect(tempRect);
        RectF visibleRect = new RectF(tempRect);

        mCoachCover.getGlobalVisibleRect(tempRect);
        visibleRect.offset(-tempRect.left, -tempRect.top);
//        mCoachCover.makeHoleAt(visibleRect, CoachCover.HoleType.INSCRIBE);
//        mCoachCover.makeHoleAt(visibleRect, CoachCover.HoleType.HALF_INSCRIBE);
        mCoachCover.makeHoleAt(visibleRect, CoachCover.HoleType.CIRCUMSCRIBE);
    }
}
