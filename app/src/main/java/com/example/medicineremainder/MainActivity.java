package com.example.medicineremainder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load MainFragment initially
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MainFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new MainFragment();  // Home
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new bmi();  // BMI Fragment
            }
            else if (item.getItemId() == R.id.nav_diet){
                selectedFragment = new DietPlannerFragment();
            }
            else{
                selectedFragment = new HistoryFragment();
            }



            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
            }
            return true;
        });

    }
}
