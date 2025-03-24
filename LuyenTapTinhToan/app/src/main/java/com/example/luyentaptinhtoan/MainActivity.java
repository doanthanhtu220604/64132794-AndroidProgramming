package com.example.luyentaptinhtoan;

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

    EditText editTextSo1;
    EditText editTextSo2;
    EditText editTextKetQua;

    Button DauCong, DauTru, DauNhan, DauChia;

    void TimDieuKien (){
        editTextSo1 = (EditText) findViewById(R.id.edtSo1);
        editTextSo2 = (EditText) findViewById(R.id.edtSo2);
        editTextKetQua = (EditText) findViewById(R.id.edtKQ);
        DauCong = (Button) findViewById(R.id.btnDauCong);
        DauTru = (Button) findViewById(R.id.btnDauTru);
        DauNhan = (Button) findViewById(R.id.btnDauNhan);
        DauChia = (Button) findViewById(R.id.btnDauChia);
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
        TimDieuKien();
        DauCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextSo1 = (EditText) findViewById(R.id.edtSo1);
                EditText editTextSo2 = (EditText) findViewById(R.id.edtSo2);

                String soThu1 = editTextSo1.getText().toString();
                String soThu2 = editTextSo2.getText().toString();

                float soA = Float.parseFloat(soThu1);
                float soB = Float.parseFloat(soThu2);

                float Tong = soA + soB;

                EditText editTextKetQua = (EditText) findViewById(R.id.edtKQ);

                String chuoiKQ = String.valueOf(Tong);
                editTextKetQua.setText(chuoiKQ);
            }
        });
        DauTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextSo1 = (EditText) findViewById(R.id.edtSo1);
                EditText editTextSo2 = (EditText) findViewById(R.id.edtSo2);

                String soThu1 = editTextSo1.getText().toString();
                String soThu2 = editTextSo2.getText().toString();

                float soA = Float.parseFloat(soThu1);
                float soB = Float.parseFloat(soThu2);

                float Hieu = soA - soB;

                EditText editTextKetQua = (EditText) findViewById(R.id.edtKQ);

                String chuoiKQ = String.valueOf(Hieu);
                editTextKetQua.setText(chuoiKQ);
            }
        });
    }
}