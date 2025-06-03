package com.example.gobimovie;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class MoviePlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    private String videoUrl;
    private String movieTitle;
    private String thumbnailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        videoUrl = getIntent().getStringExtra("VIDEO_URL");
        movieTitle = getIntent().getStringExtra("VIDEO_TITLE");
        thumbnailUrl = getIntent().getStringExtra("IMG_URL");

        if (videoUrl == null || videoUrl.isEmpty()) {
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            Toast.makeText(this, "Không có URL video, sử dụng video mặc định.", Toast.LENGTH_SHORT).show();
        }

        if (movieTitle == null || movieTitle.isEmpty()) {
            movieTitle = "Phim không có tiêu đề";
        }
        if (thumbnailUrl == null) {
            thumbnailUrl = "";
        }

        if (videoUrl.contains("?url=")) {
            videoUrl = videoUrl.split("\\?url=")[1];
        }

        // Không cần hiển thị tiêu đề trong màn hình xem phim (theo yêu cầu)

        initExoPlayer();
        hideActionBar();
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initExoPlayer() {
        playerView = findViewById(R.id.movie_exo_player);
        if (playerView == null) {
            Log.e("ExoPlayer", "PlayerView is null. Check your layout XML.");
            return;
        }

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem);

        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("ExoPlayer", "Error: " + error.getMessage());
                Toast.makeText(MoviePlayerActivity.this, "Lỗi phát video: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    Log.d("ExoPlayer", "Video sẵn sàng phát");
                    saveWatchedMovie();
                } else if (state == Player.STATE_BUFFERING) {
                    Log.d("ExoPlayer", "Đang tải video...");
                } else if (state == Player.STATE_ENDED) {
                    Log.d("ExoPlayer", "Video đã kết thúc");
                }
            }
        });
    }

    private void saveWatchedMovie() {
        SharedPreferences prefs = getSharedPreferences("WatchedMovies", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Tạo HashSet mới từ Set lấy ra để tránh lỗi khi sửa trực tiếp
        Set<String> watchedMovies = new HashSet<>(prefs.getStringSet("watched_movies", new HashSet<>()));

        try {
            JSONObject movieJson = new JSONObject();
            movieJson.put("title", movieTitle);
            movieJson.put("thumbnail", thumbnailUrl);
            movieJson.put("videoUrl", videoUrl);

            watchedMovies.add(movieJson.toString());
            editor.putStringSet("watched_movies", watchedMovies);
            editor.apply();
            Log.d("MoviePlayerActivity", "Đã lưu phim: " + movieTitle);
        } catch (JSONException e) {
            Log.e("MoviePlayerActivity", "Lỗi khi lưu phim vào SharedPreferences: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi lưu phim vào lịch sử.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
