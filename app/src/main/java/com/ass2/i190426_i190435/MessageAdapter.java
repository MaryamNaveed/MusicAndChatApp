package com.ass2.i190426_i190435;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
    List<Chat> ls;
    Context c;


    public MessageAdapter(List<Chat> ls, Context c) {
        this.ls=ls;
        this.c=c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1){
            View row = LayoutInflater.from(c).inflate(R.layout.chat_row_left, parent, false);
            return new MyViewHolder(row);
        }
        else if(viewType==0){
            View row = LayoutInflater.from(c).inflate(R.layout.chat_row_right, parent, false);
            return new MyViewHolder(row);

        }
        else if(viewType==2){
            View row = LayoutInflater.from(c).inflate(R.layout.image_row_right, parent, false);
            return new MyViewHolder(row);
        }
        else if(viewType==3){
            View row = LayoutInflater.from(c).inflate(R.layout.image_row_left, parent, false);
            return new MyViewHolder(row);

        }

        else if(viewType==4){
            View row = LayoutInflater.from(c).inflate(R.layout.voice_row_right, parent, false);
            return new MyViewHolder(row);
        }
        else{
            View row = LayoutInflater.from(c).inflate(R.layout.voice_row_left, parent, false);
            return new MyViewHolder(row);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Chat chat=ls.get(position);

        if(getItemViewType(position)==0 || getItemViewType(position)==1){
            holder.msg.setText(chat.getMessage());
            holder.date.setText(chat.getDate());

        }
        else if(getItemViewType(position)==2 || getItemViewType(position)==3){
            byte[] imageData= Base64.getDecoder().decode(chat.getMessage());
            Bitmap dppp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            holder.img.setImageBitmap(dppp);
            holder.date.setText(chat.getDate());
        }

        else if(getItemViewType(position)==4 || getItemViewType(position)==5){
            ((Activity) c).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(holder.mediaPlayer != null){
                        holder.songDuration=holder.mediaPlayer.getDuration();
                        holder.seekBar.setMax(holder.songDuration/100);
                        int mCurrentPosition = holder.mediaPlayer.getCurrentPosition() / 100;
                        holder.seekBar.setProgress(mCurrentPosition);
                    }
                    holder.mHandler.postDelayed(this, 100);
                }
            });

            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(holder.mediaPlayer != null && fromUser){
                        holder.mediaPlayer.seekTo(progress * 1000);
                    }
                }
            });

            holder.date.setText(chat.getDate());
            String url = "data:audio/mp3;base64,"+chat.getMessage();
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.firstTime){
                        holder.firstTime=false;
                        holder.mediaPlayer.reset();
                        holder.mediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        );
                        try {
                            holder.mediaPlayer.setDataSource(url);
                            holder.mediaPlayer.prepare();
                            holder.mediaPlayer.start();
                            holder.isPause=false;
                            holder.img.setImageResource(R.drawable.pause);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else if(holder.isPause) {
                        try {
                            holder.mediaPlayer.seekTo(holder.pauseLength);
                            holder.mediaPlayer.start();
                            holder.isPause=false;
                            holder.img.setImageResource(R.drawable.pause);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        holder.mediaPlayer.pause();
                        holder.pauseLength=holder.mediaPlayer.getCurrentPosition();
                        holder.isPause=true;
                        holder.img.setImageResource(R.drawable.play);
                    }


                }
            });

            holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    holder.img.setImageResource(R.drawable.play);
                    holder.isPause=true;
                    holder.pauseLength=0;


                }
            });


        }

        if(getItemViewType(position)==0 || getItemViewType(position)==2 || getItemViewType(position)==4){

            holder.totalMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Toast.makeText(c, "cliked", Toast.LENGTH_LONG).show();

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();

                    String strDate = now.format(dtf);

                    LocalDateTime dateNow= LocalDateTime.parse(strDate, dtf);
                    System.out.println(ls.get(position).getDate());
                    LocalDateTime dateMsg = LocalDateTime.parse(ls.get(position).getDate(), dtf);

                    long minutes = ChronoUnit.MINUTES.between(dateMsg, dateNow);


                    if(minutes<=5){
                        LayoutInflater factory = LayoutInflater.from(c);
                        final View view1 = factory.inflate(R.layout.msg_pop_up, null);
                        Button edit = view1.findViewById(R.id.edit);
                        Button delete = view1.findViewById(R.id.delete);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Chat newChat = new Chat(ls.get(position).getSender(), ls.get(position).getReceiver(), ls.get(position).getMessage(), ls.get(position).getDate(), ls.get(position).getMessageType(), ls.get(position).getSeen());


                                StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/deleteChat.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
//                        System.out.println(response);


                                                try {
                                                    JSONObject res = new JSONObject(response);


                                                        Toast.makeText(c, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(c, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                                                }


                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(c, "Connection Error", Toast.LENGTH_LONG).show();

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

                                RequestQueue queue = Volley.newRequestQueue(c);
                                queue.add(request);

                            }
                        });




                        AlertDialog.Builder builder = new AlertDialog.Builder(c).setView(view1);

                        AlertDialog dialog=builder.create();

                        dialog.show();


                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();


                                if(ls.get(position).getMessageType().equals("Text")){

                                    LayoutInflater factory = LayoutInflater.from(c);
                                    final View view1 = factory.inflate(R.layout.text_pop_up, null);
                                    EditText mytext = view1.findViewById(R.id.mytext);
                                    Button send= view1.findViewById(R.id.send);
                                    Button cancel= view1.findViewById(R.id.cancel);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(c).setView(view1);

                                    AlertDialog dialog=builder.create();

                                    dialog.show();

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();

                                        }
                                    });

                                    send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();

                                            Chat newChat = new Chat(ls.get(position).getSender(), ls.get(position).getReceiver(), ls.get(position).getMessage(), ls.get(position).getDate(), ls.get(position).getMessageType(), ls.get(position).getSeen());


                                            StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/editChat.php",
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {

                                                            try {
                                                                JSONObject res = new JSONObject(response);


                                                                Toast.makeText(c, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                                Toast.makeText(c, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                                                            }


                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Toast.makeText(c, "Connection Error", Toast.LENGTH_LONG).show();

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
                                                    params.put("newMsg", mytext.getText().toString());



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


                                    });

                                }
                                else if(ls.get(position).getMessageType().equals("Image")){



                                        LayoutInflater factory = LayoutInflater.from(c);
                                        final View view1 = factory.inflate(R.layout.image_pop_up, null);
                                        ImageView myimg=  view1.findViewById(R.id.myimg);
                                        Button send= view1.findViewById(R.id.send);
                                        Button cancel= view1.findViewById(R.id.cancel);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(c).setView(view1);

                                        AlertDialog dialog=builder.create();

                                        dialog.show();



                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();

                                            }
                                        });

                                        myimg.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                if (c instanceof MessageActivity) {
                                                    ((MessageActivity) c).getImage(myimg);
                                                    holder.image1=true;
                                                }

                                            }
                                        });

                                        send.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                if(holder.image1){
                                                    holder.image1=false;
                                                    dialog.dismiss();
                                                    Bitmap bmp = ((BitmapDrawable)myimg.getDrawable()).getBitmap();
                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                    byte[] byteArray = stream.toByteArray();
                                                    final  String selectedImage =Base64.getEncoder().encodeToString(byteArray);
                                                    Chat newChat = new Chat(ls.get(position).getSender(), ls.get(position).getReceiver(), ls.get(position).getMessage(), ls.get(position).getDate(), ls.get(position).getMessageType(), ls.get(position).getSeen());


                                                    StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/editChat.php",
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {

                                                                    try {
                                                                        JSONObject res = new JSONObject(response);


                                                                        Toast.makeText(c, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        Toast.makeText(c, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                                                                    }


                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Toast.makeText(c, "Connection Error", Toast.LENGTH_LONG).show();

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
                                                            params.put("newMsg",selectedImage );



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
                                                else {
                                                    Toast.makeText(c, "Please select an image", Toast.LENGTH_SHORT).show();
                                                }




                                            }


                                        });

                                    }
                                else if(ls.get(position).getMessageType().equals("Audio")){

                                    LayoutInflater factory = LayoutInflater.from(c);
                                    final View view1 = factory.inflate(R.layout.audio_pop_up, null);

                                    Button send= view1.findViewById(R.id.send);
                                    Button cancel= view1.findViewById(R.id.cancel);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(c).setView(view1);

                                    AlertDialog dialog=builder.create();

                                    dialog.show();

                                    if (c instanceof MessageActivity) {
                                        ((MessageActivity) c).startRecording();
                                        holder.image1=true;
                                    }

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(holder.image1){
                                                ((MessageActivity) c).stopRecording();
                                            }
                                            dialog.dismiss();
                                            ((MessageActivity) c).isVoice=false;

                                        }
                                    });

                                    send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                            holder.image1 = false;


                                            if (c instanceof MessageActivity) {
                                                ((MessageActivity) c).stopRecording();
                                                ((MessageActivity) c).isVoice=false;
                                                holder.image1 = true;
                                                try {
                                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(((MessageActivity) c).fileName));
                                                    int read;
                                                    byte[] buff = new byte[1024];
                                                    while ((read = in.read(buff)) > 0) {
                                                        out.write(buff, 0, read);
                                                    }
                                                    out.flush();
                                                    byte[] audioBytes = out.toByteArray();
                                                    final String msg = Base64.getEncoder().encodeToString(audioBytes);
                                                    Chat newChat = new Chat(ls.get(position).getSender(), ls.get(position).getReceiver(), ls.get(position).getMessage(), ls.get(position).getDate(), ls.get(position).getMessageType(), ls.get(position).getSeen());


                                                    StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/editChat.php",
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {

                                                                    try {
                                                                        JSONObject res = new JSONObject(response);


                                                                        Toast.makeText(c, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        Toast.makeText(c, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                                                                    }


                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Toast.makeText(c, "Connection Error", Toast.LENGTH_LONG).show();

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
                                                            params.put("newMsg", msg);



                                                            return params;
                                                        }
                                                    };

                                                    request.setRetryPolicy(new DefaultRetryPolicy(
                                                            50000,
                                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                                    RequestQueue queue = Volley.newRequestQueue(c);
                                                    queue.add(request);



                                                } catch (Exception e) {

                                                }


                                            }


                                        }


                                    });

                                }





                            }

                        });
                    }
                    else{
                        Toast.makeText(c, "Cannot Edit or  Delete Message", Toast.LENGTH_LONG).show();

                    }

                }
            });



        }






    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView msg, date;
        ImageView img;
        SeekBar seekBar;
        int songDuration=0;
        Handler mHandler = new Handler();
        int pauseLength=0;
        boolean isPause=false, firstTime=true;
        LinearLayout totalMsg;
        boolean image1=false;

        MediaPlayer mediaPlayer = new MediaPlayer();
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


                    msg=itemView.findViewById(R.id.msg);
                    date=itemView.findViewById(R.id.date);

                    img=itemView.findViewById(R.id.img);
                    seekBar=itemView.findViewById(R.id.seekBar);

                    totalMsg=itemView.findViewById(R.id.totalMsg);
        }
    }

    @Override
    public int getItemViewType(int position){

        SharedPreferences mPref;

        SharedPreferences.Editor editmPref;
        mPref= c.getSharedPreferences("com.ass2.i190426_i190435", c.MODE_PRIVATE);
        editmPref=mPref.edit();

        if(ls.get(position).getSender()==mPref.getInt("id", 0) && ls.get(position).getMessageType().equals("Text")){
            return 0;

        }
        else if(ls.get(position).getMessageType().equals("Text")){
            return 1;
        }

        else if(ls.get(position).getSender()==mPref.getInt("id", 0) && ls.get(position).getMessageType().equals("Image")){
            return 2;

        }
        else if(ls.get(position).getMessageType().equals("Image")){
            return 3;
        }

        else if(ls.get(position).getSender()==mPref.getInt("id", 0) && ls.get(position).getMessageType().equals("Audio")){
            return 4;

        }
        else{
            return 5;
        }



    }
}
