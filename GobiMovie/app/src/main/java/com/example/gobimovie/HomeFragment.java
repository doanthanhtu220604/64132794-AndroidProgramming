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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements MovieItemClickListener {
    private List<Slide> lsSlides;
    private ViewPager2 sliderPager;
    private TabLayout indicator;
    private Timer timer; // Biến để quản lý Timer
    private RecyclerView MoviesRV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ ViewPager2 và TabLayout từ layout
        sliderPager = view.findViewById(R.id.slider_pager);
        indicator = view.findViewById(R.id.indicator);
        MoviesRV = view.findViewById(R.id.Rv_movies);

        // Khởi tạo danh sách slide
        lsSlides = new ArrayList<>();
        lsSlides.add(new Slide(R.drawable.slide1, "Totoro"));
        lsSlides.add(new Slide(R.drawable.slide2, "One Piece"));

        // Gắn adapter cho ViewPager2
        SlidePageAdapter adapter = new SlidePageAdapter(lsSlides);
        sliderPager.setAdapter(adapter);
        // Kết nối TabLayout với ViewPager2 bằng TabLayoutMediator
        new TabLayoutMediator(indicator, sliderPager, (tab, position) -> {
            // Tiêu đề tab theo từng slide
        }).attach();

        // Khởi tạo và chạy Timer
        startSliderTimer();
        // RecyclerView setup
        // int data
        List<Movie> lstMovies = new ArrayList<>();

        lstMovies.add(new Movie("Cuộc Hôn Nhân Hoàn Hảo", R.drawable.cuochonnhanhoanhoa,R.drawable.cuochonnhanhoanhaocover));
        lstMovies.add(new Movie("Hạnh Phúc Bị Đánh Cắp", R.drawable.hanhphucbidanhcap,R.drawable.cuochonnhanhoanhaocover));
        lstMovies.add(new Movie("Ngộ Nhập Quân Mộng", R.drawable.ngonhapquanmong,R.drawable.cuochonnhanhoanhaocover));
        lstMovies.add(new Movie("Đại Tống Thiếu Niên Chí 2", R.drawable.daitongthieunhienchi2,R.drawable.cuochonnhanhoanhaocover));
        lstMovies.add(new Movie("Ngộ Nhập Quân Mộng", R.drawable.ngonhapquanmong,R.drawable.cuochonnhanhoanhaocover));
        lstMovies.add(new Movie("Cuộc Hôn Nhân Hoàn Hảo", R.drawable.cuochonnhanhoanhoa,R.drawable.cuochonnhanhoanhaocover));

        MovieAdapter movieAdapter = new MovieAdapter(getContext(), lstMovies, this); // Sử dụng getContext()
        MoviesRV.setAdapter(movieAdapter);
        MoviesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)); // Sử dụng getContext()





        return view;
    }

    private void startSliderTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
    }

    @Override
    public void onMovieClick(Movie movie, ImageView movieImageView) {
        // Tạo Intent để chuyển sang MovieDetailActivity
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("imgURL", movie.getThumbnail());
        intent.putExtra("imgCover",movie.getCoverPhoto());


        // Tạo hiệu ứng chuyển đổi
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(), // Sử dụng getActivity() thay vì HomeFragment.this
                movieImageView, // View được chia sẻ (ImageView của phim)
                "movieImageTransition" // Tên chuyển đổi, phải khớp với MovieDetailActivity
        );

        // Bắt đầu Activity với hiệu ứng chuyển đổi
        startActivity(intent, options.toBundle());

        // Hiển thị Toast
        Toast.makeText(getContext(), "Item clicked: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            // Kiểm tra xem Fragment có còn gắn với Activity không
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
        // Hủy Timer khi Fragment View bị hủy
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        sliderPager = null; // Tránh giữ tham chiếu không cần thiết
    }
}