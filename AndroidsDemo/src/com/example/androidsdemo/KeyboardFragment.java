/*
package com.example.androidsdemo;


import android.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


public class KeyBoardFragment
        extends Fragment {
    private static long lastClickTime;
    public View mBackgroundView;
    public EditText mEditView;
    public View mEmotionView;
    public View mFunctionView;
    public KeyboardListenLinearLayout mKeyboardLayout;
    public boolean mKeykoardShow;
    public boolean misLive;

    private void initViewHeight() {
        try {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) this.mBackgroundView.getLayoutParams();
            localLayoutParams.height = dop.f();
            this.mBackgroundView.setLayoutParams(localLayoutParams);
            return;
        } catch (Exception localException) {
            RelativeLayout.LayoutParams localLayoutParams1 = (RelativeLayout.LayoutParams) this.mBackgroundView.getLayoutParams();
            localLayoutParams1.height = dop.f();
            this.mBackgroundView.setLayoutParams(localLayoutParams1);
        }
    }

    public static boolean isFastDoubleClick() {
        long l1 = System.currentTimeMillis();
        long l2 = l1 - lastClickTime;
        if ((0L < l2) && (l2 < 600L)) {
            return true;
        }
        lastClickTime = l1;
        return false;
    }

    private void setBackgroundViewGone(View paramView) {
        ka.a("ddrb", "setBackgroundViewGone");
        if (paramView.getVisibility() == 4) {
        }
        try {
            Thread.sleep(200L);
            paramView.setVisibility(8);
            return;
        } catch (InterruptedException localInterruptedException) {
            for (; ; ) {
                localInterruptedException.printStackTrace();
            }
        }
    }

    private void setBackgroundViewInvisible(View paramView) {
        if (paramView.getVisibility() == 0) {
        }
        try {
            Thread.sleep(200L);
            paramView.setVisibility(4);
            return;
        } catch (InterruptedException localInterruptedException) {
            for (; ; ) {
                localInterruptedException.printStackTrace();
            }
        }
    }

    private void setEditTextTouch() {
        this.mEditView.setOnClickListener(new KeyBoardFragment .3 (this));
    }

    private void setKeyboardListener() {
        this.mKeyboardLayout.setOnKeyboardStateChangedListener(new KeyBoardFragment .2 (this));
    }

    private void setSimpleKeyboardListener() {
        this.mKeyboardLayout.setOnKeyboardStateChangedListener(new KeyBoardFragment .1 (this));
    }

    public void initAllView(View paramView, KeyboardListenLinearLayout paramKeyboardListenLinearLayout, EditText paramEditText) {
        this.mBackgroundView = paramView;
        this.mKeyboardLayout = paramKeyboardListenLinearLayout;
        this.mEditView = paramEditText;
        setKeyboardListener();
        setEditTextTouch();
    }

    public void initAllView(View paramView1, KeyboardListenLinearLayout paramKeyboardListenLinearLayout, EditText paramEditText, View paramView2, View paramView3) {
        this.mBackgroundView = paramView1;
        this.mKeyboardLayout = paramKeyboardListenLinearLayout;
        this.mEditView = paramEditText;
        this.mEmotionView = paramView2;
        this.mFunctionView = paramView3;
        setKeyboardListener();
        setEditTextTouch();
    }

    public void initAllView(View paramView, KeyboardListenLinearLayout paramKeyboardListenLinearLayout, EditText paramEditText, boolean paramBoolean) {
        this.mBackgroundView = paramView;
        this.mKeyboardLayout = paramKeyboardListenLinearLayout;
        this.mEditView = paramEditText;
        this.misLive = paramBoolean;
        setKeyboardListener();
        setEditTextTouch();
    }

    public void initAllView(KeyboardListenLinearLayout paramKeyboardListenLinearLayout) {
        this.mKeyboardLayout = paramKeyboardListenLinearLayout;
        setSimpleKeyboardListener();
    }

    public void onKeyboardChanged(int paramInt) {
    }

    public void setViewState() {
        if (isFastDoubleClick()) {
            return;
        }
        if (this.mBackgroundView.getVisibility() == 0) {
            getActivity().getWindow().setSoftInputMode(35);
            try {
                Thread.sleep(200L);
                this.mBackgroundView.setVisibility(4);
                this.mEditView.setFocusable(true);
                this.mEditView.setFocusableInTouchMode(true);
                this.mEditView.requestFocus();
                dop.b(getActivity());
                initViewHeight();
                return;
            } catch (InterruptedException localInterruptedException1) {
                for (; ; ) {
                    localInterruptedException1.printStackTrace();
                }
            }
        }
        getActivity().getWindow().setSoftInputMode(35);
        try {
            Thread.sleep(200L);
            this.mBackgroundView.setVisibility(0);
            dop.a(getActivity());
            initViewHeight();
            onKeyboardChanged(-4);
            return;
        } catch (InterruptedException localInterruptedException2) {
            for (; ; ) {
                localInterruptedException2.printStackTrace();
            }
        }
    }

    public void setViewStateForEmotion() {
        if (this.mBackgroundView.getVisibility() == 0) {
            if (this.mFunctionView.getVisibility() == 0) {
                this.mEmotionView.setVisibility(0);
                this.mFunctionView.setVisibility(8);
                onKeyboardChanged(-4);
                return;
            }
            getActivity().getWindow().setSoftInputMode(35);
            try {
                Thread.sleep(200L);
                this.mEditView.setFocusable(true);
                this.mEditView.setFocusableInTouchMode(true);
                this.mEditView.requestFocus();
                dop.b(getActivity());
                initViewHeight();
                return;
            } catch (InterruptedException localInterruptedException1) {
                for (; ; ) {
                    localInterruptedException1.printStackTrace();
                }
            }
        }
        getActivity().getWindow().setSoftInputMode(35);
        try {
            Thread.sleep(200L);
            this.mBackgroundView.setVisibility(0);
            this.mEmotionView.setVisibility(0);
            this.mFunctionView.setVisibility(8);
            dop.a(getActivity());
            initViewHeight();
            onKeyboardChanged(-4);
            return;
        } catch (InterruptedException localInterruptedException2) {
            for (; ; ) {
                localInterruptedException2.printStackTrace();
            }
        }
    }

    public void setViewStateForFunction() {
        if (this.mBackgroundView.getVisibility() == 0) {
            if (this.mEmotionView.getVisibility() == 0) {
                this.mFunctionView.setVisibility(0);
                this.mEmotionView.setVisibility(8);
                onKeyboardChanged(-5);
                return;
            }
            getActivity().getWindow().setSoftInputMode(35);
            try {
                Thread.sleep(200L);
                this.mEditView.setFocusable(true);
                this.mEditView.setFocusableInTouchMode(true);
                this.mEditView.requestFocus();
                dop.b(getActivity());
                initViewHeight();
                return;
            } catch (InterruptedException localInterruptedException1) {
                for (; ; ) {
                    localInterruptedException1.printStackTrace();
                }
            }
        }
        getActivity().getWindow().setSoftInputMode(35);
        try {
            Thread.sleep(200L);
            this.mBackgroundView.setVisibility(0);
            this.mFunctionView.setVisibility(0);
            this.mEmotionView.setVisibility(8);
            dop.a(getActivity());
            initViewHeight();
            onKeyboardChanged(-5);
            return;
        } catch (InterruptedException localInterruptedException2) {
            for (; ; ) {
                localInterruptedException2.printStackTrace();
            }
        }
    }
}
*/
