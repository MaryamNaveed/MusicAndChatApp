package com.ass2.i190426_i190435;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile;
    TextView name, seen;
    EditText msg;
    ImageButton send;

    String userName, userDp, seenUser, statusUser;
    int id;

    RecyclerView rv;

    MessageAdapter adapter;
    List<Chat> ls;

    SharedPreferences mPref;
    SharedPreferences.Editor editmPref;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        name=findViewById(R.id.name);
        profile=findViewById(R.id.profile);
        send=findViewById(R.id.send);
        msg=findViewById(R.id.msg);
        ls=new ArrayList<>();
        rv=findViewById(R.id.rv);
        seen=findViewById(R.id.seen);
        mPref= getSharedPreferences("com.ass2.i190426_i190435", MODE_PRIVATE);
        editmPref=mPref.edit();

        adapter=new MessageAdapter(ls, MessageActivity.this);
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager lm=new LinearLayoutManager(MessageActivity.this);
        rv.setLayoutManager(lm);

        userName=getIntent().getStringExtra("name");
        name.setText(userName);

        id=getIntent().getIntExtra("id", 0);



        userDp=getIntent().getStringExtra("profile");
        byte[] imageData= Base64.getDecoder().decode(userDp);
        Bitmap dppp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        profile.setImageBitmap(dppp);


        seenUser=getIntent().getStringExtra("lastSeen");
        statusUser=getIntent().getStringExtra("status");

        if(statusUser.equals("offline")){
            seen.setText(seenUser);
        }
        else{
            seen.setText(statusUser);
        }




        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(msg.getText().toString().trim().equals("")){
                    Toast.makeText(MessageActivity.this, "Message is Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(mPref.getInt("id", 0), id,msg.getText().toString(), "Text");
                    msg.setText("");
                }
            }
        });



    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(int sender, int receiver,String message, String messageType){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        Chat newChat = new Chat(sender, receiver, message, dtf.format(now), messageType, 0);


        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/addChat.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {

                                getMessage();

                            } else {
                                Toast.makeText(MessageActivity.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MessageActivity.this, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageActivity.this, "Connection Error", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender", String.valueOf(newChat.getSender()));
                params.put("receiver", String.valueOf(newChat.getReceiver()));
                params.put("message", newChat.getMessage());
                params.put("messageType", newChat.getMessageType());
                params.put("date", newChat.getDate());
                params.put("seen",String.valueOf(0));



                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);
        queue.add(request);







    }
    public void getMessage(){
        ls.clear();

        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/getChatbyId.php",
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {

                                JSONArray chats=res.getJSONArray("chats");
                                for (int i=0; i<chats.length(); i++){
                                    JSONObject c=chats.getJSONObject(i);

                                    ls.add(new Chat(c.getInt("sender"),c.getInt("receiver"), c.getString("message"), c.getString("date"),c.getString("messageType"),c.getInt("seen")));
                                    System.out.println(ls.get(i).getMessage());
                                    adapter.notifyDataSetChanged();


                                }



                            } else {
                                Toast.makeText(MessageActivity.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MessageActivity.this, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                            Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                        Toast.makeText(MessageActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                int idd=mPref.getInt("id", 0);

                params.put("sender", String.valueOf(idd));
                params.put("receiver", String.valueOf(id));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);
        queue.add(request);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessage();
    }

    //
//    @Override
//    protected void onResume() {
//        super.onResume();
//        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
//        Query query = ref.child("user").orderByChild("id").equalTo(user.getUid());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
//                    HashMap<String, Object> hashMap=new HashMap<>();
//                    hashMap.put("status","online");
//                    appleSnapshot.getRef().updateChildren(hashMap);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @Override
//    protected void onPause() {
//
//
//        super.onPause();
//
//        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
//        Query query = ref.child("user").orderByChild("id").equalTo(user.getUid());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
//                    HashMap<String, Object> hashMap=new HashMap<>();
//                    hashMap.put("status","offline");
//                    appleSnapshot.getRef().updateChildren(hashMap);
//                    HashMap<String, Object> hashMap1=new HashMap<>();
//                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//                    LocalDateTime now = LocalDateTime.now();
//                    String lastSeen="Last Seen "+ now.format(dtf);
//                    hashMap1.put("lastSeen",lastSeen);
//                    appleSnapshot.getRef().updateChildren(hashMap1);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}