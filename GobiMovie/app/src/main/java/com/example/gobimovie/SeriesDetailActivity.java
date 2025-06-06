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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeriesDetailActivity extends AppCompatActivity {

    private ImageView seriesThumbnailImg, seriesCoverImg;
    private TextView tv_title, tv_description;
    private ImageButton buttonBack;
    private Button buttonViewDetails, buttonAddToFavorites;
    private RecyclerView rvParts;
    private PartAdapter partAdapter;
    private List<Part> partList;
    private String seriesGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_series_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> finish());

        buttonViewDetails = findViewById(R.id.button_view_details);
        buttonViewDetails.setOnClickListener(v -> {
            String title = getIntent().getStringExtra("title");
            String description = getIntent().getStringExtra("description");

            Intent intent = new Intent(SeriesDetailActivity.this, MovieFullDetailsActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("genre", seriesGenre);
            startActivity(intent);
        });

        // Thiết lập sự kiện cho nút "Thêm vào yêu thích"
        buttonAddToFavorites = findViewById(R.id.button_favsr);
        buttonAddToFavorites.setOnClickListener(v -> addToFavorites());
    }

    private void initView() {
        String seriesTitle = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("imgURL");
        String description = getIntent().getStringExtra("description");

        seriesThumbnailImg = findViewById(R.id.detail_series_img);
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.cuochonnhanhoanhoa)
                .into(seriesThumbnailImg);

        seriesCoverImg = findViewById(R.id.detail_series_cover);

        tv_title = findViewById(R.id.detail_series_title);
        if (seriesTitle != null) {
            tv_title.setText(seriesTitle);
        }

        tv_description = findViewById(R.id.detail_series_desc);
        if (description != null) {
            tv_description.setText(description);
        }

        rvParts = findViewById(R.id.rv_parts);
        partList = new ArrayList<>();
        partAdapter = new PartAdapter(this, partList);
        rvParts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvParts.setHasFixedSize(true);
        rvParts.setAdapter(partAdapter);

        if (seriesTitle != null) {
            fetchSeriesDetailsFromFirebase(seriesTitle);
        }
    }

    private void fetchSeriesDetailsFromFirebase(String seriesTitle) {
        DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
        seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Stitle").getValue(String.class);
                    if (title != null && title.equals(seriesTitle)) {
                        seriesGenre = dataSnapshot.child("Sgenre").getValue(String.class);
                        String thumbnail = dataSnapshot.child("Sthumbnail").getValue(String.class);
                        if (thumbnail != null) {
                            Glide.with(SeriesDetailActivity.this)
                                    .load(thumbnail)
                                    .placeholder(R.drawable.cuochonnhanhoanhoa)
                                    .into(seriesCoverImg);
                        }

                        DataSnapshot partsSnapshot = dataSnapshot.child("parts");
                        partList.clear();
                        for (DataSnapshot partSnapshot : partsSnapshot.getChildren()) {
                            String partName = partSnapshot.child("part").getValue(String.class);
                            String partImageUrl = partSnapshot.child("url").getValue(String.class);
                            String partVideoUrl = partSnapshot.child("vidUrl").getValue(String.class);
                            if (partName != null && partImageUrl != null && partVideoUrl != null) {
                                partList.add(new Part(partName, partImageUrl, partVideoUrl));
                            }
                        }
                        partAdapter.notifyDataSetChanged();

                        if (partList.isEmpty()) {
                            rvParts.setVisibility(View.GONE);
                            findViewById(R.id.parts_label).setVisibility(View.GONE);
                        } else {
                            rvParts.setVisibility(View.VISIBLE);
                            findViewById(R.id.parts_label).setVisibility(View.VISIBLE);
                        }

                        Log.d("SeriesDetailActivity", "Fetched Genre from Firebase: " + seriesGenre);
                        break;
                    }
                }

                if (seriesGenre == null) {
                    seriesGenre = "Không xác định";
                    Log.d("SeriesDetailActivity", "Genre not found in Firebase for series.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SeriesDetailActivity.this, "Lỗi khi lấy dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                seriesGenre = "Không xác định";
            }
        });
    }

    private void addToFavorites() {
        String title = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("imgURL");
        String description = getIntent().getStringExtra("description");

        if (title == null || title.isEmpty()) {
            Toast.makeText(this, "Không thể thêm series do thiếu tiêu đề!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject seriesJson = new JSONObject();
            seriesJson.put("title", title);
            seriesJson.put("thumbnail", imageUrl != null ? imageUrl : "");
            seriesJson.put("description", description != null ? description : "");
            seriesJson.put("videoUrl", ""); // Series không có videoUrl chung
            seriesJson.put("genre", seriesGenre != null ? seriesGenre : "Không xác định");
            seriesJson.put("isSeries", true); // Đánh dấu là series

            SharedPreferences prefs = getSharedPreferences("FavoriteMovies", MODE_PRIVATE);
            Set<String> favoriteMoviesSet = new HashSet<>(prefs.getStringSet("favorite_movies", new HashSet<>()));

            // Kiểm tra xem series đã có trong danh sách yêu thích chưa
            boolean isAlreadyFavorite = false;
            for (String json : favoriteMoviesSet) {
                try {
                    JSONObject existingItem = new JSONObject(json);
                    if (existingItem.getString("title").equals(title)) {
                        isAlreadyFavorite = true;
                        break;
                    }
                } catch (JSONException e) {
                    Log.e("SeriesDetailActivity", "Lỗi khi phân tích JSON: " + e.getMessage());
                }
            }

            if (isAlreadyFavorite) {
                Toast.makeText(this, "Series đã có trong danh sách yêu thích!", Toast.LENGTH_SHORT).show();
            } else {
                favoriteMoviesSet.add(seriesJson.toString());
                prefs.edit().putStringSet("favorite_movies", favoriteMoviesSet).apply();
                Toast.makeText(this, "Đã thêm series vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("SeriesDetailActivity", "Lỗi khi tạo JSON: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi thêm series vào yêu thích", Toast.LENGTH_SHORT).show();
        }
    }
}