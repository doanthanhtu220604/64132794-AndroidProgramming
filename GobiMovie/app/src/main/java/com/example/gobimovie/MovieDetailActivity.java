package com.example.gobimovie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView MovieThumbnailImg, MovieCoverImg;
    private TextView tv_title, tv_description;

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

        // Lấy dữ liệu từ Intent và hiển thị
        iniView();

        // Thêm sự kiện nhấn cho FloatingActionButton
        FloatingActionButton playButton = findViewById(R.id.floatingActionButton3);
        playButton.setOnClickListener(v -> {
            // Lấy extras từ Intent
            Bundle extras = getIntent().getExtras();
            // Chuẩn bị Intent để mở MoviePlayerActivity
            Intent intent = new Intent(MovieDetailActivity.this, MoviePlayerActivity.class);
            // Truyền URL video từ Tlink (lấy từ extras)
            String videoUrl = extras != null ? extras.getString("videoURL", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") : "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            intent.putExtra("VIDEO_URL", videoUrl);
            startActivity(intent);
        });
    }

    void iniView() {
        Bundle extras = getIntent().getExtras();
        String movieTitle = extras != null ? extras.getString("title", "") : "";
        String imageUrl = extras != null ? extras.getString("imgURL", "") : ""; // Lấy URL dưới dạng chuỗi
        String description = extras != null ? extras.getString("description", "") : ""; // Lấy mô tả
        String videoUrl = extras != null ? extras.getString("videoURL", "") : ""; // Lấy URL video

        // Khởi tạo ImageView
        MovieThumbnailImg = findViewById(R.id.detail_movie_img);
        // Sử dụng Glide để tải hình ảnh từ URL
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.cuochonnhanhoanhoa) // Hình ảnh mặc định nếu URL không hợp lệ
                .into(MovieThumbnailImg);

        // MovieCoverImg không được sử dụng vì Intent không truyền imgCover
        MovieCoverImg = findViewById(R.id.detail_movie_cover);
        // Nếu bạn muốn hiển thị cover, cần truyền thêm từ HomeFragment, tạm thời đặt mặc định
        Glide.with(this)
                .load(imageUrl) // Dùng cùng URL với thumbnail
                .placeholder(R.drawable.cuochonnhanhoanhoa)
                .into(MovieCoverImg);

        // Đặt tiêu đề
        tv_title = findViewById(R.id.detail_movie_title);
        tv_title.setText(movieTitle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(movieTitle);
        }

        // Đặt mô tả
        tv_description = findViewById(R.id.detail_movie_desc);
        tv_description.setText(description);
    }
}