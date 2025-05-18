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
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;

public class MoviePlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    private String videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"; // URL mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        // Kiểm tra URL từ Intent
        if (getIntent().hasExtra("VIDEO_URL")) {
            videoUrl = getIntent().getStringExtra("VIDEO_URL");
        }

        initExoPlayer();
        hideActionBar();
    }

    private void hideActionBar() {
        // Kiểm tra xem ActionBar có tồn tại trước khi ẩn
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

        // Khởi tạo ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Tạo DataSource Factory
        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);

        // Tạo ProgressiveMediaSource
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));

        // Chuẩn bị và phát video
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);

        // Xử lý lỗi và trạng thái
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