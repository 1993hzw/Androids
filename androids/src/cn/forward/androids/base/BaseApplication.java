package cn.forward.androids.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import cn.forward.androids.exception.UncaughtExceptionHandler;
import cn.forward.androids.utils.ThreadUtil;

public class BaseApplication extends Application {

    public static int VERSION_CODE = -1;
    public static String VERSION_NAME = "";

    public static Toast sToast;
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
    }

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        sContext = context;
        sToast = Toast.makeText(sContext, "", Toast.LENGTH_SHORT);
        try {
            PackageManager manager = sContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(sContext.getPackageName(), 0);
            VERSION_CODE = info.versionCode;
            VERSION_NAME = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStringById(int id) {
        if (sContext == null) {
            return null;
        }
        return BaseApplication.sContext.getResources().getString(id);
    }

    public static void showToast(String msg) {

        if (BaseApplication.sContext == null) {
            return;
        }
        BaseApplication.sToast.setText(msg);
        BaseApplication.sToast.setDuration(Toast.LENGTH_SHORT);
        BaseApplication.sToast.show();
    }

    public static void showToast(int id) {
        showToast(getStringById(id));
    }

}
