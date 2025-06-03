package com.example.gobimovie;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ nút "Phim đã xem"
        Button watchedMoviesButton = view.findViewById(R.id.button_watched);
        if (watchedMoviesButton != null) {
            watchedMoviesButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), WatchedMoviesActivity.class);
                startActivity(intent);
            });
        } else {
            Toast.makeText(getContext(), "Không tìm thấy nút Phim đã xem", Toast.LENGTH_SHORT).show();
        }

        // Ánh xạ nút "Phim yêu thích"
        Button favoritesButton = view.findViewById(R.id.button_favorites);
        if (favoritesButton != null) {
            favoritesButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FavoritesMoviesActivity.class);
                startActivity(intent);
            });
        } else {
            Toast.makeText(getContext(), "Không tìm thấy nút Phim yêu thích", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}