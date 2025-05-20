package com.example.gobimovie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SlidePageAdapter extends RecyclerView.Adapter<SlidePageAdapter.SlideViewHolder> {

    private List<Slide> slideList;

    public SlidePageAdapter(List<Slide> slideList) {
        this.slideList = slideList;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_home, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        Slide slide = slideList.get(position);
        // Sử dụng Glide để tải hình ảnh từ URL
        Glide.with(holder.itemView.getContext())
                .load(slide.getTurl()) // Turl là URL của hình ảnh
                .into(holder.imageView);
        // Đặt tiêu đề từ Ttitle
        holder.textView.setText(slide.getTtitle());
    }

    @Override
    public int getItemCount() {
        return slideList.size();
    }

    public static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slide_img);
            textView = itemView.findViewById(R.id.slide_title);
        }
    }
}