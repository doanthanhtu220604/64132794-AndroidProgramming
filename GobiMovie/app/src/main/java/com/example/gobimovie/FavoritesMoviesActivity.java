package com.example.gobimovie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesMoviesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteItemAdapter itemAdapter;
    private List<FavoriteItem> lstFavoriteItems;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_movies);

        recyclerView = findViewById(R.id.favorite_movies_list);
        lstFavoriteItems = new ArrayList<>();
        itemAdapter = new FavoriteItemAdapter(lstFavoriteItems);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        // Nút quay lại
        buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> finish());

        loadFavoriteItems();
    }

    private void loadFavoriteItems() {
        SharedPreferences prefs = getSharedPreferences("FavoriteMovies", MODE_PRIVATE);
        Set<String> favoriteItemsSet = prefs.getStringSet("favorite_movies", new HashSet<>());

        lstFavoriteItems.clear();
        Set<String> titlesSet = new HashSet<>();

        for (String itemJson : favoriteItemsSet) {
            try {
                JSONObject json = new JSONObject(itemJson);
                String title = json.getString("title");
                String thumbnail = json.getString("thumbnail");
                String videoUrl = json.getString("videoUrl");
                String description = json.getString("description");
                String genre = json.getString("genre");
                boolean isSeries = json.optBoolean("isSeries", false);

                if (title != null && !titlesSet.contains(title)) {
                    lstFavoriteItems.add(new FavoriteItem(title, thumbnail, videoUrl, description, genre, isSeries));
                    titlesSet.add(title);
                }
            } catch (JSONException e) {
                Log.e("FavoritesMoviesActivity", "Lỗi khi phân tích JSON: " + e.getMessage());
            }
        }

        if (lstFavoriteItems.isEmpty()) {
            Toast.makeText(this, "Không có phim hoặc series nào trong danh sách yêu thích.", Toast.LENGTH_SHORT).show();
        }

        itemAdapter.notifyDataSetChanged();
    }

    // Lớp dữ liệu cho phim hoặc series yêu thích
    private static class FavoriteItem {
        String title;
        String thumbnail;
        String videoUrl;
        String description;
        String genre;
        boolean isSeries;

        FavoriteItem(String title, String thumbnail, String videoUrl, String description, String genre, boolean isSeries) {
            this.title = title;
            this.thumbnail = thumbnail;
            this.videoUrl = videoUrl;
            this.description = description;
            this.genre = genre;
            this.isSeries = isSeries;
        }
    }

    // Adapter cho RecyclerView
    private class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder> {
        private List<FavoriteItem> items;

        FavoriteItemAdapter(List<FavoriteItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_movie, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FavoriteItem item = items.get(position);
            holder.titleTextView.setText(item.title);

            if (item.thumbnail != null && !item.thumbnail.isEmpty()) {
                Glide.with(holder.thumbnailImageView.getContext())
                        .load(item.thumbnail)
                        .placeholder(R.drawable.cuochonnhanhoanhoa)
                        .into(holder.thumbnailImageView);
            } else {
                holder.thumbnailImageView.setImageResource(R.drawable.cuochonnhanhoanhoa);
            }

            // Click mở chi tiết
            holder.itemView.setOnClickListener(v -> {
                Intent intent;
                if (item.isSeries) {
                    intent = new Intent(FavoritesMoviesActivity.this, SeriesDetailActivity.class);
                } else {
                    intent = new Intent(FavoritesMoviesActivity.this, MovieDetailActivity.class);
                    intent.putExtra("videoURL", item.videoUrl);
                }
                intent.putExtra("title", item.title);
                intent.putExtra("imgURL", item.thumbnail);
                intent.putExtra("description", item.description);
                startActivity(intent);
            });

            // Click giữ để xóa
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(position);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            ImageView thumbnailImageView;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.item_movie_title);
                thumbnailImageView = itemView.findViewById(R.id.item_movie_img);
            }
        }

        private void showDeleteDialog(int position) {
            new AlertDialog.Builder(FavoritesMoviesActivity.this)
                    .setTitle("Xóa mục")
                    .setMessage("Bạn có chắc muốn xóa mục này khỏi danh sách yêu thích?")
                    .setPositiveButton("Xóa", (dialog, which) -> removeItem(position))
                    .setNegativeButton("Hủy", null)
                    .show();
        }

        private void removeItem(int position) {
            FavoriteItem itemToRemove = items.get(position);

            SharedPreferences prefs = getSharedPreferences("FavoriteMovies", MODE_PRIVATE);
            Set<String> favoriteItemsSet = new HashSet<>(prefs.getStringSet("favorite_movies", new HashSet<>()));

            String targetJson = null;
            for (String itemJson : favoriteItemsSet) {
                try {
                    JSONObject json = new JSONObject(itemJson);
                    String title = json.getString("title");
                    if (title.equals(itemToRemove.title)) {
                        targetJson = itemJson;
                        break;
                    }
                } catch (JSONException e) {
                    Log.e("FavoritesMoviesActivity", "Lỗi khi phân tích JSON: " + e.getMessage());
                }
            }
            if (targetJson != null) {
                favoriteItemsSet.remove(targetJson);
                prefs.edit().putStringSet("favorite_movies", favoriteItemsSet).apply();

                items.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(FavoritesMoviesActivity.this, "Đã xóa khỏi danh sách yêu thích.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}