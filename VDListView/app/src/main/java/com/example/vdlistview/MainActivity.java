package com.example.vdlistview;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listViewNNLT;
    ArrayList<String> dsNgonNguLT;

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

        listViewNNLT = findViewById(R.id.lvNNLT);
        dsNgonNguLT = new ArrayList<String>();
        dsNgonNguLT.add("Python");
        dsNgonNguLT.add("Java");
        dsNgonNguLT.add("C++");
        dsNgonNguLT.add("C#");

        ArrayAdapter<String> adapterNNLT = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dsNgonNguLT
        );
        listViewNNLT.setAdapter(adapterNNLT);

        listViewNNLT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedLanguage = dsNgonNguLT.get(position);
                Toast.makeText(MainActivity.this, "Bạn chọn: " + selectedLanguage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}