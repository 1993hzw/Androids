package com.example.androidsdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.forward.androids.utils.EllipsizeUtils;

/**
 * @author ziwei huang
 */
public class EllipsizeUtilsDemo extends Activity {

    private EditText mEditText;
    private TextView mOrigin;
    private TextView mSearchResult;
    private TextView mSearchResult2;
    private TextView mSearchResult3;
    private TextView mMaxLineView;

    private TextView mEllipsizeStart, mEllipsizeMiddle, mEllipsizeEnd;

    private static final String TEXT = "“I want to, very much,” the little prince replied. “But I have not much time. I have friends to discover, and a great many things to understand.”" +
            "“我非常想，”小王子回答道，“但是我没有太多时间，我要去找我的朋友，还有很多事情要弄明白。”";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ellipise_demo);
        setTitle("EllipsizeUtils");

        mEditText = findViewById(R.id.edit_text);
        mOrigin = findViewById(R.id.text);
        mSearchResult = findViewById(R.id.search_result);
        mSearchResult2 = findViewById(R.id.search_result2);
        mSearchResult3 = findViewById(R.id.search_result3);
        mMaxLineView = findViewById(R.id.max_line);
        mEllipsizeStart = findViewById(R.id.ellipsize_start);
        mEllipsizeMiddle = findViewById(R.id.ellipsize_middle);
        mEllipsizeEnd = findViewById(R.id.ellipsize_end);
        mEllipsizeStart.setEllipsize(TextUtils.TruncateAt.START);
        mEllipsizeMiddle.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        mEllipsizeEnd.setEllipsize(TextUtils.TruncateAt.END);

        mEllipsizeStart.setText(TEXT);
        mEllipsizeMiddle.setText(TEXT);
        mEllipsizeEnd.setText(TEXT);

        mMaxLineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"1", "2", "3", "4", "不限"};
                new AlertDialog.Builder(EllipsizeUtilsDemo.this)
                        .setTitle("Max line")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean isSingleLine = (which == 0);
                                int max = 1;
                                if (which == items.length - 1) {
                                    max = Integer.MAX_VALUE;
                                } else {
                                    max = which + 1;
                                }
                                mSearchResult.setSingleLine(isSingleLine);
                                mSearchResult.setMaxLines(max);
                                mSearchResult2.setSingleLine(isSingleLine);
                                mSearchResult2.setMaxLines(max);
                                mSearchResult3.setSingleLine(isSingleLine);
                                mSearchResult3.setMaxLines(max);

                                mEllipsizeStart.setSingleLine(isSingleLine);
                                mEllipsizeStart.setMaxLines(max);
                                mEllipsizeMiddle.setSingleLine(isSingleLine);
                                mEllipsizeMiddle.setMaxLines(max);
                                mEllipsizeEnd.setSingleLine(isSingleLine);
                                mEllipsizeEnd.setMaxLines(max);

                                mMaxLineView.setText("Max Line=" + items[which]);
                                mEditText.setText(mEditText.getText());
                                mEditText.setSelection(mEditText.getText().toString().length());
                            }
                        }).create().show();
            }
        });

        mOrigin.setText(TEXT);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EllipsizeUtils.ellipsizeAndHighlight(mSearchResult, TEXT, s.toString(),
                        Color.RED, true, true);

                EllipsizeUtils.ellipsizeAndHighlight(mSearchResult2, TEXT, s.toString(),
                        Color.RED, false, true);

                EllipsizeUtils.ellipsizeAndHighlight(mSearchResult3, TEXT, s.toString(),
                        Color.RED, false, false);

                EllipsizeUtils.ellipsize(mEllipsizeStart, TEXT);
                EllipsizeUtils.ellipsize(mEllipsizeMiddle, TEXT);
                EllipsizeUtils.ellipsize(mEllipsizeEnd, TEXT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
