package cn.forward.androids.utils;

import android.content.Context;

/**
 * Created by huangziwei on 16-3-8.
 */
public class Util {

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
