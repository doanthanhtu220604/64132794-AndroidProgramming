package com.example.gobimovie;

import android.content.Intent;
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

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView MovieThumbnailImg, MovieCoverImg;
    private TextView tv_title, tv_description;
    private Button buttonWatchMovie, buttonViewDetails;
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
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity và quay lại màn hình trước
            }
        });

        // Thiết lập sự kiện cho nút "Xem Phim"
        buttonWatchMovie = findViewById(R.id.button_watch_movie);
        buttonWatchMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        // Thiết lập sự kiện cho nút "Xem Chi Tiết"
        buttonViewDetails = findViewById(R.id.button_view_details);
        buttonViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ Intent
                String title = getIntent().getStringExtra("title");
                String description = getIntent().getStringExtra("description");

                // Mở MovieFullDetailsActivity và truyền dữ liệu
                Intent intent = new Intent(MovieDetailActivity.this, MovieFullDetailsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("genre", movieGenre); // Truyền thể loại lấy từ Firebase
                startActivity(intent);
            }
        });
    }

    void iniView() {
        // Lấy dữ liệu từ Intent
        String movieTitle = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("imgURL"); // Chứa Fpos
        String description = getIntent().getStringExtra("description");

        // Khởi tạo ImageView
        MovieThumbnailImg = findViewById(R.id.detail_movie_img);
        Glide.with(this)
                .load(imageUrl) // Tải poster (Fpos) vào detail_movie_img
                .placeholder(R.drawable.cuochonnhanhoanhoa)
                .into(MovieThumbnailImg);

        MovieCoverImg = findViewById(R.id.detail_movie_cover);
        // Không tải ảnh cho MovieCoverImg ở đây, sẽ tải trong fetchMovieDetailsFromFirebase

        // Đặt tiêu đề
        tv_title = findViewById(R.id.detail_movie_title);
        if (movieTitle != null) {
            tv_title.setText(movieTitle);
        }

        // Đặt mô tả
        tv_description = findViewById(R.id.detail_movie_desc);
        if (description != null) {
            tv_description.setText(description);
        }

        // Lấy dữ liệu từ Firebase dựa trên tiêu đề phim
        if (movieTitle != null) {
            fetchMovieDetailsFromFirebase(movieTitle);
        }
    }

    private void fetchMovieDetailsFromFirebase(String movieTitle) {
        // Kiểm tra node "featured"
        DatabaseReference featuredRef = FirebaseDatabase.getInstance().getReference("featured");
        featuredRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean foundInFeatured = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Ftitle").getValue(String.class);
                    if (title != null && title.equals(movieTitle)) {
                        // Lấy thể loại và thumbnail từ Firebase
                        movieGenre = dataSnapshot.child("Fgenre").getValue(String.class);
                        String thumbnail = dataSnapshot.child("Fthumbnail").getValue(String.class);

                        // Tải thumbnail vào detail_movie_cover
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
                    // Nếu không tìm thấy trong "featured", kiểm tra node "series"
                    DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
                    seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String title = dataSnapshot.child("Stitle").getValue(String.class);
                                if (title != null && title.equals(movieTitle)) {
                                    // Lấy thể loại và thumbnail từ Firebase
                                    movieGenre = dataSnapshot.child("Sgenre").getValue(String.class);
                                    String thumbnail = dataSnapshot.child("Sthumbnail").getValue(String.class);

                                    // Tải thumbnail vào detail_movie_cover
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
}