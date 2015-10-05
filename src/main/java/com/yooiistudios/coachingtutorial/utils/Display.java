package com.yooiistudios.coachingtutorial.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by Dongheyon Jeong in Randombox_Android from Yooii Studios Co., LTD. on 2015. 10. 5.
 *
 * Display
 *  display
 */
public class Display {
    private Display() {
        throw new AssertionError("You MUST NOT create the instance of this class!!");
    }

    public static Size getSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = windowManager.getDefaultDisplay();

        Point pointForSize = new Point();
        display.getSize(pointForSize);

        return new Size(pointForSize);
    }
}
