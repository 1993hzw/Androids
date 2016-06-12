package cn.forward.androids.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.forward.androids.R;

/**
 * @author hzw
 * @date 2016/1/28.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    public static final String SHAREDPREFERENCES_NAME = "sharedpreferences_name";

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.support.v4.view.",
            "android.webkit.",
            "android.app.",
    };

    protected SharedPreferences mPrefer;

    public SharedPreferences getSharedPreferences() {
        return mPrefer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefer = getSharedPreferences(SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public boolean isRestricted() {
        // 禁止在xml布局文件中设置onClick属性
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);
        if (view != null) {
            return view;
        } else {
            return onCreateView(name, context, attrs);
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);
        if (view == null) {
            try {
                if (-1 == name.indexOf('.')) {
                    for (String prefix : sClassPrefixList) {
                        try { // 不能用getLayoutInflater(),否则弹出对话框时报空指针异常!!!!!
                            view = LayoutInflater.from(context).createView(name, prefix, attrs);
                            if (view != null) {
                                break;
                            }
                        } catch (ClassNotFoundException cnfe) {
                        }
                    }
                } else {
                    view = LayoutInflater.from(context).createView(name, null, attrs);
                }

                if (view == null) {
                    throw new ClassNotFoundException("view is null");
                } else {
                    // view注入
                    view = onInjectView(view, context, attrs);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InflateException ine) {
                throw new RuntimeException(ine);
            }
        } else { // fragments

        }

        return view;
    }

    /**
     * LayoutInflater.from(context)
     * ×如果context为getApplicationContext()，则BasicActivity中的ｖｉｅｗ注入不升息！！！！！
     * <p>
     * view注入。当通过LayoutInflater.inflater方法 创建完view时调用
     *
     * @param view
     * @param context
     * @param attrs
     * @return
     */
    public View onInjectView(View view, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.View);
        if (a.getBoolean(R.styleable.View_injectListener, false)) {
            view.setOnClickListener(BaseActivity.this);
        }
        a.recycle();
        return view;
    }

    public void showToast(String msg) {
        BaseApplication.showToast(msg);
    }

    public void showToast(int id) {
        BaseApplication.showToast(id);
    }

    public void saveBoolean(String key, boolean b) {
        SharedPreferences.Editor editor = mPrefer.edit();
        editor.putBoolean(key, b);
        editor.commit();
    }

    public void saveString(String key, String s) {
        SharedPreferences.Editor editor = mPrefer.edit();
        editor.putString(key, s);
        editor.commit();
    }

    public void saveLong(String key, long l) {
        SharedPreferences.Editor editor = mPrefer.edit();
        editor.putLong(key, l);
        editor.commit();
    }

    public void startActivity(Class<?> cls) {
        startActivity(new Intent(getApplicationContext(), cls));
    }

    /**
     * 设置沉浸式状态栏，配合布局文件中fitssystemwindows属性
     */
    public void setStatusBarTranslucent(boolean translucent, boolean isStatusBarDarMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            if (translucent) {
                winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            } else {
                winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
            win.setAttributes(winParams);

            if (!setStatusBarDarkModeMEIZU(getWindow(), isStatusBarDarMode)) {
                setStatusBarDarkModeXIAOMI(getWindow(), isStatusBarDarMode);
            }
        }
    }


    /**
     * 设置全屏
     * @param flag
     */
    public void setFullScreen(boolean flag) {
        if (flag) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 魅族手机设置状态栏字体为黑色
     *
     * @param window
     * @param dark   是否设置为黑色
     * @return
     */
    public boolean setStatusBarDarkModeMEIZU(Window window, boolean dark) {
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
    public boolean setStatusBarDarkModeXIAOMI(Window window, boolean darkmode) {
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
