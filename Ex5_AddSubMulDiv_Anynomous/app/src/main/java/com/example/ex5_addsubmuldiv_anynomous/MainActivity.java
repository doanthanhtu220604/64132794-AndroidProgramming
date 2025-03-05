package com.example.ex5_addsubmuldiv_anynomous;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // khai bao cac dieu khien
    EditText editTextSo1;
    EditText editTextSo2;
    EditText editTextKQ;
    Button nutCong, nutTru, nutNhan, nutChia;

    void TimDieuKhien() {
        editTextSo1 = (EditText) findViewById(R.id.edtSo1);
        editTextSo2 = (EditText) findViewById(R.id.edtSo2);
        editTextKQ = (EditText) findViewById(R.id.edtKetQua);
        nutCong = (Button) findViewById(R.id.btnCong);
        nutTru = (Button) findViewById(R.id.btnTru);
        nutNhan = (Button) findViewById(R.id.btnNhan);
        nutChia = (Button) findViewById(R.id.btnChia);

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
        TimDieuKhien();
        // gan bo lang nghe su kien va code xu li cho tung nut
        View.OnClickListener boLangNgheCong = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xu ly cong o day
                //b1 lay du lieu 2 so
                // b1.1 tim edittext so 1 va so 2
                EditText editTextSo1 = (EditText) findViewById(R.id.edtSo1);
                EditText editTextSo2 = (EditText) findViewById(R.id.edtSo2);
                // lay du lieu tu hai dieu khien do
                String sothu1 = editTextSo1.getText().toString();
                String sothu2 = editTextSo2.getText().toString();
                // chuyen tu chuoi sang so
                float soA = Float.parseFloat(sothu1);
                float soB = Float.parseFloat(sothu2);
                // tinh toan
                float Tong = soA + soB;

                EditText editTextKQ = (EditText) findViewById(R.id.edtKetQua);

                String chuoiKQ = String.valueOf(Tong);

                editTextKQ.setText(chuoiKQ);

            }
        };
        nutCong.setOnClickListener(boLangNgheCong);
        nutTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xu li o day
                // xu ly cong o day
                //b1 lay du lieu 2 so
                // b1.1 tim edittext so 1 va so 2
                EditText editTextSo1 = (EditText) findViewById(R.id.edtSo1);
                EditText editTextSo2 = (EditText) findViewById(R.id.edtSo2);
                // lay du lieu tu hai dieu khien do
                String sothu1 = editTextSo1.getText().toString();
                String sothu2 = editTextSo2.getText().toString();
                // chuyen tu chuoi sang so
                float soA = Float.parseFloat(sothu1);
                float soB = Float.parseFloat(sothu2);
                // tinh toan
                float Tong = soA - soB;

                EditText editTextKQ = (EditText) findViewById(R.id.edtKetQua);

                String chuoiKQ = String.valueOf(Tong);

                editTextKQ.setText(chuoiKQ);
            }
        });
        nutNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xu li o day
                // xu ly cong o day
                //b1 lay du lieu 2 so
                // b1.1 tim edittext so 1 va so 2
                EditText editTextSo1 = (EditText) findViewById(R.id.edtSo1);
                EditText editTextSo2 = (EditText) findViewById(R.id.edtSo2);
                // lay du lieu tu hai dieu khien do
                String sothu1 = editTextSo1.getText().toString();
                String sothu2 = editTextSo2.getText().toString();
                // chuyen tu chuoi sang so
                float soA = Float.parseFloat(sothu1);
                float soB = Float.parseFloat(sothu2);
                // tinh toan
                float Tong = soA * soB;

                EditText editTextKQ = (EditText) findViewById(R.id.edtKetQua);

                String chuoiKQ = String.valueOf(Tong);

                editTextKQ.setText(chuoiKQ);
            }
        });
        nutChia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xu ly cong o day
                //b1 lay du lieu 2 so
                // b1.1 tim edittext so 1 va so 2
                EditText editTextSo1 = (EditText) findViewById(R.id.edtSo1);
                EditText editTextSo2 = (EditText) findViewById(R.id.edtSo2);
                // lay du lieu tu hai dieu khien do
                String sothu1 = editTextSo1.getText().toString();
                String sothu2 = editTextSo2.getText().toString();
                // chuyen tu chuoi sang so
                float soA = Float.parseFloat(sothu1);
                float soB = Float.parseFloat(sothu2);
                // tinh toan
                float Tong = soA / soB;

                EditText editTextKQ = (EditText) findViewById(R.id.edtKetQua);

                String chuoiKQ = String.valueOf(Tong);

                editTextKQ.setText(chuoiKQ);

            }
        });
    }

}