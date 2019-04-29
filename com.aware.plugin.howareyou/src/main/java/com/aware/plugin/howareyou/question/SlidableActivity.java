package com.aware.plugin.howareyou.question;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.model.SlidrPosition;

public abstract class SlidableActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);


        SlidrConfig config = new SlidrConfig.Builder()
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {
                    }

                    @Override
                    public void onSlideChange(float percent) {
                    }

                    @Override
                    public void onSlideOpened() {
                    }

                    @Override
                    public void onSlideClosed() {
                        onSlide();
                    }
                })
                .build();

        Slidr.attach(this, config);
    }

    protected abstract void onSlide();
}