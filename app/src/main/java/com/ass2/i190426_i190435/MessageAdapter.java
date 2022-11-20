package com.ass2.i190426_i190435;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


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
        else{
            View row = LayoutInflater.from(c).inflate(R.layout.chat_row_right, parent, false);
            return new MyViewHolder(row);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat=ls.get(position);
        holder.msg.setText(chat.getMessage());
        holder.date.setText(chat.getDate());

    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView msg, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            msg=itemView.findViewById(R.id.msg);
            date=itemView.findViewById(R.id.date);
        }
    }

    @Override
    public int getItemViewType(int position){
//        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//
//        if(ls.get(position).getSender().equals(firebaseUser.getUid())){
//            return 0;
//        }
//        else{
//            return 1;
//        }
        SharedPreferences mPref;
        SharedPreferences.Editor editmPref;
        mPref= c.getSharedPreferences("com.ass2.i190426_i190435", c.MODE_PRIVATE);
        editmPref=mPref.edit();
        if(ls.get(position).getSender()==mPref.getInt("id", 0)){
            return 0;

        }
        else{
            return 1;
        }



    }
}
