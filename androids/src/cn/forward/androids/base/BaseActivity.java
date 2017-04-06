package cn.forward.androids.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author hzw
 * @date 2016/1/28.
 */
public class BaseActivity extends Activity implements View.OnClickListener, InjectionLayoutInflater.OnViewCreatedListener {

    protected SharedPreferences mPrefer;
    private boolean mCanViewInjected = true;
    private InjectionLayoutInflater.OnViewCreatedListener mViewClickListenerInjector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void setContentView(int layoutResID) {
        if (mViewClickListenerInjector == null) {
            mViewClickListenerInjector = InjectionLayoutInflater.getViewOnClickListenerInjector(this);
        }
        View view = InjectionLayoutInflater.from(this).inflate(layoutResID, null, this);
        super.setContentView(view);
    }

    @Override
    public View onViewCreated(Context context, View parent, View view, AttributeSet attrs) {
        if (!mCanViewInjected) {
            return view;
        }
        return mViewClickListenerInjector.onViewCreated(context, parent, view, attrs);
    }

    public void setCanViewInjected(boolean canViewInjected) {
        if (((ViewGroup) findViewById(android.R.id.content)).getChildCount() > 0) {
            throw new RuntimeException("必须在setContentView之前调用");
        }
        mCanViewInjected = canViewInjected;
    }

    public boolean isCanViewInjected() {
        return mCanViewInjected;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        mPrefer = sharedPreferences;
    }

    public SharedPreferences getSharedPreferences() {
        return mPrefer;
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
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
     * 设置全屏
     *
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

}
