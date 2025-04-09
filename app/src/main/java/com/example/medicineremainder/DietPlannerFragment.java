package com.example.medicineremainder;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DietPlannerFragment extends Fragment {

    private EditText etAge, etHeight, etWeight;
    private AutoCompleteTextView dropdownGender, dropdownActivityLevel, dropdownDietaryRestrictions;
    private MaterialButton btnGeneratePlan;
    private TextView tvDietPlan;
    private ProgressBar progressBar;
    private CardView resultsCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_planner, container, false);

        // Initialize UI components
        etAge = view.findViewById(R.id.et_age);
        etHeight = view.findViewById(R.id.et_height);
        etWeight = view.findViewById(R.id.et_weight);
        dropdownGender = view.findViewById(R.id.dropdown_gender);
        dropdownActivityLevel = view.findViewById(R.id.dropdown_activity_level);
        dropdownDietaryRestrictions = view.findViewById(R.id.dropdown_dietary_restrictions);
        btnGeneratePlan = view.findViewById(R.id.btn_generate_plan);
        tvDietPlan = view.findViewById(R.id.tv_diet_plan);
        progressBar = view.findViewById(R.id.progress_bar);
        resultsCard = view.findViewById(R.id.results_card);

        // Initialize dropdown adapters
        setupDropdowns();

        // Set click listener for generate button
        btnGeneratePlan.setOnClickListener(v -> generateDietPlan());

        return view;
    }

    private void setupDropdowns() {
        // Gender dropdown setup
        String[] genderOptions = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                getContext(), R.layout.dropdown_item, genderOptions);
        dropdownGender.setAdapter(genderAdapter);

        // Activity level dropdown setup
        String[] activityOptions = new String[]{
                "Sedentary (little or no exercise)",
                "Lightly active (light exercise 1-3 days/week)",
                "Moderately active (moderate exercise 3-5 days/week)",
                "Very active (hard exercise 6-7 days/week)",
                "Extra active (very hard exercise & physical job)"
        };
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                getContext(), R.layout.dropdown_item, activityOptions);
        dropdownActivityLevel.setAdapter(activityAdapter);

        // Dietary restrictions dropdown setup
        String[] dietaryOptions = new String[]{
                "None",
                "Vegetarian",
                "Vegan",
                "Gluten-free",
                "Dairy-free",
                "Keto",
                "Paleo",
                "Low-carb",
                "Mediterranean"
        };
        ArrayAdapter<String> dietaryAdapter = new ArrayAdapter<>(
                getContext(), R.layout.dropdown_item, dietaryOptions);
        dropdownDietaryRestrictions.setAdapter(dietaryAdapter);
    }

    private void generateDietPlan() {
        if (!validateInputs()) return;

        // Show progress bar and hide results
        progressBar.setVisibility(View.VISIBLE);
        tvDietPlan.setVisibility(View.GONE);
        resultsCard.setVisibility(View.VISIBLE);

        // Parse input values
        int age = Integer.parseInt(etAge.getText().toString());
        double height = Double.parseDouble(etHeight.getText().toString());
        double weight = Double.parseDouble(etWeight.getText().toString());
        String gender = dropdownGender.getText().toString();
        String activityLevel = dropdownActivityLevel.getText().toString();
        String dietaryRestrictions = dropdownDietaryRestrictions.getText().toString();

        // Extract activity level key from dropdown text
        String activityKey = "sedentary";
        if (activityLevel.contains("Lightly active")) {
            activityKey = "lightly active";
        } else if (activityLevel.contains("Moderately active")) {
            activityKey = "moderately active";
        } else if (activityLevel.contains("Very active")) {
            activityKey = "very active";
        } else if (activityLevel.contains("Extra active")) {
            activityKey = "extra active";
        }

        // Calculate BMI and daily calories
        double bmi = calculateBMI(weight, height);
        int dailyCalories = calculateDailyCalories(age, gender, weight, height, activityKey);

        // Create and display the diet plan
        String plan = createPersonalizedDietPlan(bmi, dailyCalories, dietaryRestrictions);
        progressBar.setVisibility(View.GONE);
        tvDietPlan.setVisibility(View.VISIBLE);
        tvDietPlan.setText(plan);
    }

    private boolean validateInputs() {
        if (etAge.getText().toString().isEmpty() ||
                etHeight.getText().toString().isEmpty() ||
                etWeight.getText().toString().isEmpty() ||
                dropdownGender.getText().toString().isEmpty() ||
                dropdownActivityLevel.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private double calculateBMI(double weight, double height) {
        double heightInMeters = height / 100;
        return weight / (heightInMeters * heightInMeters);
    }

    private int calculateDailyCalories(int age, String gender, double weight, double height, String activityLevel) {
        double bmr;
        if (gender.equalsIgnoreCase("male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        double activityMultiplier;
        switch (activityLevel.toLowerCase()) {
            case "lightly active":
                activityMultiplier = 1.375;
                break;
            case "moderately active":
                activityMultiplier = 1.55;
                break;
            case "very active":
                activityMultiplier = 1.725;
                break;
            case "extra active":
                activityMultiplier = 1.9;
                break;
            default:
                activityMultiplier = 1.2; // Sedentary
                break;
        }

        return (int) (bmr * activityMultiplier);
    }

    private String createPersonalizedDietPlan(double bmi, int dailyCalories, String dietaryRestrictions) {
        String dietGoal = bmi < 18.5 ? "Weight Gain" : (bmi >= 25 ? "Weight Loss" : "Maintenance");

        // Adjust calories based on goal
        if (dietGoal.equals("Weight Loss")) {
            dailyCalories -= 500;
        } else if (dietGoal.equals("Weight Gain")) {
            dailyCalories += 500;
        }

        // Calculate meal calories
        int breakfastCalories = (int) (dailyCalories * 0.3);
        int lunchCalories = (int) (dailyCalories * 0.4);
        int dinnerCalories = (int) (dailyCalories * 0.3);

        // Get meal options based on dietary restrictions
        List<String> breakfastOptions = getBreakfastOptions(dietaryRestrictions);
        List<String> lunchOptions = getLunchOptions(dietaryRestrictions);
        List<String> dinnerOptions = getDinnerOptions(dietaryRestrictions);

        // Shuffle options for variety
        Collections.shuffle(breakfastOptions);
        Collections.shuffle(lunchOptions);
        Collections.shuffle(dinnerOptions);

        // Build the diet plan text
        StringBuilder plan = new StringBuilder();
        plan.append("PERSONALIZED DIET PLAN\n\n");
        plan.append("Daily Calorie Target: ").append(dailyCalories).append(" calories\n");
        plan.append("Goal: ").append(dietGoal).append("\n\n");

        plan.append("BREAKFAST (").append(breakfastCalories).append(" cal):\n");
        plan.append("- ").append(breakfastOptions.get(0)).append("\n\n");

        plan.append("LUNCH (").append(lunchCalories).append(" cal):\n");
        plan.append("- ").append(lunchOptions.get(0)).append("\n\n");

        plan.append("DINNER (").append(dinnerCalories).append(" cal):\n");
        plan.append("- ").append(dinnerOptions.get(0)).append("\n\n");

        plan.append("GENERAL TIPS:\n");
        plan.append("- Drink 8 glasses of water daily\n");
        plan.append("- Limit sugar and processed foods\n");
        plan.append("- Eat fresh fruits and vegetables\n");

        return plan.toString();
    }

    private List<String> getBreakfastOptions(String restrictions) {
        List<String> options = new ArrayList<>();
        if (restrictions.toLowerCase().contains("vegan")) {
            options.addAll(Arrays.asList(
                    "Oatmeal with almond milk and chia seeds",
                    "Tofu scramble with vegetables",
                    "Avocado toast on whole grain bread with nutritional yeast",
                    "Smoothie bowl with berries and granola",
                    "Vegan protein pancakes with maple syrup"
            ));
        } else if (restrictions.toLowerCase().contains("vegetarian")) {
            options.addAll(Arrays.asList(
                    "Greek yogurt with berries and honey",
                    "Vegetable omelette with cheese",
                    "Cottage cheese with fruits and nuts",
                    "Whole grain toast with avocado and eggs",
                    "Protein smoothie with whey protein"
            ));
        } else {
            options.addAll(Arrays.asList(
                    "Scrambled eggs with turkey bacon and toast",
                    "Chicken sausage with oatmeal",
                    "Boiled eggs with banana and peanut butter",
                    "Protein pancakes with bacon",
                    "Turkey and egg breakfast burrito"
            ));
        }
        return options;
    }

    private List<String> getLunchOptions(String restrictions) {
        List<String> options = new ArrayList<>();
        if (restrictions.toLowerCase().contains("vegan")) {
            options.addAll(Arrays.asList(
                    "Quinoa salad with beans and avocado",
                    "Lentil curry with brown rice",
                    "Grilled tofu wrap with vegetables",
                    "Chickpea buddha bowl with tahini dressing",
                    "Vegan burrito bowl with guacamole"
            ));
        } else if (restrictions.toLowerCase().contains("vegetarian")) {
            options.addAll(Arrays.asList(
                    "Paneer wrap with mint chutney",
                    "Veggie burger with sweet potato fries",
                    "Dal with chapati and yogurt",
                    "Mediterranean falafel bowl",
                    "Caprese sandwich on whole grain bread"
            ));
        } else {
            options.addAll(Arrays.asList(
                    "Grilled chicken salad with olive oil dressing",
                    "Beef stir fry with brown rice",
                    "Baked fish with quinoa and steamed vegetables",
                    "Turkey and avocado sandwich on whole grain bread",
                    "Chicken burrito bowl with black beans"
            ));
        }
        return options;
    }

    private List<String> getDinnerOptions(String restrictions) {
        List<String> options = new ArrayList<>();
        if (restrictions.toLowerCase().contains("vegan")) {
            options.addAll(Arrays.asList(
                    "Vegetable stir fry with tofu and brown rice",
                    "Chickpea and vegetable soup with crusty bread",
                    "Stuffed bell peppers with quinoa and lentils",
                    "Zucchini pasta with tomato sauce and nutritional yeast",
                    "Black bean burgers with baked sweet potato wedges"
            ));
        } else if (restrictions.toLowerCase().contains("vegetarian")) {
            options.addAll(Arrays.asList(
                    "Mushroom risotto with parmesan",
                    "Palak paneer with brown rice",
                    "Vegetable pasta with alfredo sauce",
                    "Eggplant parmesan with side salad",
                    "Stuffed portobello mushrooms with quinoa"
            ));
        } else {
            options.addAll(Arrays.asList(
                    "Grilled salmon with roasted vegetables",
                    "Chicken stew with brown rice",
                    "Turkey meatballs with whole grain pasta",
                    "Lean beef steak with baked potato and broccoli",
                    "Baked chicken with quinoa and asparagus"
            ));
        }
        return options;
    }
}