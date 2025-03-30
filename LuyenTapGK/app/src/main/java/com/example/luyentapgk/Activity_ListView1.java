package com.example.luyentapgk;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity_ListView1 extends AppCompatActivity {
    TextView tvChiTiet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvChiTiet = findViewById(R.id.tvChiTiet);

        // Nhận dữ liệu từ Intent
        String tenNgonNgu = getIntent().getStringExtra("tenNgonNgu");

        // Hiển thị dữ liệu lên TextView
        tvChiTiet.setText("Ngôn ngữ lập trình: " + tenNgonNgu);

    }
}