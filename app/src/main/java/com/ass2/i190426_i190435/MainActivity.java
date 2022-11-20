package com.ass2.i190426_i190435;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;



public class MainActivity extends AppCompatActivity {

    Handler handler;
    SharedPreferences mPref;
    SharedPreferences.Editor editmPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref=getSharedPreferences("com.ass2.i190426_i190435", MODE_PRIVATE);
        editmPref=mPref.edit();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                    if(mPref.getBoolean("loggedIn", false)){
                        Intent intent= new Intent(MainActivity.this, TabLayout.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent= new Intent(MainActivity.this, CreateAccount.class);
                        startActivity(intent);
                    }


                finish();
            }
        }, 5000);
    }


}