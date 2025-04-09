package com.example.medicineremainder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class bmi extends Fragment {

    private TextInputEditText weightInput, heightInput;
    private TextView resultValueText, resultCategoryText;
    private MaterialButton calculateButton, kgButton, lbsButton, cmButton, ftinButton;
    private ProgressBar bmiIndicator;
    private boolean isMetric = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bmi, container, false);

        // Find all views
        weightInput = view.findViewById(R.id.weightInput);
        heightInput = view.findViewById(R.id.heightInput);
        resultValueText = view.findViewById(R.id.resultValueText);
        resultCategoryText = view.findViewById(R.id.resultCategoryText);
        calculateButton = view.findViewById(R.id.calculateButton);
        bmiIndicator = view.findViewById(R.id.bmiIndicator);

        // Unit toggle buttons
        kgButton = view.findViewById(R.id.kgButton);
        lbsButton = view.findViewById(R.id.lbsButton);
        cmButton = view.findViewById(R.id.cmButton);
        ftinButton = view.findViewById(R.id.ftinButton);

        // Set listeners
        calculateButton.setOnClickListener(v -> calculateBMI());

        // Set up unit toggle buttons
        kgButton.setOnClickListener(v -> toggleWeightUnit(true));
        lbsButton.setOnClickListener(v -> toggleWeightUnit(false));
        cmButton.setOnClickListener(v -> toggleHeightUnit(true));
        ftinButton.setOnClickListener(v -> toggleHeightUnit(false));

        return view;
    }

    private void toggleWeightUnit(boolean isKg) {
        kgButton.setChecked(isKg);
        lbsButton.setChecked(!isKg);

        // Update hint text based on selected unit
        if (isKg) {
            weightInput.setHint("Weight (kg)");
        } else {
            weightInput.setHint("Weight (lbs)");
        }

        isMetric = isKg && cmButton.isChecked();
    }

    private void toggleHeightUnit(boolean isCm) {
        cmButton.setChecked(isCm);
        ftinButton.setChecked(!isCm);

        // Update hint text based on selected unit
        if (isCm) {
            heightInput.setHint("Height (cm)");
        } else {
            heightInput.setHint("Height (ft.in)");
        }

        isMetric = isCm && kgButton.isChecked();
    }

    private void calculateBMI() {
        String weightStr = weightInput.getText().toString();
        String heightStr = heightInput.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            resultValueText.setText("--");
            resultCategoryText.setVisibility(View.GONE);
            bmiIndicator.setProgress(0);
            return;
        }

        float weight = Float.parseFloat(weightStr);
        float height = Float.parseFloat(heightStr);
        float bmi;

        // Calculate BMI based on unit system
        if (kgButton.isChecked() && cmButton.isChecked()) {
            // Metric - kg and cm
            float heightM = height / 100; // Convert height to meters
            bmi = weight / (heightM * heightM);
        } else if (kgButton.isChecked() && ftinButton.isChecked()) {
            // Mixed - kg and ft.in (assuming input in decimal, e.g., 5.75 for 5'9")
            float heightInches = (int)height * 12 + ((height - (int)height) * 100);
            float heightM = heightInches * 0.0254f; // Convert to meters
            bmi = weight / (heightM * heightM);
        } else if (lbsButton.isChecked() && cmButton.isChecked()) {
            // Mixed - lbs and cm
            float heightM = height / 100; // Convert height to meters
            float weightKg = weight * 0.453592f; // Convert to kg
            bmi = weightKg / (heightM * heightM);
        } else {
            // Imperial - lbs and ft.in
            float heightInches = (int)height * 12 + ((height - (int)height) * 100);
            bmi = (weight * 703) / (heightInches * heightInches);
        }

        String category = getBMICategory(bmi);

        // Update UI with results
        resultValueText.setText(String.format("%.1f", bmi));
        resultCategoryText.setText(category);
        resultCategoryText.setVisibility(View.VISIBLE);

        // Update progress indicator
        int progress = Math.min(40, Math.round(bmi));
        bmiIndicator.setProgress(progress);

        // Set text color based on BMI category
        if (bmi < 18.5) {
            resultCategoryText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else if (bmi < 25) {
            resultCategoryText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (bmi < 30) {
            resultCategoryText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            resultCategoryText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private String getBMICategory(float bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25.0) return "Normal weight";
        else if (bmi < 30.0) return "Overweight";
        else return "Obese";
    }
}