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

public class WatchListFragment extends Fragment implements MovieItemClickListener {
    private Spinner spinnerGenre;
    private ImageView searchIcon, cancelSearch;
    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private MovieAdapter movieAdapter;
    private List<Movie> allMovies;
    private List<Movie> filteredMovies;
    private List<String> genres;
    private String currentGenre = "Tất cả";
    private String currentQuery = "";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        // Ánh xạ các thành phần giao diện
        spinnerGenre = view.findViewById(R.id.spinner_genre);
        searchIcon = view.findViewById(R.id.search_icon);
        etSearch = view.findViewById(R.id.et_search);
        cancelSearch = view.findViewById(R.id.cancel_search);
        rvSearchResults = view.findViewById(R.id.rv_search_results);

        // Khởi tạo danh sách
        allMovies = new ArrayList<>();
        filteredMovies = new ArrayList<>();
        genres = new ArrayList<>();
        genres.add("Tất cả");

        // Khởi tạo RecyclerView với GridLayoutManager (2 cột)
        movieAdapter = new MovieAdapter(getContext(), filteredMovies, this);
        rvSearchResults.setAdapter(movieAdapter);
        rvSearchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
         // Khoảng cách item

        // Lấy dữ liệu từ Firebase
        loadMoviesFromFirebase();

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
            filterMovies(); // Cập nhật danh sách theo thể loại
        });

        // TextWatcher cho tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                filterMovies();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadMoviesFromFirebase() {
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("featured");
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMovies.clear();
                Set<String> genreSet = new HashSet<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Ftitle").getValue(String.class);
                    String link = dataSnapshot.child("Tlink").getValue(String.class);
                    String des = dataSnapshot.child("Fdes").getValue(String.class);
                    String thumbnail = dataSnapshot.child("Fthumbnail").getValue(String.class);
                    String genre = dataSnapshot.child("Fgenre").getValue(String.class);
                    String poster = dataSnapshot.child("Fpos").getValue(String.class);

                    if (title != null && link != null && des != null && thumbnail != null && genre != null && poster != null) {
                        Movie movie = new Movie(title, link, des, thumbnail, genre, poster);
                        allMovies.add(movie);
                        genreSet.add(genre);
                    }
                }

                genres.clear();
                genres.add("Tất cả");
                genres.addAll(genreSet);
                setupGenreSpinner();

                filterMovies();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load movies: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                textView.setBackgroundColor(android.graphics.Color.GRAY);
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
                filterMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filterMovies() {
        filteredMovies.clear();
        for (Movie movie : allMovies) {
            boolean matchesGenre = currentGenre.equals("Tất cả") || movie.getFgenre().equals(currentGenre);
            boolean matchesQuery = currentQuery.isEmpty() || movie.getFtitle().toLowerCase().contains(currentQuery.toLowerCase());
            if (matchesGenre && matchesQuery) {
                filteredMovies.add(movie);
            }
        }
        movieAdapter.notifyDataSetChanged();

//        if (filteredMovies.isEmpty()) {
//            Toast.makeText(getContext(), "No movies found.", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onMovieClick(Movie movie, ImageView movieImageView) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("title", movie.getFtitle());
        intent.putExtra("imgURL", movie.getFpos());
        intent.putExtra("description", movie.getFdes());
        intent.putExtra("videoURL", movie.getTlink());

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(), movieImageView, "movieImageTransition");
        startActivity(intent, options.toBundle());
    }
}