package com.example.luyentapgk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ActivityCau2 extends AppCompatActivity {
    ListView lvViewNNLT;
    ArrayList<String> dsNgonNguLT;
    Button nutTroVe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cau2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ ListView
        lvViewNNLT = findViewById(R.id.lvNNLT);

        // Tạo danh sách ngôn ngữ lập trình
        dsNgonNguLT = new ArrayList<>();
        dsNgonNguLT.add("Python");
        dsNgonNguLT.add("C++");
        dsNgonNguLT.add("C#");
        dsNgonNguLT.add("Java");
        dsNgonNguLT.add("JavaScript");

        // Tạo Adapter cho ListView
        ArrayAdapter<String> adapterNNLT = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, dsNgonNguLT
        );
        lvViewNNLT.setAdapter(adapterNNLT);

        // Xử lý sự kiện khi người dùng nhấn vào một item trong ListView
        lvViewNNLT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = dsNgonNguLT.get(position);

                // Chuyển sang màn hình chi tiết
                Intent intent = new Intent(ActivityCau2.this, Activity_ListView1.class);
                intent.putExtra("tenNgonNgu", selectedItem);
                startActivity(intent);
            }
        });

        // Ánh xạ nút trở về
        nutTroVe = findViewById(R.id.btnTroVe2);

        // Xử lý sự kiện khi nhấn nút trở về
        nutTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTroVe2 = new Intent(ActivityCau2.this, MainActivity.class);
                startActivity(intentTroVe2);
            }
        });
    }
}
