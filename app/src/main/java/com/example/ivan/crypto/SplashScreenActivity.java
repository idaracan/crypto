package com.example.ivan.crypto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ivan on 15/02/18.
 */

public class SplashScreenActivity extends AppCompatActivity implements Runnable {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        toMainActivity();
        finish();
    }
    public void toMainActivity(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
