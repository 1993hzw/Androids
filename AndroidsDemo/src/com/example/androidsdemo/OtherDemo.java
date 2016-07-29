package com.example.androidsdemo;

import android.app.Activity;
import android.os.Bundle;

import cn.forward.androids.AsyncTask;
import cn.forward.androids.views.RoundProgressBar;

/**
 * Created by huangziwei on 16-6-27.
 */
public class OtherDemo extends Activity {

    private RoundProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        mProgress = (RoundProgressBar) findViewById(R.id.plugin_download_progress);

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
    }
}
