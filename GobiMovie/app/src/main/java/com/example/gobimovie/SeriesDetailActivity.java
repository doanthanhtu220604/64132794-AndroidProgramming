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

import java.util.ArrayList;
import java.util.List;

public class SeriesDetailActivity extends AppCompatActivity {

    private ImageView seriesThumbnailImg, seriesCoverImg;
    private TextView tv_title, tv_description;
    private ImageButton buttonBack;
    private Button buttonViewDetails;
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
}
