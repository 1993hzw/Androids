package cn.forward.androids.exception;

import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import cn.forward.androids.utils.LogUtil;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private HandlerListener mHandlerListener;
    private String mLogDir;

    public UncaughtExceptionHandler() {
        this(null);
    }

    public UncaughtExceptionHandler(HandlerListener handlerListener) {
        this(handlerListener, null);
    }

    public UncaughtExceptionHandler(HandlerListener handlerListener, String logDirPath) {
        mHandlerListener = handlerListener;
        mLogDir = logDirPath != null ? logDirPath : LogUtil.LOG_DIR;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    private void saveLogs(Throwable ex) {
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            LogUtil.writeLog(new String(data), mLogDir);
        } catch (Exception e) {
            LogUtil.e("UncaughtExceptionHandler", e.getMessage(), e);
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtil.i("uncaughtException");
        if (ex != null) {
            ex.printStackTrace();
            saveLogs(ex);
        }
        if (mHandlerListener != null) {
            Looper.getMainLooper().prepare();
            mHandlerListener.onHandleException(ex);
            Looper.getMainLooper().loop();
        }
    }

    public interface HandlerListener {
        void onHandleException(Throwable throwable);
    }

}
