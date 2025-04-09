package com.example.medicineremainder;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class MainFragment extends Fragment implements MedicineAdapter.OnMedicineTakenListener {
    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private MedicineDatabase db;
    private List<Medicine> medicines;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = MedicineDatabase.getInstance(getContext());
        loadMedicines();

        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), AddMedActivity.class)));

        return view;
    }

    private void loadMedicines() {
        medicines = db.medicineDao().getActiveMedicines();
        adapter = new MedicineAdapter(medicines, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMedicines();
    }

    @Override
    public void onMedicineTaken(Medicine medicine, int position) {
        // Mark as taken with current timestamp
        medicine.setTaken(true);
        medicine.setTakenTimestamp(System.currentTimeMillis());

        // Update in database
        new Thread(() -> {
            db.medicineDao().updateMedicine(medicine);

            // Update UI on main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.removeItem(position);
                    Snackbar.make(recyclerView, medicine.getName() + " marked as taken", Snackbar.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}