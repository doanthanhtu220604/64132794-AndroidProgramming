package com.example.gobimovie;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeriesFragment extends Fragment implements SeriesAdapter.SeriesItemClickListener {
    private Spinner spinnerGenre;
    private ImageView searchIcon, cancelSearch;
    private EditText etSearch;
    private RecyclerView rvSeriesByGenre;
    private SeriesAdapter seriesAdapter;
    private List<Series> allSeries;
    private List<Series> filteredSeries;
    private List<String> genres;
    private String currentGenre = "Tất cả";
    private String currentQuery = "";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        // Ánh xạ các thành phần giao diện
        spinnerGenre = view.findViewById(R.id.spinner_genre);
        searchIcon = view.findViewById(R.id.search_icon);
        etSearch = view.findViewById(R.id.et_search);
        cancelSearch = view.findViewById(R.id.cancel_search);
        rvSeriesByGenre = view.findViewById(R.id.rv_movies_by_genre);

        // Khởi tạo danh sách
        allSeries = new ArrayList<>();
        filteredSeries = new ArrayList<>();
        genres = new ArrayList<>();
        genres.add("Tất cả");

        // Khởi tạo RecyclerView với GridLayoutManager (2 cột)
        seriesAdapter = new SeriesAdapter(getContext(), filteredSeries, this);
        rvSeriesByGenre.setAdapter(seriesAdapter);
        rvSeriesByGenre.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvSeriesByGenre.setHasFixedSize(true); // Tối ưu hiệu suất

        // Lấy dữ liệu từ Firebase
        loadSeriesFromFirebase();

        // Sự kiện nhấn icon tìm kiếm
        searchIcon.setOnClickListener(v -> {
            searchIcon.setVisibility(View.GONE);
            etSearch.setVisibility(View.VISIBLE);
            cancelSearch.setVisibility(View.VISIBLE);
            etSearch.requestFocus(); // Mở bàn phím
        });

        // Sự kiện nhấn nút hủy
        cancelSearch.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.setVisibility(View.GONE);
            cancelSearch.setVisibility(View.GONE);
            searchIcon.setVisibility(View.VISIBLE);
            currentQuery = "";
            filterSeries(); // Cập nhật danh sách theo thể loại
        });

        // TextWatcher cho tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                filterSeries();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadSeriesFromFirebase() {
        DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
        seriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allSeries.clear();
                Set<String> genreSet = new HashSet<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Stitle").getValue(String.class);
                    String link = dataSnapshot.child("Slink").getValue(String.class);
                    String des = dataSnapshot.child("Sdes").getValue(String.class);
                    String thumbnail = dataSnapshot.child("Sthumbnail").getValue(String.class);
                    String genre = dataSnapshot.child("Sgenre").getValue(String.class);
                    String poster = dataSnapshot.child("Spos").getValue(String.class);

                    if (title != null && link != null && des != null && thumbnail != null && genre != null && poster != null) {
                        Series series = new Series(title, poster, genre, link, thumbnail, des);
                        allSeries.add(series);
                        genreSet.add(genre);
                    }
                }

                genres.clear();
                genres.add("Tất cả");
                genres.addAll(genreSet);
                setupGenreSpinner();

                filterSeries();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Không thể tải series: " + error.getMessage());
            }
        });
    }

    private void setupGenreSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, genres) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(android.graphics.Color.WHITE);
                textView.setBackgroundColor(android.graphics.Color.DKGRAY);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(android.graphics.Color.WHITE);
                textView.setBackgroundColor(android.graphics.Color.GRAY);
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(spinnerAdapter);

        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGenre = genres.get(position);
                filterSeries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filterSeries() {
        filteredSeries.clear();
        for (Series series : allSeries) {
            boolean matchesGenre = currentGenre.equals("Tất cả") || series.getSgenre().equals(currentGenre);
            boolean matchesQuery = currentQuery.isEmpty() || series.getStitle().toLowerCase().contains(currentQuery.toLowerCase());
            if (matchesGenre && matchesQuery) {
                filteredSeries.add(series);
            }
        }
        seriesAdapter.notifyDataSetChanged();

        if (filteredSeries.isEmpty()) {
            showToast("Không tìm thấy series nào.");
        }
    }

    @Override
    public void onSeriesClick(Series series, ImageView seriesImageView) {
        if (getContext() != null && getActivity() != null) {
            Intent intent = new Intent(getContext(), SeriesDetailActivity.class);
            intent.putExtra("title", series.getStitle());
            intent.putExtra("imgURL", series.getSpos());
            intent.putExtra("description", series.getSdes());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(), seriesImageView, "movieImageTransition");
            startActivity(intent, options.toBundle());
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        spinnerGenre = null;
        searchIcon = null;
        etSearch = null;
        cancelSearch = null;
        rvSeriesByGenre = null;
        seriesAdapter = null;
    }
}