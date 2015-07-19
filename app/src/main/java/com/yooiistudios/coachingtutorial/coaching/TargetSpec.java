package com.yooiistudios.coachingtutorial.coaching;

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
        LEFT, TOP, RIGHT, BOTTOM;
    }
    @NonNull
    public final View view;
    public final String message;
    public final Direction direction;

    public TargetSpec(Builder builder) {
        view = builder.view;
        message = builder.message;
        direction = builder.direction;
    }

    public static class Builder {
        @NonNull
        public final View view;
        public String message = "";
        public Direction direction = Direction.LEFT;

        public Builder(@NonNull View view) {
            this.view = view;
        }

        public Builder setMessags(String message) {
            this.message = message;
            return this;
        }

        public Builder setDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        public TargetSpec build() {
            return new TargetSpec(this);
        }
    }
}
