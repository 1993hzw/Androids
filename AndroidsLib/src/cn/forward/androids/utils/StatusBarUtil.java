package cn.forward.androids.utils;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by huangziwei on 16-6-6.
 */
public class StatusBarUtil {
    /**
     * 设置沉浸式状态栏，配合布局文件中fitssystemwindows属性
     *
     * @param win
     * @param translucent
     * @param darkMode 状态栏文字是否为黑色
     */
    public static void setStatusBarTranslucent(Window win, boolean translucent, boolean darkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams winParams = win.getAttributes();
            if (translucent) {
                winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            } else {
                winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
            win.setAttributes(winParams);

            if (!setStatusBarDarkModeMEIZU(win, darkMode)) {
                setStatusBarDarkModeXIAOMI(win, darkMode);
            }
        }
    }

    /**
     * @see StatusBarUtil#setStatusBarTranslucent(Window, boolean, boolean)
     */
    public static void setStatusBarTranslucent(Activity activity, boolean translucent, boolean darkMode) {
        setStatusBarTranslucent(activity.getWindow(), translucent, darkMode);
    }

    /**
     * 魅族手机设置状态栏字体为黑色
     *
     * @param window
     * @param dark   是否设置为黑色
     * @return
     */
    public static boolean setStatusBarDarkModeMEIZU(Window window, boolean dark) {
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
//                e.printStackTrace();
        }
        return false;
    }

    /**
     * 魅族手机设置状态栏字体为黑色
     *
     * @param window
     * @param darkmode 是否设置为黑色
     * @return
     */
    public static boolean setStatusBarDarkModeXIAOMI(Window window, boolean darkmode) {
        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return false;
    }


}
