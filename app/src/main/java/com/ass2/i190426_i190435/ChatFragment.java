package com.ass2.i190426_i190435;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    RecyclerView rv;
    UserAdpater adapter;
    List<User> ls;
    TextView logout, username;
    DrawerLayout drawer;
    CircleImageView dp, profileImage;
    ImageView menu;
    SharedPreferences mPref;
    SharedPreferences.Editor editmPref;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ls=new ArrayList<>();
        rv=getView().findViewById(R.id.rv);
        drawer=getView().findViewById(R.id.drawer);
        logout=getView().findViewById(R.id.logout);
        username=getView().findViewById(R.id.userName);
        dp=getView().findViewById(R.id.dpImage);
        profileImage=getView().findViewById(R.id.profileImage);
        menu=getView().findViewById(R.id.menu);

        mPref=getActivity().getSharedPreferences("com.ass2.i190426_i190435", Context.MODE_PRIVATE);
        editmPref=mPref.edit();

        adapter=new UserAdpater(ls, getActivity());
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager lm=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawer.isDrawerOpen(Gravity.LEFT)){
                    drawer.closeDrawer(Gravity.LEFT);
                }
                else{
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editmPref.putBoolean("loggedIn", false);
                editmPref.apply();
                editmPref.commit();

                Intent intent = new Intent(getActivity(), CreateAccount.class);
                startActivity(intent);

            }
        });

        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/getUserbyId.php",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {
                                JSONObject user=res.getJSONObject("user");
                                username.setText(user.getString("name"));
                                byte[] imageData= Base64.getDecoder().decode(user.getString("dp"));
                                Bitmap dppp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                dp.setImageBitmap(dppp);
                                profileImage.setImageBitmap(dppp);



                            } else {
                                Toast.makeText(getActivity(), res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Cannot Parse JSON", Toast.LENGTH_LONG).show();
//                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                int idd=mPref.getInt("id", 0);

                params.put("id", String.valueOf(idd));


                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);


        StringRequest request1 = new StringRequest(Request.Method.GET, Ip.ipAdd + "/getUsers.php",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {
                                JSONArray users=res.getJSONArray("users");
                                for (int i=0; i<users.length(); i++){
                                    JSONObject u=users.getJSONObject(i);
                                    if(u.getInt("id")!=mPref.getInt("id", 0)){
                                        ls.add(new User(u.getInt("id"),u.getString("name"), u.getString("email"), u.getString("password"),u.getString("gender"),u.getString("num"),u.getString("status1"),u.getString("lastSeen"),u.getString("deviceId"),u.getString("dp")));
                                        adapter.notifyDataSetChanged();
                                    }

                                }



                            } else {
                                Toast.makeText(getActivity(), res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Cannot Parse JSON", Toast.LENGTH_LONG).show();
//                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        request1.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue1 = Volley.newRequestQueue(getActivity());
        queue1.add(request1);



    }

    @Override
    public void onResume() {
        super.onResume();



        StringRequest request1 = new StringRequest(Request.Method.GET, Ip.ipAdd + "/getUsers.php",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        ls.clear();

                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {
                                JSONArray users=res.getJSONArray("users");
                                for (int i=0; i<users.length(); i++){
                                    JSONObject u=users.getJSONObject(i);
                                    if(u.getInt("id")!=mPref.getInt("id", 0)){
                                        ls.add(new User(u.getInt("id"),u.getString("name"), u.getString("email"), u.getString("password"),u.getString("gender"),u.getString("num"),u.getString("status1"),u.getString("lastSeen"),u.getString("deviceId"),u.getString("dp")));
                                        adapter.notifyDataSetChanged();
                                    }

                                }



                            } else {
                                Toast.makeText(getActivity(), res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Cannot Parse JSON", Toast.LENGTH_LONG).show();
//                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        request1.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue1 = Volley.newRequestQueue(getActivity());
        queue1.add(request1);

    }
}