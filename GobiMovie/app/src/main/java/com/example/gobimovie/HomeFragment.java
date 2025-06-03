package com.example.gobimovie;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

public class HomeFragment extends Fragment implements MovieItemClickListener, SeriesAdapter.SeriesItemClickListener {
    private List<Slide> lsSlides;
    private ViewPager2 sliderPager;
    private TabLayout indicator;
    private Timer timer;
    private boolean isTimerRunning = false;
    private RecyclerView moviesRV;
    private RecyclerView seriesRV;
    private SlidePageAdapter slideAdapter;
    private MovieAdapter movieAdapter;
    private SeriesAdapter seriesAdapter;
    private List<Movie> lstMovies;
    private List<Series> lstSeries;
    private DatabaseReference slideRef;
    private DatabaseReference movieRef;
    private DatabaseReference seriesRef;
    private ValueEventListener slideListener;
    private ValueEventListener movieListener;
    private ValueEventListener seriesListener;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo Firebase references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        slideRef = database.getReference("trailer");
        movieRef = database.getReference("featured");
        seriesRef = database.getReference("series");

        // Ánh xạ các view
        sliderPager = view.findViewById(R.id.slider_pager);
        indicator = view.findViewById(R.id.indicator);
        moviesRV = view.findViewById(R.id.Rv_movies);
        seriesRV = view.findViewById(R.id.Rv_series);
        TextView tvSeeAllMovies = view.findViewById(R.id.tv_see_all); // Nút "Xem tất cả" cho phim lẻ
        TextView tvSeeAllSeries = view.findViewById(R.id.tv_see_all_series); // Nút "Xem tất cả" cho phim bộ (ID đã sửa)

        // Thiết lập ViewPager2 cho slides
        lsSlides = new ArrayList<>();
        slideAdapter = new SlidePageAdapter(lsSlides);
        sliderPager.setAdapter(slideAdapter);
        new TabLayoutMediator(indicator, sliderPager, (tab, position) -> {
        }).attach();

        // Lấy dữ liệu slides từ Firebase
        slideListener = new ValueEventListener() {
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
                slideAdapter.notifyDataSetChanged();
                if (lsSlides.isEmpty()) {
                    showToast("Không có slide nào để hiển thị.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Không thể tải slide: " + error.getMessage());
                Log.e("HomeFragment", "Không thể tải slide: " + error.getMessage());
            }
        };
        slideRef.addValueEventListener(slideListener);

        // Thiết lập RecyclerView cho phim lẻ
        lstMovies = new ArrayList<>();
        movieListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstMovies.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Ftitle").getValue(String.class);
                    String link = dataSnapshot.child("Tlink").getValue(String.class);
                    String des = dataSnapshot.child("Fdes").getValue(String.class);
                    String thumbnail = dataSnapshot.child("Fthumbnail").getValue(String.class);
                    String genre = dataSnapshot.child("Fgenre").getValue(String.class);
                    String poster = dataSnapshot.child("Fpos").getValue(String.class);

