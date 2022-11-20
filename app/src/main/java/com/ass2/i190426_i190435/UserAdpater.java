package com.ass2.i190426_i190435;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Base64;
import java.util.List;

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


    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,  msg;
        CircleImageView dp;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            msg=itemView.findViewById(R.id.msg);
            dp=itemView.findViewById((R.id.dp));
        }
    }
}

