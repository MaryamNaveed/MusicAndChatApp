package com.ass2.i190426_i190435;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreateAccount extends AppCompatActivity {

    Button createaccount;
    TextView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createaccount = findViewById(R.id.createaccount);
        signin = findViewById(R.id.signin);

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(CreateAccount.this, SignUp.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(CreateAccount.this, SignIn.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(mAuth.getCurrentUser()!=null){
//            startActivity(new Intent(CreateAccount.this, MainActivity2.class));
//        }
//    }
}