package com.example.androidsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MaskImageViewDemo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maskimageview);

        setTitle("MaskImageView");

        findViewById(R.id.stextview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

    }

}
