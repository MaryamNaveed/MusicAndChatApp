package com.ass2.i190426_i190435;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

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


//
//        Query ref = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("id").equalTo(id);
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                getMessage(mAuth.getUid(), id);
//
//                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
//                    User u=appleSnapshot.getValue(User.class);
//                    seenUser=u.lastSeen;
//                    statusUser=u.status;
//
//                    if(statusUser.equals("offline")){
//                        seen.setText(seenUser);
//                    }
//                    else{
//                        seen.setText(statusUser);
//                    }
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//
//
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(int sender, int receiver,String message, String messageType){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        Chat newChat = new Chat(sender, receiver, message, dtf.format(now), messageType, 0);


    }
//    public void getMessage(String myId, String id1){
//        ls=new ArrayList<>();
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chat");
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ls.clear();
//                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    Chat c= dataSnapshot.getValue(Chat.class);
////                    Toast.makeText(MessageActivity.this, c.getSender()+" "+c.getMessage()+"  "+c.getReceiver(),Toast.LENGTH_LONG).show();
//                    if((c.getReceiver().equals(myId) && c.getSender().equals(id1)) ||
//                            ( c.getReceiver().equals(id1) && c.getSender().equals(myId))){
////                        Toast.makeText(MessageActivity.this, "Added: "+c.getMessage(),Toast.LENGTH_LONG).show();
//                        ls.add(c);
//
//                    }
//                    adapter=new MessageAdapter(ls, MessageActivity.this);
//                    rv.setAdapter(adapter);
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
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