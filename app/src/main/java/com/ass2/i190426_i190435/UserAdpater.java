package com.ass2.i190426_i190435;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdpater extends RecyclerView.Adapter<UserAdpater.MyViewHolder> {
    List<User> ls;
    Context c;

    public UserAdpater(List<User> ls, Context c) {
        this.ls = ls;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(c).inflate(R.layout.row_user, parent, false);
        return new MyViewHolder(row);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(ls.get(position).getName());
        byte[] imageData= Base64.getDecoder().decode(ls.get(position).getDp());
        Bitmap dppp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        holder.dp.setImageBitmap(dppp);

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(c, MessageActivity.class);

                    intent.putExtra("name", ls.get(position).getName());
                    intent.putExtra("phone", ls.get(position).getPhone());
                    intent.putExtra("profile", ls.get(position).getDp());
                    intent.putExtra("id", ls.get(position).getId());
                    intent.putExtra("lastSeen", ls.get(position).getLastSeen());
                    intent.putExtra("status", ls.get(position).getStatus());
                    c.startActivity(intent);

            }


        });
        holder.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, MessageActivity.class);

                intent.putExtra("name", ls.get(position).getName());
                intent.putExtra("phone", ls.get(position).getPhone());
                intent.putExtra("profile", ls.get(position).getDp());
                intent.putExtra("id", ls.get(position).getId());
                intent.putExtra("lastSeen", ls.get(position).getLastSeen());
                intent.putExtra("status", ls.get(position).getStatus());
                c.startActivity(intent);

            }


        });

        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/getLastMessage.php",
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
//                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {

                                JSONObject chat=res.getJSONObject("chat");
                                SharedPreferences mPref;
                                mPref= c.getSharedPreferences("com.ass2.i190426_i190435", Context.MODE_PRIVATE);
                                int idd=mPref.getInt("id", 0);

                                if(chat.getInt("seen")==0 && chat.getInt("sender")==ls.get(position).getId() && chat.getInt("receiver")==idd){

                                    holder.seenornot.setImageResource(R.drawable.ic_baseline_stop_24);
                                }
                                else {
                                    System.out.println(chat.getString("message"));
                                    holder.seenornot.setImageResource(0);
                                }

                                if(chat.getString("messageType").equals("Text")){

                                    holder.msg.setText(chat.getString("message"));
                                }
                                else if(chat.getString("messageType").equals("Voice")){
                                    holder.msg.setText("Voice Message");
                                }
                                else{
                                    holder.msg.setText("Photo");
                                }




                            } else {
                                Toast.makeText(c, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(c, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                            Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(c, "Connection Error", Toast.LENGTH_LONG).show();
                        Toast.makeText(c, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences mPref;
                mPref= c.getSharedPreferences("com.ass2.i190426_i190435", Context.MODE_PRIVATE);

                int idd=mPref.getInt("id", 0);

                params.put("sender", String.valueOf(idd));
                params.put("receiver", String.valueOf(ls.get(position).getId()));

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(c);
        queue.add(request);


    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,  msg;
        CircleImageView dp;
        ImageView seenornot;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            msg=itemView.findViewById(R.id.msg);
            dp=itemView.findViewById((R.id.dp));
            seenornot=itemView.findViewById(R.id.seenOrnot);
        }
    }
}

