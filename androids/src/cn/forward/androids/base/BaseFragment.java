package cn.forward.androids.base;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


/**
 * @author hzw
 * @date 2016/2/27.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {

    protected SharedPreferences mPrefer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onClick(View v) {

    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        mPrefer = sharedPreferences;
    }

    public SharedPreferences getSharedPreferences() {
        return mPrefer;
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int id) {
        Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
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
