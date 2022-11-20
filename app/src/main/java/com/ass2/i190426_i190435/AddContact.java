package com.ass2.i190426_i190435;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContact extends AppCompatActivity {

    Button add;
    EditText name, num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        add=findViewById(R.id.add);
        name=findViewById(R.id.name);
        num=findViewById(R.id.num);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().isEmpty() && !num.getText().toString().isEmpty()){

                    AddNewContact();


                }
                else{
                    Toast.makeText(AddContact.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                AddNewContact();

            } else {

            }
        }
    }

    public void AddNewContact(){
        Intent intent=new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name.getText().toString());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, num.getText().toString());

        startActivity(intent);


    }
}