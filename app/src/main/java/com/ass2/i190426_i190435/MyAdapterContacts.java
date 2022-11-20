package com.ass2.i190426_i190435;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapterContacts extends RecyclerView.Adapter<MyAdapterContacts.MyViewHolder> {
    List<User> ls;
    Context c;

    public MyAdapterContacts(List<User> ls, Context c) {
        this.ls = ls;
        this.c = c;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(c).inflate(R.layout.contacts_row, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(ls.get(position).getName());
        holder.num.setText(ls.get(position).getPhone());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





            }});
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, num, msg;
        CircleImageView dp;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            num=itemView.findViewById(R.id.num);
            dp=itemView.findViewById((R.id.dp));
        }
    }
}

