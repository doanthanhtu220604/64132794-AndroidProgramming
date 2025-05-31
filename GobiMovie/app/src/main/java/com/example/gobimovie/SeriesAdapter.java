package com.example.gobimovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.MyViewHolder> {
    private Context context;
    private List<Series> sData;
    private SeriesItemClickListener seriesItemClickListener;

    public SeriesAdapter(Context context, List<Series> sData, SeriesItemClickListener listener) {
        this.context = context;
        this.sData = sData;
        this.seriesItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Series series = sData.get(position);
        holder.tvTitle.setText(series.getStitle());
        Glide.with(context)
                .load(series.getSpos())
                .into(holder.imgSeries);
    }

    @Override
    public int getItemCount() {
        return sData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ImageView imgSeries;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.item_movie_title);
            imgSeries = itemView.findViewById(R.id.item_movie_img);

            itemView.setOnClickListener(v -> seriesItemClickListener.onSeriesClick(sData.get(getAdapterPosition()), imgSeries));
        }
    }

    public interface SeriesItemClickListener {
        void onSeriesClick(Series series, ImageView imageView);
    }
}