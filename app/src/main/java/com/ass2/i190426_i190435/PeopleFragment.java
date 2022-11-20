package com.ass2.i190426_i190435;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PeopleFragment extends Fragment {

    public PeopleFragment() {
        // Required empty public constructor
    }

    RecyclerView rv;
    List<User> temp;
    MyAdapterContacts adapter;
    List<User> ls;
    EditText search;
    String searchText="";
    ImageView add;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rv= getView().findViewById(R.id.rv);
        search =getView().findViewById(R.id.search);
        ls=new ArrayList<>();
        adapter=new MyAdapterContacts(ls, getActivity());
        rv.setAdapter(adapter);
        add=getView().findViewById(R.id.add);
        RecyclerView.LayoutManager lm=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchText = search.getText().toString();
                ls.clear();

                if(searchText.isEmpty()){
                    ls.addAll(temp);
                }
                else{
                    for(User u1: temp){
                        if(u1.getName().toLowerCase().contains(searchText.toLowerCase())){
                            ls.add(u1);

                        }
                        else if(u1.getPhone().toLowerCase().contains(searchText.toLowerCase())){
                            ls.add(u1);
                        }
                        adapter.notifyDataSetChanged();

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 200);

        } else {

            LoadContacts();

        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AddContact.class);
                startActivity(intent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void LoadContacts(){
        temp=new ArrayList<>();
        ls.clear();

        ContentResolver contentResolver =getView().getContext().getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null);

        System.out.println("Cursor: "+cursor.getCount()+"  "+cursor.getColumnName(0));

        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                @SuppressLint("Range") String id= cursor.getString(cursor.getColumnIndex((ContactsContract.Contacts._ID)));
                @SuppressLint("Range") String name= cursor.getString(cursor.getColumnIndex((ContactsContract.Contacts.DISPLAY_NAME)));
                @SuppressLint("Range") int no= Integer.parseInt(cursor.getString(cursor.getColumnIndex((ContactsContract.Contacts.HAS_PHONE_NUMBER))));

                if(no>0){
                    Cursor cursor1 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);

                    while(cursor1.moveToNext()){
                        @SuppressLint("Range") String phone =cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        User u = new User();
                        u.setName(name);
                        u.setPhone(phone);
                        temp.add(u);
                        ls.add(u);
                    }
                    cursor1.close();
                }
            }
        }
        cursor.close();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                LoadContacts();

            } else {

            }
        }
    }
    public void SearchContacts(){


    }


}