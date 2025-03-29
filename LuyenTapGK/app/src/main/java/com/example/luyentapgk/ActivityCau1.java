package com.example.luyentapgk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivityCau1 extends AppCompatActivity {
    Button nutCong, nutTroVe;
    EditText editTextsoA;
    EditText editTextsoB;
    EditText editTextKetQua;
    void TimDieuKhien(){
        nutTroVe = (Button) findViewById(R.id.btnTroVe) ;
        nutCong = (Button) findViewById(R.id.btnCong);
        editTextsoA = (EditText) findViewById(R.id.edtA);
        editTextsoB = (EditText) findViewById(R.id.edtB);
        editTextKetQua = (EditText) findViewById(R.id.edtKQ);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cau1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TimDieuKhien();
        nutCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextsoA = (EditText) findViewById(R.id.edtA);
                EditText editTextsoB = (EditText) findViewById(R.id.edtB);
                String sothu1 = editTextsoA.getText().toString();
                String sothu2 = editTextsoB.getText().toString();
                float soA = Float.parseFloat(sothu1);
                float soB = Float.parseFloat(sothu2);
                float tong = soA + soB;
                EditText editTextKetQua = (EditText) findViewById(R.id.edtKQ);
                String chuoiKQ = String.valueOf(tong);
                editTextKetQua.setText(chuoiKQ);


            }
        });
        nutTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTroVeMain = new Intent(ActivityCau1.this,MainActivity.class);
                startActivity(intentTroVeMain);
            }
        });
    }
}