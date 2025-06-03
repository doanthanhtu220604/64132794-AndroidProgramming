package com.example.gobimovie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView MovieThumbnailImg, MovieCoverImg;
    private TextView tv_title, tv_description;
    private Button buttonWatchMovie, buttonViewDetails, buttonAddToFavorites;
    private ImageButton buttonBack;
    private String movieGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo giao diện và hiển thị dữ liệu
        iniView();

        // Thiết lập sự kiện cho nút "Trở về"
        buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> finish());

        // Thiết lập sự kiện cho nút "Xem Phim"
        buttonWatchMovie = findViewById(R.id.button_watch_movie);
        buttonWatchMovie.setOnClickListener(v -> {
            String videoUrl = getIntent().getStringExtra("videoURL");
            String title = getIntent().getStringExtra("title");
            String imageUrl = getIntent().getStringExtra("imgURL");

            if (videoUrl != null && !videoUrl.isEmpty()) {
                Intent intent = new Intent(MovieDetailActivity.this, MoviePlayerActivity.class);
                intent.putExtra("VIDEO_URL", videoUrl);
                intent.putExtra("VIDEO_TITLE", title);
                intent.putExtra("IMG_URL", imageUrl);
                startActivity(intent);
            } else {
                Toast.makeText(MovieDetailActivity.this, "Không tìm thấy URL video!", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập sự kiện cho nút "Xem Chi Tiết"
        buttonViewDetails = findViewById(R.id.button_view_details);
        buttonViewDetails.setOnClickListener(v -> {
            String title = getIntent().getStringExtra("title");
            String description = getIntent().getStringExtra("description");
            Intent intent = new Intent(MovieDetailActivity.this, MovieFullDetailsActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("genre", movieGenre);
            startActivity(intent);
        });

        // Thiết lập sự kiện cho nút "Thêm vào yêu thích"
        buttonAddToFavorites = findViewById(R.id.button_fav);
        buttonAddToFavorites.setOnClickListener(v -> addToFavorites());
    }

    void iniView() {
        String movieTitle = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("imgURL");
        String description = getIntent().getStringExtra("description");

        MovieThumbnailImg = findViewById(R.id.detail_movie_img);
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.cuochonnhanhoanhoa)
                .into(MovieThumbnailImg);

        MovieCoverImg = findViewById(R.id.detail_movie_cover);

        tv_title = findViewById(R.id.detail_movie_title);
        if (movieTitle != null) {
            tv_title.setText(movieTitle);
        }

        tv_description = findViewById(R.id.detail_movie_desc);
        if (description != null) {
            tv_description.setText(description);
        }

        if (movieTitle != null) {
            fetchMovieDetailsFromFirebase(movieTitle);
        }
    }

    private void fetchMovieDetailsFromFirebase(String movieTitle) {
        DatabaseReference featuredRef = FirebaseDatabase.getInstance().getReference("featured");
        featuredRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean foundInFeatured = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Ftitle").getValue(String.class);
                    if (title != null && title.equals(movieTitle)) {
                        movieGenre = dataSnapshot.child("Fgenre").getValue(String.class);
                        String thumbnail = dataSnapshot.child("Fthumbnail").getValue(String.class);
                        if (thumbnail != null) {
                            Glide.with(MovieDetailActivity.this)
                                    .load(thumbnail)
                                    .placeholder(R.drawable.cuochonnhanhoanhoa)
                                    .into(MovieCoverImg);
                        } else {
                            Log.d("MovieDetailActivity", "Thumbnail not found in Firebase for featured.");
                        }
                        Log.d("MovieDetailActivity", "Fetched Genre from Firebase (featured): " + movieGenre);
                        foundInFeatured = true;
                        break;
                    }
                }

                if (!foundInFeatured) {
                    DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
                    seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String title = dataSnapshot.child("Stitle").getValue(String.class);
                                if (title != null && title.equals(movieTitle)) {
                                    movieGenre = dataSnapshot.child("Sgenre").getValue(String.class);
                                    String thumbnail = dataSnapshot.child("Sthumbnail").getValue(String.class);
                                    if (thumbnail != null) {
                                        Glide.with(MovieDetailActivity.this)
                                                .load(thumbnail)
                                                .placeholder(R.drawable.cuochonnhanhoanhoa)
                                                .into(MovieCoverImg);
                                    } else {
                                        Log.d("MovieDetailActivity", "Thumbnail not found in Firebase for series.");
                                    }
                                    Log.d("MovieDetailActivity", "Fetched Genre from Firebase (series): " + movieGenre);
                                    break;
                                }
                            }
                            if (movieGenre == null) {
                                movieGenre = "Không xác định";
                                Log.d("MovieDetailActivity", "Genre not found in Firebase for series.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(MovieDetailActivity.this, "Lỗi khi lấy dữ liệu từ series: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            movieGenre = "Không xác định";
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MovieDetailActivity.this, "Lỗi khi lấy dữ liệu từ featured: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                movieGenre = "Không xác định";
            }
        });
    }

    private void addToFavorites() {
        String title = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("imgURL");
        String description = getIntent().getStringExtra("description");
        String videoUrl = getIntent().getStringExtra("videoURL");

        if (title == null || title.isEmpty()) {
            Toast.makeText(this, "Không thể thêm phim do thiếu tiêu đề!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject movieJson = new JSONObject();
            movieJson.put("title", title);
            movieJson.put("thumbnail", imageUrl != null ? imageUrl : "");
            movieJson.put("description", description != null ? description : "");
            movieJson.put("videoUrl", videoUrl != null ? videoUrl : "");
            movieJson.put("genre", movieGenre != null ? movieGenre : "Không xác định");

            SharedPreferences prefs = getSharedPreferences("FavoriteMovies", MODE_PRIVATE);
            Set<String> favoriteMoviesSet = new HashSet<>(prefs.getStringSet("favorite_movies", new HashSet<>()));

            // Kiểm tra xem phim đã có trong danh sách yêu thích chưa
            boolean isAlreadyFavorite = false;
            for (String json : favoriteMoviesSet) {
                try {
                    JSONObject existingMovie = new JSONObject(json);
                    if (existingMovie.getString("title").equals(title)) {
                        isAlreadyFavorite = true;
                        break;
                    }
                } catch (JSONException e) {
                    Log.e("MovieDetailActivity", "Lỗi khi phân tích JSON: " + e.getMessage());
                }
            }

            if (isAlreadyFavorite) {
                Toast.makeText(this, "Phim đã có trong danh sách yêu thích!", Toast.LENGTH_SHORT).show();
            } else {
                favoriteMoviesSet.add(movieJson.toString());
                prefs.edit().putStringSet("favorite_movies", favoriteMoviesSet).apply();
                Toast.makeText(this, "Đã thêm phim vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("MovieDetailActivity", "Lỗi khi tạo JSON: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi thêm phim vào yêu thích", Toast.LENGTH_SHORT).show();
        }
    }
}