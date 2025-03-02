package com.example.ex3_simplesumapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    public void XuLyCong(View view){
        // tim, tham chieu den dieu khien xml
        EditText editTextSoA  = findViewById(R.id.edtA);
        EditText editTextSoB  = findViewById(R.id.edtB);
        EditText editTextKetQua  = findViewById(R.id.edtKQ);

        // lay du lieu o dieu khien so A
        String strA = editTextSoA.getText().toString();
        // lay du lieu o dieu khien so B
        String strB = editTextSoB.getText().toString();
        // chuyen du lieu sang dang so
        int so_A = Integer.parseInt(strA);
        int so_B = Integer.parseInt(strB);
        // tinh toan theo yeu cau
        int tong = so_A + so_B ;
        String strTong = String.valueOf(tong); // chuyen sang dang chuoi
        // hien ra man hinh
        editTextKetQua.setText(strTong);
    }
}