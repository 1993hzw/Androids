package com.example.androidsdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cn.forward.androids.AsyncTask;
import cn.forward.androids.annotation.ViewInject;
import cn.forward.androids.annotation.ViewInjectProcessor;
import cn.forward.androids.base.BaseActivity;
import cn.forward.androids.base.InjectionLayoutInflater;
import cn.forward.androids.views.RoundProgressBar;

/**
 * Created by huangziwei on 16-6-27.
 */
public class OtherDemo extends BaseActivity {

    @ViewInject(id = R.id.plugin_download_progress)
    private RoundProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
//        mProgress = (RoundProgressBar) findViewById(R.id.plugin_download_progress);

        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 1; i <= 100; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                }
                return super.doInBackground(params);
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {

                mProgress.setProgress(progress[0]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mProgress.setProgress(100);
            }
        }.execute();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("hello");
        dialog.setMessage("hello world!");

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn1) {
            showToast("111");
        } else if (v.getId() == R.id.btn2) {
            showToast("222");
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.other_container);
            InjectionLayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_other, viewGroup, this);
        }
    }

    @Override
    public View onViewCreated(Context context, View parent, View view, AttributeSet attrs) {
        view = super.onViewCreated(context, parent, view, attrs);
        ViewInjectProcessor.process(OtherDemo.this, view);
        return view;
    }
}
