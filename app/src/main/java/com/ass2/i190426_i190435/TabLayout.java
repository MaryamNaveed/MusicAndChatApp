package com.ass2.i190426_i190435;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TabLayout extends AppCompatActivity {

    com.google.android.material.tabs.TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    SharedPreferences mPref;
    SharedPreferences.Editor editmPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);


        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewerPager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.AddItems(new ChatFragment(), "Chat");
        viewPagerAdapter.AddItems(new PeopleFragment(), "Contacts");
        viewPagerAdapter.AddItems(new CallFragment(), "Calls");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        mPref=getSharedPreferences("com.ass2.i190426_i190435", MODE_PRIVATE);
        editmPref=mPref.edit();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

       /* if (item.getItemId() == R.id.main_logout_option)
        {
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_settings_option)
        {
            SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.main_create_group_option)
        {
            RequestNewGroup();
        }
        if (item.getItemId() == R.id.main_find_friends_option)
        {
            SendUserToFindFriendsActivity();
        }*/

        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        ArrayList<Fragment> fragments=new ArrayList<>();
        ArrayList<String> titles=new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void AddName(String title){
            titles.add(title);
        }

        public void AddFragment(Fragment fragment){
            fragments.add(fragment);

        }

        public  void AddItems(Fragment fragment, String title){
            AddFragment(fragment);
            AddName(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();



        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String lastSeen = "Last Seen " + dtf.format(now);


        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/updateLastseenandStatus.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {



                            } else {
                                Toast.makeText(TabLayout.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TabLayout.this, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                            Toast.makeText(TabLayout.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TabLayout.this, "Connection Error", Toast.LENGTH_LONG).show();
                        Toast.makeText(TabLayout.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(mPref.getInt("id", 0)));
                params.put("lastSeen", lastSeen);
                params.put("status1", "online");


                return params;
            }
        };

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(TabLayout.this);
        queue.add(request);




    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPause() {
        super.onPause();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String lastSeen = "Last Seen " + dtf.format(now);



        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/updateLastseenandStatus.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {



                            } else {
                                Toast.makeText(TabLayout.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TabLayout.this, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                            Toast.makeText(TabLayout.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TabLayout.this, "Connection Error", Toast.LENGTH_LONG).show();
                        Toast.makeText(TabLayout.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(mPref.getInt("id", 0)));
                params.put("lastSeen", lastSeen);
                params.put("status1", "offline");


                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(TabLayout.this);
        queue.add(request);

    }
}