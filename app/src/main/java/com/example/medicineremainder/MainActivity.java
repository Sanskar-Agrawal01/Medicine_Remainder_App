package com.example.medicineremainder;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private MedicineDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = MedicineDatabase.getInstance(this);
        List<Medicine> medicines = db.medicineDao().getAllMedicines();
        adapter = new MedicineAdapter(medicines);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddMedActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Medicine> medicines = db.medicineDao().getAllMedicines();
        adapter = new MedicineAdapter(medicines);
        recyclerView.setAdapter(adapter);
    }
}