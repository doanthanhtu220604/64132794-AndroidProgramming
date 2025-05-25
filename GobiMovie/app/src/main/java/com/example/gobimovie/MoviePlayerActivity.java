package com.example.gobimovie;

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

public class MoviePlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        // Lấy URL từ Intent
        videoUrl = getIntent().getStringExtra("VIDEO_URL");
        if (videoUrl == null || videoUrl.isEmpty()) {
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"; // URL mặc định
            Toast.makeText(this, "Không có URL video, sử dụng video mặc định.", Toast.LENGTH_SHORT).show();
        }

        // Xử lý URL nếu có wrapper
        if (videoUrl.contains("?url=")) {
            videoUrl = videoUrl.split("\\?url=")[1]; // Lấy phần sau "?url="
        }

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
                } else if (state == Player.STATE_BUFFERING) {
                    Log.d("ExoPlayer", "Đang tải video...");
                } else if (state == Player.STATE_ENDED) {
                    Log.d("ExoPlayer", "Video đã kết thúc");
                }
            }
        });
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