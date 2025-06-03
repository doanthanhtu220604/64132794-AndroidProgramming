package com.example.gobimovie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WatchedMoviesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WatchedMovieAdapter movieAdapter;
    private List<WatchedMovie> lstWatchedMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watched_movies);

        recyclerView = findViewById(R.id.watched_movies_list);

        lstWatchedMovies = new ArrayList<>();
        movieAdapter = new WatchedMovieAdapter(lstWatchedMovies);
        recyclerView.setAdapter(movieAdapter);

        // Dùng GridLayoutManager với 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        loadWatchedMovies();
    }

    private void loadWatchedMovies() {
        SharedPreferences prefs = getSharedPreferences("WatchedMovies", MODE_PRIVATE);
        Set<String> watchedMoviesSet = prefs.getStringSet("watched_movies", new HashSet<>());

        lstWatchedMovies.clear();

        // Dùng Set tạm để loại phim trùng
        Set<String> titlesSet = new HashSet<>();

        for (String movieJson : watchedMoviesSet) {
            try {
                JSONObject json = new JSONObject(movieJson);
                String title = json.getString("title");
                String thumbnail = json.getString("thumbnail");
                String videoUrl = json.getString("videoUrl");

                if (title != null && videoUrl != null && !titlesSet.contains(title)) {
                    lstWatchedMovies.add(new WatchedMovie(title, thumbnail, videoUrl));
                    titlesSet.add(title);
                }
            } catch (JSONException e) {
                Log.e("WatchedMoviesActivity", "Lỗi khi phân tích JSON: " + e.getMessage());
            }
        }

        if (lstWatchedMovies.isEmpty()) {
            Toast.makeText(this, "Không có phim nào trong lịch sử xem.", Toast.LENGTH_SHORT).show();
        }

        movieAdapter.notifyDataSetChanged();
    }

    // Lớp dữ liệu cho phim đã xem
    private static class WatchedMovie {
        String title;
        String thumbnail;
        String videoUrl;

        WatchedMovie(String title, String thumbnail, String videoUrl) {
            this.title = title;
            this.thumbnail = thumbnail;
            this.videoUrl = videoUrl;
        }
    }

    // Adapter cho RecyclerView
    private class WatchedMovieAdapter extends RecyclerView.Adapter<WatchedMovieAdapter.ViewHolder> {
        private List<WatchedMovie> movies;

        WatchedMovieAdapter(List<WatchedMovie> movies) {
            this.movies = movies;
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
            WatchedMovie movie = movies.get(position);
            holder.titleTextView.setText(movie.title);

            if (movie.thumbnail != null && !movie.thumbnail.isEmpty()) {
                Glide.with(holder.thumbnailImageView.getContext())
                        .load(movie.thumbnail)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(holder.thumbnailImageView);
            } else {
                holder.thumbnailImageView.setImageResource(R.drawable.ic_launcher_background);
            }

            // Click mở xem phim
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(WatchedMoviesActivity.this, MoviePlayerActivity.class);
                intent.putExtra("VIDEO_URL", movie.videoUrl);
                intent.putExtra("VIDEO_TITLE", movie.title);
                intent.putExtra("IMG_URL", movie.thumbnail);
                startActivity(intent);
            });

            // Click giữ để xóa phim khỏi danh sách
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(position);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return movies.size();
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
            new AlertDialog.Builder(WatchedMoviesActivity.this)
                    .setTitle("Xóa phim")
                    .setMessage("Bạn có chắc muốn xóa phim này khỏi lịch sử xem?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        removeMovie(position);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }

        private void removeMovie(int position) {
            // Xóa khỏi danh sách bộ nhớ
            WatchedMovie movieToRemove = movies.get(position);

            SharedPreferences prefs = getSharedPreferences("WatchedMovies", MODE_PRIVATE);
            Set<String> watchedMoviesSet = new HashSet<>(prefs.getStringSet("watched_movies", new HashSet<>()));

            // Tìm và xóa movie tương ứng JSON
            String targetJson = null;
            for (String movieJson : watchedMoviesSet) {
                try {
                    JSONObject json = new JSONObject(movieJson);
                    String title = json.getString("title");
                    if (title.equals(movieToRemove.title)) {
                        targetJson = movieJson;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (targetJson != null) {
                watchedMoviesSet.remove(targetJson);
                prefs.edit().putStringSet("watched_movies", watchedMoviesSet).apply();

                movies.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(WatchedMoviesActivity.this, "Đã xóa phim khỏi lịch sử xem.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
