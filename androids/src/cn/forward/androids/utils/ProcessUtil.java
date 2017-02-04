package cn.forward.androids.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 *
 */
public class ProcessUtil {

    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (null == infos) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo process : infos) {
            if (process.pid == pid) {
                return process.processName;
            }
        }
        return "";
    }

    public static boolean isMainProcess(Context context) {
        return context.getApplicationContext().getPackageName().equals(getCurrentProcessName(context));
    }
}
