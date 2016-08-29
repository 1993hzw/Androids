package cn.forward.androids.base;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;


/**
 * @author hzw
 * @date 2016/2/27.
 */
public class BaseFragment extends Fragment {

    protected SharedPreferences mPrefer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefer = getActivity().getSharedPreferences(BaseActivity.SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        if (BaseApplication.sContext == null) {
            BaseApplication.init(getContext());
        }
    }

    public void onClick(View v) {

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
        startActivity(new Intent(getActivity().getApplicationContext(), cls));
    }
}
