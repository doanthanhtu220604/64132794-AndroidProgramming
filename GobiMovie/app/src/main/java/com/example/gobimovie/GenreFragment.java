package com.example.gobimovie;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class GenreFragment extends Fragment implements MovieItemClickListener {
    private Spinner spinnerGenre;
    private RecyclerView rvMoviesByGenre;
    private MovieAdapter movieAdapter;
    private List<Movie> allMovies;
    private List<Movie> filteredMovies;
    private List<String> genres;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        // Ánh xạ các thành phần giao diện
        spinnerGenre = view.findViewById(R.id.spinner_genre);
        rvMoviesByGenre = view.findViewById(R.id.rv_movies_by_genre);

        // Khởi tạo danh sách
        allMovies = new ArrayList<>();
        filteredMovies = new ArrayList<>();
        genres = new ArrayList<>();
        genres.add("Tất cả");

        // Khởi tạo RecyclerView với GridLayoutManager (3 cột)
        movieAdapter = new MovieAdapter(getContext(), filteredMovies, this);
        rvMoviesByGenre.setAdapter(movieAdapter);
        rvMoviesByGenre.setLayoutManager(new GridLayoutManager(getContext(), 3)); // Sử dụng 3 cột

        // Lấy dữ liệu từ Firebase
        loadMoviesFromFirebase();

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

                    if (title != null && link != null && des != null && thumbnail != null && genre != null) {
                        Movie movie = new Movie(title, link, des, thumbnail, genre);
                        allMovies.add(movie);
                        genreSet.add(genre);
                    }
                }

                genres.clear();
                genres.add("Tất cả");
                genres.addAll(genreSet);
                setupGenreSpinner();

                filterMovies("Tất cả");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load movies: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGenreSpinner() {
        // Tùy chỉnh Adapter cho Spinner với màu chữ trắng và background xám nhạt
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, genres) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(android.graphics.Color.WHITE); // Màu chữ trắng cho dropdown
                textView.setBackgroundColor(android.graphics.Color.GRAY); // Background xám nhạt cho dropdown
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(android.graphics.Color.WHITE); // Màu chữ trắng cho item được chọn
                textView.setBackgroundColor(android.graphics.Color.GRAY); // Background xám nhạt cho item được chọn
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(spinnerAdapter);

        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGenre = genres.get(position);
                filterMovies(selectedGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }

    private void filterMovies(String genre) {
        filteredMovies.clear();
        if (genre.equals("Tất cả")) {
            filteredMovies.addAll(allMovies);
        } else {
            for (Movie movie : allMovies) {
                if (genre.equals(movie.getFgenre())) {
                    filteredMovies.add(movie);
                }
            }
        }
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMovieClick(Movie movie, ImageView movieImageView) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("title", movie.getFtitle());
        intent.putExtra("imgURL", movie.getFthumbnail());
        intent.putExtra("description", movie.getFdes());
        intent.putExtra("videoURL", movie.getTlink());

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                movieImageView,
                "movieImageTransition"
        );

        startActivity(intent, options.toBundle());
        Toast.makeText(getContext(), "Item clicked: " + movie.getFtitle(), Toast.LENGTH_SHORT).show();
    }
}