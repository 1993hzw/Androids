package cn.forward.androids.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by huangziwei on 16-6-27.
 */
public class KeyboardLayout extends FrameLayout {

    private KeyboardLayoutListener mListener;
    private boolean mIsKeyboardActive = false; //　输入法是否激活
    private int mKeyboardHeight = 0; // 输入法高度

    public KeyboardLayout(Context context) {
        this(context, null, 0);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboradOnGlobalChangeListener());
    }

    private class KeyboradOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        int mScreenHeight = 0;

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }
            mScreenHeight = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight();
            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            Rect localObject = new Rect();
            ((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(localObject);
            int screenHeight = getScreenHeight();
            int keyboardHeight = screenHeight - localObject.bottom; // 输入法的高度
            boolean bool = false;
            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                bool = true; // 超过屏幕五分之一则表示弹出了输入法
                mKeyboardHeight = keyboardHeight;
            }
            mIsKeyboardActive = bool;
            if (mListener != null) {
                mListener.onKeyboardStateChanged(bool, keyboardHeight);
            }
        }
    }

    public void setKeyboardListener(KeyboardLayoutListener listener) {
        mListener = listener;
    }

    public KeyboardLayoutListener getKeyboardListener() {
        return mListener;
    }

    public boolean isKeyboardActive() {
        return mIsKeyboardActive;
    }

    public interface KeyboardLayoutListener {
        void onKeyboardStateChanged(boolean isActive, int keyboardHeight);
    }

}