                    if (title != null && link != null && des != null && thumbnail != null && genre != null && poster != null) {
                        lstMovies.add(new Movie(title, link, des, thumbnail, genre, poster));
                    }
                }

                List<Movie> limitedMovies = lstMovies.size() > 10 ? lstMovies.subList(0, 10) : lstMovies;
                if (limitedMovies.isEmpty()) {
                    showToast("Không có phim lẻ nào để hiển thị.");
                }

                movieAdapter = new MovieAdapter(mContext, limitedMovies, HomeFragment.this);
                moviesRV.setAdapter(movieAdapter);
                moviesRV.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                moviesRV.setHasFixedSize(true);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Không thể tải phim lẻ: " + error.getMessage());
                Log.e("HomeFragment", "Không thể tải phim lẻ: " + error.getMessage());
            }
        };
        movieRef.addValueEventListener(movieListener);

        // Thiết lập RecyclerView cho series
        lstSeries = new ArrayList<>();
        seriesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstSeries.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("Stitle").getValue(String.class);
                    String link = dataSnapshot.child("Slink").getValue(String.class);
                    String des = dataSnapshot.child("Sdes").getValue(String.class);
                    String thumbnail = dataSnapshot.child("Sthumbnail").getValue(String.class);
                    String genre = dataSnapshot.child("Sgenre").getValue(String.class);
                    String poster = dataSnapshot.child("Spos").getValue(String.class);

                    if (title != null && link != null && des != null && thumbnail != null && genre != null && poster != null) {
                        lstSeries.add(new Series(title, poster, genre, link, thumbnail, des));
                    }
                }

                List<Series> limitedSeries = lstSeries.size() > 10 ? lstSeries.subList(0, 10) : lstSeries;
                if (limitedSeries.isEmpty()) {
                    showToast("Không có phim bộ nào để hiển thị.");
                }

                seriesAdapter = new SeriesAdapter(mContext, limitedSeries, HomeFragment.this);
                seriesRV.setAdapter(seriesAdapter);
                seriesRV.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                seriesRV.setHasFixedSize(true);
                seriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Không thể tải phim bộ: " + error.getMessage());
                Log.e("HomeFragment", "Không thể tải phim bộ: " + error.getMessage());
            }
        };
        seriesRef.addValueEventListener(seriesListener);

        // Xử lý sự kiện nhấn nút "Xem tất cả" cho Phim lẻ
        if (tvSeeAllMovies != null) {
            tvSeeAllMovies.setOnClickListener(v -> {
                if (getActivity() != null && mContext != null) {
                    MoviesFragment watchListFragment = new MoviesFragment();
                    ViewGroup parentContainer = (ViewGroup) getView().getParent();
                    int containerId = parentContainer != null ? parentContainer.getId() : -1;

                    if (containerId != -1) {
                        try {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(containerId, watchListFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } catch (IllegalArgumentException e) {
                            Log.e("HomeFragment", "Lỗi khi chuyển fragment: " + e.getMessage());
                            Toast.makeText(mContext, "Lỗi khi chuyển đến WatchListFragment.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("HomeFragment", "Không tìm thấy container cha cho HomeFragment");
                        Toast.makeText(mContext, "Lỗi khi chuyển đến WatchListFragment.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.e("HomeFragment", "Không tìm thấy tv_see_all trong layout");
        }

        // Xử lý sự kiện nhấn nút "Xem tất cả" cho Phim bộ
        if (tvSeeAllSeries != null) {
            tvSeeAllSeries.setOnClickListener(v -> {
                if (getActivity() != null && mContext != null) {
                    SeriesFragment genreFragment = new SeriesFragment();
                    ViewGroup parentContainer = (ViewGroup) getView().getParent();
                    int containerId = parentContainer != null ? parentContainer.getId() : -1;

                    if (containerId != -1) {
                        try {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(containerId, genreFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } catch (IllegalArgumentException e) {
                            Log.e("HomeFragment", "Lỗi khi chuyển fragment: " + e.getMessage());
                            Toast.makeText(mContext, "Lỗi khi chuyển đến GenreFragment.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("HomeFragment", "Không tìm thấy container cha cho HomeFragment");
                        Toast.makeText(mContext, "Lỗi khi chuyển đến GenreFragment.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.e("HomeFragment", "Không tìm thấy tv_see_all_series trong layout");
        }

        return view;
    }

    private void startSliderTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
        isTimerRunning = true;
    }

    @Override
    public void onMovieClick(Movie movie, ImageView movieImageView) {
        if (mContext != null && getActivity() != null) {
            Intent intent = new Intent(mContext, MovieDetailActivity.class);
            intent.putExtra("title", movie.getFtitle());
            intent.putExtra("imgURL", movie.getFpos());
            intent.putExtra("description", movie.getFdes());
            intent.putExtra("videoURL", movie.getTlink());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(), movieImageView, "movieImageTransition");
            startActivity(intent, options.toBundle());
        }
    }

    @Override
    public void onSeriesClick(Series series, ImageView seriesImageView) {
        if (mContext != null && getActivity() != null) {
            Intent intent = new Intent(mContext, SeriesDetailActivity.class);
            intent.putExtra("title", series.getStitle());
            intent.putExtra("imgURL", series.getSpos());
            intent.putExtra("description", series.getSdes());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(), seriesImageView, "movieImageTransition");
            startActivity(intent, options.toBundle());
        }
    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            if (isAdded() && !isDetached() && getActivity() != null && sliderPager != null && isTimerRunning) {
                getActivity().runOnUiThread(() -> {
                    int currentItem = sliderPager.getCurrentItem();
                    int nextItem = (currentItem < lsSlides.size() - 1) ? currentItem + 1 : 0;
                    sliderPager.setCurrentItem(nextItem);
                });
            }
        }
    }

    private void showToast(String message) {
        if (mContext != null) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            isTimerRunning = false;
            timer.cancel();
            timer = null;
        }
        if (slideRef != null && slideListener != null) {
            slideRef.removeEventListener(slideListener);
        }
        if (movieRef != null && movieListener != null) {
            movieRef.removeEventListener(movieListener);
        }
        if (seriesRef != null && seriesListener != null) {
            seriesRef.removeEventListener(seriesListener);
        }
        sliderPager = null;
        moviesRV = null;
        seriesRV = null;
    }
}