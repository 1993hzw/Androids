package cn.forward.androids.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author hzw
 * @date 2016/1/16.
 */
public class LogUtil {

    public static String LOG_DIR = "ALog";

    public static boolean sIsLog = true;
    public static final String LOG_TAG = "log";

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

    public static boolean writeLog(String log, String dirPath) {
        File sdCardDir;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录
        } else { // sd1
            sdCardDir = new File("/storage/sdcard1");
        }

        if (!sdCardDir.exists()) {
            return false;
        }

        try {
            if (dirPath == null) {
                dirPath = LOG_DIR;
            }

            String date = DateUtil.getDate();

            log = "\r\n" + date + "==>" + log;
            String fileName = "log-" + date + ".txt";
            File dir = new File(sdCardDir, dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(log.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("LogUtil", e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean writeLog(String log) {
        return writeLog(log, null);
    }

}
