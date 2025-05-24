package com.example.gobimovie;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;

public class WatchListFragment extends Fragment implements MovieItemClickListener {
    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private MovieAdapter movieAdapter;
    private List<Movie> allMovies;
    private List<Movie> filteredMovies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        // Ánh xạ các thành phần giao diện
        etSearch = view.findViewById(R.id.et_search);
        rvSearchResults = view.findViewById(R.id.rv_search_results);

        // Khởi tạo danh sách
        allMovies = new ArrayList<>();
        filteredMovies = new ArrayList<>();

        // Khởi tạo RecyclerView với GridLayoutManager (3 cột) - giống GenreFragment
        movieAdapter = new MovieAdapter(getContext(), filteredMovies, this);
        rvSearchResults.setAdapter(movieAdapter);
        rvSearchResults.setLayoutManager(new GridLayoutManager(getContext(), 3)); // Sử dụng 3 cột

        // Lấy dữ liệu từ Firebase
        loadMoviesFromFirebase();

        // Thêm TextWatcher để lọc phim khi nhập từ khóa
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadMoviesFromFirebase() {
        // Logic lấy dữ liệu từ Firebase giống GenreFragment
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("featured");
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMovies.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Ftitle").getValue(String.class);
                    String link = dataSnapshot.child("Tlink").getValue(String.class);
                    String des = dataSnapshot.child("Fdes").getValue(String.class);
                    String thumbnail = dataSnapshot.child("Fthumbnail").getValue(String.class);
                    String genre = dataSnapshot.child("Fgenre").getValue(String.class);

                    // Kiểm tra null giống GenreFragment
                    if (title != null && link != null && des != null && thumbnail != null && genre != null) {
                        Movie movie = new Movie(title, link, des, thumbnail, genre);
                        allMovies.add(movie);
                    }
                }

                // Hiển thị tất cả phim ban đầu - giống GenreFragment
                filteredMovies.clear();
                filteredMovies.addAll(allMovies);
                movieAdapter.notifyDataSetChanged();

                // Nếu không có phim, hiển thị thông báo
                if (allMovies.isEmpty()) {
                    Toast.makeText(getContext(), "No movies available in watchlist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load movies: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMovies(String query) {
        // Lọc phim theo từ khóa tìm kiếm (tương tự cách GenreFragment lọc theo thể loại)
        filteredMovies.clear();
        if (query.isEmpty()) {
            filteredMovies.addAll(allMovies);
        } else {
            String searchQuery = query.toLowerCase();
            for (Movie movie : allMovies) {
                if (movie.getFtitle().toLowerCase().contains(searchQuery)) {
                    filteredMovies.add(movie);
                }
            }
        }
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMovieClick(Movie movie, ImageView movieImageView) {
        // Giữ nguyên logic khi click vào phim
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