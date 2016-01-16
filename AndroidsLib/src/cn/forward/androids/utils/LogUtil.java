package cn.forward.androids.utils;

import android.util.Log;

/**
 * 日志输出工具类
 *
 * @author hzw
 * @date 2016/1/16.
 */
public class LogUtil {

    public static boolean sIsLog = true;
    public static final String LOG_TAG  = "log";

    public static void v(String msg) {
        v(LOG_TAG, msg);
    }

    public static void d(String msg) {
        d(LOG_TAG, msg);
    }

    public static void i(String msg) {
        i(LOG_TAG, msg);
    }

    public static void w(String msg) {
        w(LOG_TAG, msg);
    }

    public static void e(String msg) {
        e(LOG_TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (sIsLog) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (sIsLog) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (sIsLog) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sIsLog) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (sIsLog) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (sIsLog) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (sIsLog) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (sIsLog) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (sIsLog) {
            Log.w(tag, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (sIsLog) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sIsLog) {
            Log.e(tag, msg, tr);
        }
    }


}
