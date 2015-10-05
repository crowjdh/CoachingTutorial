package com.yooiistudios.coachingtutorial.utils;

import android.graphics.Point;

/**
 * Created by Dongheyon Jeong in Randombox_Android from Yooii Studios Co., LTD. on 2015. 10. 5.
 *
 * Size
 * description
 */
public class Size {
    public final int width;
    public final int height;

    public Size(Point point) {
        this.width = point.x;
        this.height = point.y;
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
