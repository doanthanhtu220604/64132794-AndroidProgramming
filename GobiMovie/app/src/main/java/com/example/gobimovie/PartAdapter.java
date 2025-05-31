package com.example.gobimovie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.MyViewHolder> {
    private Context context;
    private List<Part> models;

    public PartAdapter(Context context, List<Part> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.part_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Part part = models.get(position);
        holder.part_name.setText(part.getPart());
        Glide.with(context).load(part.getUrl()).into(holder.part_image);

        // Xử lý click để phát video (tùy chọn)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MoviePlayerActivity.class);
            intent.putExtra("VIDEO_URL", part.getVidUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView part_image;
        TextView part_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            part_image = itemView.findViewById(R.id.part_image);
            part_name = itemView.findViewById(R.id.part_name);
        }
    }
}