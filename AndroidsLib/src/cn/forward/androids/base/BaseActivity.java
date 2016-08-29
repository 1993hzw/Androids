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

    public static final String SHAREDPREFERENCES_NAME = "sharedpreferences_BaseActivity";

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.support.v4.view.",
            "android.webkit.",
            "android.app.",
            "android.support.v7.view.",
    };

    protected SharedPreferences mPrefer;

    public SharedPreferences getSharedPreferences() {
        return mPrefer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BaseApplication.sContext == null) {
            BaseApplication.init(getApplicationContext());
        }
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
        View view = super.onCreateView(parent, name, context, attrs); // 内部会调用onCreateView(name, context, attrs)
        return view;
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
                        } catch (Exception e) { // 渲染主题样式时可能会报context为空指针
                            return null;
                        }
                    }
                } else {
                    try {
                        view = LayoutInflater.from(context).createView(name, null, attrs);
                    } catch (Exception e) {
                        return null;
                    }
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

}
