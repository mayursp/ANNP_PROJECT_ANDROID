package com.example.chatserver;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable(){

            public void run(){
                Intent i = new Intent(SplashScreen.this, MainScreen.class);
                startActivity(i);
                finish();
            }

        },1000);
    }
}
