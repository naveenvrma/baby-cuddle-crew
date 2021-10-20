package com.example.babycuddlecrew.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babycuddlecrew.R;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingAppActivity extends AppCompatActivity {

    private static final long DELAY = 3000;
    private boolean scheduled = false;
    private Timer splashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_app);

        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask(){
            @Override
            public void run(){
                LoadingAppActivity.this.finish();
                startActivity(new Intent(LoadingAppActivity.this, MainActivity.class));
            }
        }, DELAY);
        scheduled = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduled)
            splashTimer.cancel();
        splashTimer.purge();
    }
}
