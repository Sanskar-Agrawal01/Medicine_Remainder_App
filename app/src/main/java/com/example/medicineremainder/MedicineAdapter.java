package com.example.medicineremainder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {
    private List<Medicine> medicines;
    private OnMedicineTakenListener listener;

    public interface OnMedicineTakenListener {
        void onMedicineTaken(Medicine medicine, int position);
    }

    public MedicineAdapter(List<Medicine> medicines, OnMedicineTakenListener listener) {
        this.medicines = medicines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.medicineName.setText(medicine.getName());
        holder.medicineDetails.setText(medicine.getDosage() + " - " + medicine.getTime());
        holder.medicineTime.setText("Next Dose: " + medicine.getTime());

        holder.takenButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMedicineTaken(medicine, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public void removeItem(int position) {
        medicines.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, medicineDetails, medicineTime;
        Button takenButton;

        ViewHolder(View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicineName);
            medicineDetails = itemView.findViewById(R.id.medicineDetails);
            medicineTime = itemView.findViewById(R.id.medicineTime);
            takenButton = itemView.findViewById(R.id.btnTaken);
        }
    }
}