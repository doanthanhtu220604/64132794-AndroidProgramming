package com.example.gobimovie;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MovieFullDetailsActivity extends AppCompatActivity {

    private TextView tvMovieTitle, tvMovieGenre, tvMovieDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_full_details);

        // Ánh xạ các thành phần giao diện
        tvMovieTitle = findViewById(R.id.tv_movie_title);
        tvMovieGenre = findViewById(R.id.tv_movie_genre);
        tvMovieDescription = findViewById(R.id.tv_movie_description);

        // Lấy dữ liệu từ Intent
        String title = getIntent().getStringExtra("title");
        String genre = getIntent().getStringExtra("genre");
        String description = getIntent().getStringExtra("description");

        // Debug: Kiểm tra dữ liệu
        Log.d("MovieFullDetailsActivity", "Title: " + title + ", Genre: " + genre + ", Description: " + description);

        // Hiển thị dữ liệu
        if (title != null) tvMovieTitle.setText(title);
        if (genre != null) {
            tvMovieGenre.setText(genre);
        } else {
            tvMovieGenre.setText("Không xác định");
        }
        if (description != null) tvMovieDescription.setText(description);
    }
}