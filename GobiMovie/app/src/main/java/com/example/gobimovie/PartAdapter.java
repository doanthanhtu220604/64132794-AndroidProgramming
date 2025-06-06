package com.example.gobimovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        holder.itemView.setOnClickListener(v -> {
            // Mở MoviePlayerActivity
            Intent intent = new Intent(context, MoviePlayerActivity.class);
            intent.putExtra("VIDEO_URL", part.getVidUrl());
            context.startActivity(intent);

            // Lưu vào danh sách đã xem (chỉ lưu tên tập và ảnh tập)
            SharedPreferences prefs = context.getSharedPreferences("WatchedMovies", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Lấy danh sách hiện tại (nếu có), nếu không thì tạo mới
            Set<String> watchedSet = new HashSet<>(prefs.getStringSet("watched_movies", new HashSet<>()));

            try {
                JSONObject json = new JSONObject();
                json.put("title", part.getPart());       // ✅ Tên tập phim
                json.put("thumbnail", part.getUrl());    // ✅ Ảnh tập phim
                json.put("videoUrl", part.getVidUrl());  // ✅ Link video

                watchedSet.add(json.toString());

                editor.putStringSet("watched_movies", watchedSet);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
