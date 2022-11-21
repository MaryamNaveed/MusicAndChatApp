package com.ass2.i190426_i190435;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import java.util.Base64;
import java.util.List;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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

        MediaPlayer mediaPlayer = new MediaPlayer();
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


                    msg=itemView.findViewById(R.id.msg);
                    date=itemView.findViewById(R.id.date);

                    img=itemView.findViewById(R.id.img);
                    seekBar=itemView.findViewById(R.id.seekBar);
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
