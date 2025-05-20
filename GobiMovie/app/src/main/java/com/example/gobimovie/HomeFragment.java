package com.example.gobimovie;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements MovieItemClickListener {
    private List<Slide> lsSlides;
    private ViewPager2 sliderPager;
    private TabLayout indicator;
    private Timer timer;
    private RecyclerView MoviesRV;
    private SlidePageAdapter adapter;
    private MovieAdapter movieAdapter; // Khai báo adapter ở cấp class để cập nhật sau
    private List<Movie> lstMovies; // Khai báo danh sách ở cấp class

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference slideRef = database.getReference("trailer"); // Node cho slide (giữ nguyên)
        DatabaseReference movieRef = database.getReference("featured"); // Node cho movie

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ ViewPager2 và TabLayout từ layout
        sliderPager = view.findViewById(R.id.slider_pager);
        indicator = view.findViewById(R.id.indicator);
        MoviesRV = view.findViewById(R.id.Rv_movies);

        // Khởi tạo danh sách slide (giữ nguyên)
        lsSlides = new ArrayList<>();
        adapter = new SlidePageAdapter(lsSlides);
        sliderPager.setAdapter(adapter);

        // Lấy dữ liệu slide từ Firebase (giữ nguyên)
        slideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsSlides.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Ttitle").getValue(String.class);
                    String url = dataSnapshot.child("Turl").getValue(String.class);
                    String vid = dataSnapshot.child("Tvid").getValue(String.class);
                    if (title != null && url != null && vid != null) {
                        lsSlides.add(new Slide(title, url, vid));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load slides: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Kết nối TabLayout với ViewPager2 bằng TabLayoutMediator (giữ nguyên)
        new TabLayoutMediator(indicator, sliderPager, (tab, position) -> {
        }).attach();

        // Khởi tạo và chạy Timer (giữ nguyên)
        startSliderTimer();

        // RecyclerView setup cho Movies
        lstMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(getContext(), lstMovies, this);
        MoviesRV.setAdapter(movieAdapter);
        MoviesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Lấy dữ liệu movie từ Firebase
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstMovies.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Ánh xạ dữ liệu từ Firebase vào object Movie
                    String title = dataSnapshot.child("Ftitle").getValue(String.class);
                    String link = dataSnapshot.child("Tlink").getValue(String.class);
                    String des = dataSnapshot.child("Fdes").getValue(String.class);
                    String thumbnail = dataSnapshot.child("Fthumbnail").getValue(String.class);
                    String genre = dataSnapshot.child("Fgenre").getValue(String.class); // Thêm dòng này

                    if (title != null && link != null && des != null && thumbnail != null && genre != null) {
                        lstMovies.add(new Movie(title, link, des, thumbnail, genre)); // Truyền đủ 5 tham số
                    }
                }
                movieAdapter.notifyDataSetChanged(); // Cập nhật adapter sau khi dữ liệu thay đổi
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load movies: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void startSliderTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
    }

    @Override
    public void onMovieClick(Movie movie, ImageView movieImageView) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("title", movie.getFtitle());
        intent.putExtra("imgURL", movie.getFthumbnail());
        intent.putExtra("description", movie.getFdes()); // Thêm Fdes
        intent.putExtra("videoURL", movie.getTlink()); // Thêm Tlink

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                movieImageView,
                "movieImageTransition"
        );

        startActivity(intent, options.toBundle());
        Toast.makeText(getContext(), "Item clicked: " + movie.getFtitle(), Toast.LENGTH_SHORT).show();
    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            if (isAdded() && !isDetached() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (sliderPager != null) {
                        int currentItem = sliderPager.getCurrentItem();
                        int nextItem = (currentItem < lsSlides.size() - 1) ? currentItem + 1 : 0;
                        sliderPager.setCurrentItem(nextItem);
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        sliderPager = null;
    }
}