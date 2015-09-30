package com.yooiistudios.coachingtutorial.models;

import android.support.annotation.NonNull;
import android.view.View;

import com.yooiistudios.coachingtutorial.views.HighlightCover;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 17.
 *
 * TargetSpec
 *  코치할 타겟의 스펙
 */
public class TargetSpec {
    public enum Direction {
        TOP_LEFT(VerticalBias.TOP, HorizontalBias.LEFT),
        TOP(VerticalBias.TOP, HorizontalBias.CENTER),
        TOP_RIGHT(VerticalBias.TOP, HorizontalBias.RIGHT),

//        CENTER_LEFT(VerticalBias.CENTER, HorizontalBias.LEFT),
//        CENTER(VerticalBias.CENTER, HorizontalBias.CENTER),
//        CENTER_RIGHT(VerticalBias.CENTER, HorizontalBias.RIGHT),

        BOTTOM_LEFT(VerticalBias.BOTTOM, HorizontalBias.LEFT),
        BOTTOM(VerticalBias.BOTTOM, HorizontalBias.CENTER),
        BOTTOM_RIGHT(VerticalBias.BOTTOM, HorizontalBias.RIGHT);

        public VerticalBias verticalBias;
        public HorizontalBias horizontalBias;

        Direction(VerticalBias verticalBias, HorizontalBias horizontalBias) {
            this.verticalBias = verticalBias;
            this.horizontalBias = horizontalBias;
        }

        public enum VerticalBias {
            TOP/*, CENTER*/, BOTTOM
        }

        public enum HorizontalBias {
            LEFT, CENTER, RIGHT
        }
    }

    @NonNull
    public final View[] views;
    @NonNull
    public final String tag;
    public final String message;
    public final Direction direction;
    public final HighlightCover.HoleType holeType;
    public final int holePaddingDp;

    public TargetSpec(Builder builder) {
        views = builder.views;
        tag = builder.tag;
        message = builder.message;
        direction = builder.direction;
        holeType = builder.holeType;
        holePaddingDp = builder.holePaddingDp;
    }

    public static class Builder {
        @NonNull
        public final View[] views;
        @NonNull
        public final String tag;
        public String message = "";
        public Direction direction = Direction.TOP_LEFT;
        public HighlightCover.HoleType holeType = HighlightCover.HoleType.CIRCLE_CIRCUMSCRIBE;
        public int holePaddingDp = 7;

        public Builder(@NonNull String tag, @NonNull View view, View... additionalViews) {
            this.views = new View[additionalViews.length + 1];
            this.tag = tag;
            this.views[0] = view;
            System.arraycopy(additionalViews, 0, this.views, 1, additionalViews.length);
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder setHoleType(HighlightCover.HoleType holeType) {
            this.holeType = holeType;
            return this;
        }

        public Builder setHolePaddingDp(int holePaddingDp) {
            this.holePaddingDp = holePaddingDp;
            return this;
        }

        public TargetSpec build() {
            return new TargetSpec(this);
        }
    }
}
