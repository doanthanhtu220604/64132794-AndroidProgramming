package com.example.ex6_intentdongian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button nutMH2;
    Button nutMH3;
    void TimDieuKhien (){
        Button nutMH2 = (Button) findViewById(R.id.btnMH2);
        Button nutMH3 = (Button) findViewById(R.id.btnMH3);
    }

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
        // tim dieu khien nut bam
        TimDieuKhien();
        // gan bo lang nghe xu kien
        nutMH2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xu ly chuyen man hinh
                // b1: tao intent, 2 tham so: man hinh hien tai va man hinh chuyen toi
                Intent intentMH2 = new Intent(MainActivity.this, MH2Activity.class);
                // b2: gui
                startActivity(intentMH2);
            }
        });
        nutMH3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMH3 = new Intent(MainActivity.this, MH3Activity.class);
                // b2: gui
                startActivity(intentMH3);
            }
        });

    }
}