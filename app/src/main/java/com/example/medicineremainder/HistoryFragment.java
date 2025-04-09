package com.example.medicineremainder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private MedicineHistoryAdapter adapter;
    private MedicineDatabase db;
    private TextView emptyView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.historyRecyclerView);
        emptyView = view.findViewById(R.id.emptyHistoryView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = MedicineDatabase.getInstance(getContext());
        loadHistory();

        return view;
    }

    private void loadHistory() {
        List<Medicine> historyItems = db.medicineDao().getMedicineHistory();

        if (historyItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter = new MedicineHistoryAdapter(historyItems);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }

    // Inner class for history adapter
    private static class MedicineHistoryAdapter extends RecyclerView.Adapter<MedicineHistoryAdapter.ViewHolder> {
        private List<Medicine> historyItems;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

        public MedicineHistoryAdapter(List<Medicine> historyItems) {
            this.historyItems = historyItems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Medicine medicine = historyItems.get(position);
            holder.medicineName.setText(medicine.getName());
            holder.medicineDetails.setText(medicine.getDosage() + " - " + medicine.getTime());

            // Format timestamp
            String takenDate = dateFormat.format(new Date(medicine.getTakenTimestamp()));
            holder.takenTimestamp.setText("Taken: " + takenDate);
        }

        @Override
        public int getItemCount() {
            return historyItems.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView medicineName, medicineDetails, takenTimestamp;

            ViewHolder(View itemView) {
                super(itemView);
                medicineName = itemView.findViewById(R.id.historyMedicineName);
                medicineDetails = itemView.findViewById(R.id.historyMedicineDetails);
                takenTimestamp = itemView.findViewById(R.id.historyTimestamp);
            }
        }
    }
}