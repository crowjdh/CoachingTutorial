package com.yooiistudios.coachingtutorial;

/**
 * Created by Dongheyon Jeong in Randombox_Android from Yooii Studios Co., LTD. on 15. 7. 21.
 *
 * DebugSettings
 * description
 */
public class DebugSettings {
    private static final boolean IS_DEBUG = false;
    private DebugSettings() {
        throw new AssertionError("You MUST NOT create the instance of this class!!");
    }

    public static boolean isDebug() {
        return IS_DEBUG;
    }
}
