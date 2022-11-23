package com.ass2.i190426_i190435;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    ImageButton send, voice;
    ImageView image;
    boolean isVoice=false;
    MediaRecorder recorder;
    String fileName;
    Handler handler = new Handler();
    Runnable runnable;

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
        image=findViewById(R.id.image);
        voice=findViewById(R.id.voice);

        adapter=new MessageAdapter(ls, MessageActivity.this);
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager lm=new LinearLayoutManager(MessageActivity.this);
        rv.setLayoutManager(lm);
        getMessage();

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

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File music = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music, "audio"+".mp3");
        fileName=file.getPath();

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isVoice){
                    isVoice=false;
                    voice.setImageResource(R.drawable.ic_baseline_mic_24);
                    stopRecording();
                }
                else{
                    isVoice=true;
                    voice.setImageResource(R.drawable.ic_baseline_delete_24);
                    if (ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MessageActivity.this, new String[] { Manifest.permission.RECORD_AUDIO },
                                10);
                    } else {
                        startRecording();
                    }
                    Toast.makeText(MessageActivity.this, "Recording started", Toast.LENGTH_LONG).show();

                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(i, "Choose your Dp"),
                        200
                );
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(isVoice){
                    isVoice=false;
                    voice.setImageResource(R.drawable.ic_baseline_mic_24);
                    stopRecording();



                    try{
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
                        int read;
                        byte[] buff = new byte[1024];
                        while ((read = in.read(buff)) > 0)
                        {
                            out.write(buff, 0, read);
                        }
                        out.flush();
                        byte[] audioBytes = out.toByteArray();
                        final String audioData=Base64.getEncoder().encodeToString(audioBytes);
                        sendMessage(mPref.getInt("id", 0), id,audioData, "Audio");
                    }
                    catch (Exception e){

                    }

                }
                else {
                    if(msg.getText().toString().trim().equals("")){
                        Toast.makeText(MessageActivity.this, "Message is Empty", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        sendMessage(mPref.getInt("id", 0), id,msg.getText().toString(), "Text");
                        msg.setText("");
                    }
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
//                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {


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

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);
        queue.add(request);


        getMessage();


    }
    public void getMessage(){



        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/getChatbyId.php",
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
//                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {

                                JSONArray chats=res.getJSONArray("chats");
                                ls.clear();
                                for (int i=0; i<chats.length(); i++){
                                    JSONObject c=chats.getJSONObject(i);

                                    ls.add(new Chat(c.getInt("sender"),c.getInt("receiver"), c.getString("message"), c.getString("date"),c.getString("messageType"),c.getInt("seen")));
//                                    System.out.println(ls.get(i).getMessage());
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
                        error.printStackTrace();
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

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);
        queue.add(request);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void toOnline(String st){
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
                params.put("id", String.valueOf(mPref.getInt("id", 0)));
                params.put("lastSeen", lastSeen);
                params.put("status1", st);


                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);
        queue.add(request);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {

        getMessage();
        updateToseen();
        toOnline("online");


        handler.postDelayed(runnable =new Runnable() {
            public void run() {
//                Toast.makeText(MessageActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                getMessage();
                updateToseen();
                getUserStatus();
                handler.postDelayed(runnable, 3000);
            }
        }, 3000);
        super.onResume();

    }



    public void getUserStatus(){
//        Toast.makeText(MessageActivity.this, "1", Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/getUserbyId.php",
                new Response.Listener<String>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
//                        Toast.makeText(MessageActivity.this, "2", Toast.LENGTH_SHORT).show();


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {
                                JSONObject user=res.getJSONObject("user");
                                seenUser=user.getString("lastSeen");
                                statusUser=user.getString("status1");

//                                Toast.makeText(MessageActivity.this, statusUser, Toast.LENGTH_SHORT).show();

                                if(statusUser.equals("offline")){
                                    seen.setText(seenUser);
                                }
                                else{
                                    seen.setText(statusUser);
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


                params.put("id", String.valueOf(id));


                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);
        queue.add(request);

    }

    public void updateToseen(){
        StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/updateSeen.php",
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);


                        try {
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("reqcode") == 1) {





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
                        error.printStackTrace();
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
                params.put("seen", "1");

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);
        queue.add(request);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==200 && resultCode==RESULT_OK){

            List<String> imagePathList = new ArrayList<>();

            if (data.getClipData() != null) {

                int count = data.getClipData().getItemCount();
                for (int i=0; i<count; i++) {
                    Uri imgUri = data.getClipData().getItemAt(i).getUri();
                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(imgUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        final String imageData=Base64.getEncoder().encodeToString(byteArray);
                        sendMessage(mPref.getInt("id", 0), id,imageData, "Image");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (data.getData() != null) {
                Uri imgUri = data.getData();
                final InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(imgUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();
                    final String imageData=Base64.getEncoder().encodeToString(byteArray);
                    sendMessage(mPref.getInt("id", 0), id,imageData, "Image");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }

            /*try{
                Uri dpp=data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(dpp);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                LayoutInflater factory = LayoutInflater.from(MessageActivity.this);
                final View view1 = factory.inflate(R.layout.image_pop_up, null);
                ImageView myimg = view1.findViewById(R.id.myimg);
                myimg.setImageBitmap(selectedImage);
                Button send = view1.findViewById(R.id.send);
                Button cancel = view1.findViewById(R.id.cancel);




                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this).setView(view1);

                AlertDialog dialog=builder.create();

                dialog.show();

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
            catch (Exception e){

            }*/

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            }else{
                //User denied Permission.
            }
        }
    }

    private void startRecording() {
        Toast.makeText(MessageActivity.this,fileName, Toast.LENGTH_SHORT).show();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            //Toast.makeText(RecordMusic.this, fileName, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        toOnline("offline");
        super.onPause();
    }



}