package com.yooiistudios.coachingtutorial.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Dongheyon Jeong in Randombox_Android from Yooii Studios Co., LTD. on 2015. 10. 5.
 *
 * UnitConverter
 * description
 */
public class UnitConverter {
    private UnitConverter() {
        throw new AssertionError("You MUST NOT create the instance of this class!!");
    }

    public static float dpToPixels(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}
