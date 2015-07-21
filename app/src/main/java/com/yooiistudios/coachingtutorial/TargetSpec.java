package com.yooiistudios.coachingtutorial;

import android.support.annotation.NonNull;
import android.view.View;

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
    public final View view;
    public final String message;
    public final Direction direction;
    public final HighlightCover.HoleType holeType;

    public TargetSpec(Builder builder) {
        view = builder.view;
        message = builder.message;
        direction = builder.direction;
        holeType = builder.holeType;
    }

    public static class Builder {
        @NonNull
        public final View view;
        public String message = "";
        public Direction direction = Direction.TOP_LEFT;
        public HighlightCover.HoleType holeType = HighlightCover.HoleType.CIRCUMSCRIBE;

        public Builder(@NonNull View view) {
            this.view = view;
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

        public TargetSpec build() {
            return new TargetSpec(this);
        }
    }
}
