package com.example.androidsdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import cn.forward.androids.views.KeyboardLayout;

/**
 * Created by huangziwei on 16-6-27.
 */
public class KeyboardLayoutDemo extends Activity {
    private KeyboardLayout mKeyboardLayout;
    private View mEmojiView;
    private Button mEmojiBtn;
    private EditText mInput;

    int mKeyboardHeight = 400; // 输入法默认高度为400

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_layout);

        // 起初的布局可自动调整大小
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mKeyboardLayout = (KeyboardLayout) findViewById(R.id.keyboard_layout);
        mEmojiView = findViewById(R.id.emoji);
        mEmojiBtn = (Button) findViewById(R.id.emoji_btn);

        mInput = (EditText) findViewById(R.id.input);

        // 点击输入框
        mInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyboardLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() { // 输入法弹出之后，重新调整
                        mEmojiBtn.setSelected(false);
                        mEmojiView.setVisibility(View.GONE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                }, 250); // 延迟一段时间，等待输入法完全弹出
            }
        });

        mEmojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmojiBtn.setSelected(!mEmojiBtn.isSelected());

                if (mKeyboardLayout.isKeyboardActive()) { // 输入法打开状态下
                    if (mEmojiBtn.isSelected()) { // 打开表情
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING); //  不改变布局，隐藏键盘，emojiView弹出
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mInput.getApplicationWindowToken(), 0);
                        mEmojiView.setVisibility(View.VISIBLE);
                    } else {
                        mEmojiView.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mInput.getApplicationWindowToken(), 0);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                } else { //  输入法关闭状态下
                    if (mEmojiBtn.isSelected()) {
                        // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                        mEmojiView.setVisibility(View.VISIBLE);
                    } else {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        mEmojiView.setVisibility(View.GONE);
                    }
                }
            }
        });

        mKeyboardLayout.setKeyboardListener(new KeyboardLayout.KeyboardLayoutListener() {
            @Override
            public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {

                if (isActive) { // 输入法打开
                    if (mKeyboardHeight != keyboardHeight) { // 键盘发生改变时才设置emojiView的高度，因为会触发onGlobalLayoutChanged，导致onKeyboardStateChanged再次被调用
                        mKeyboardHeight = keyboardHeight;
                        initEmojiView(); // 每次输入法弹起时，设置emojiView的高度为键盘的高度，以便下次emojiView弹出时刚好等于键盘高度
                    }
                    if (mEmojiBtn.isSelected()) { // 表情打开状态下
                        mEmojiView.setVisibility(View.GONE);
                        mEmojiBtn.setSelected(false);
                    }
                }
            }
        });
    }

    // 设置表情栏的高度
    private void initEmojiView() {
        ViewGroup.LayoutParams layoutParams = mEmojiView.getLayoutParams();
        layoutParams.height = mKeyboardHeight;
        mEmojiView.setLayoutParams(layoutParams);
    }
}
