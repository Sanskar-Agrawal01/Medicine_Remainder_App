package com.example.medicineremainder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMedActivity extends AppCompatActivity {
    private EditText editName, editDosage, editTime, editDuration;
    private MedicineDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        editName = findViewById(R.id.editName);
        editDosage = findViewById(R.id.editDosage);
        editTime = findViewById(R.id.editTime); // Initialize before using it
        editTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddMedActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        editTime.setText(timeFormat);
                    }, hour, minute, false);

            timePickerDialog.show();
        });

        editDuration = findViewById(R.id.editDuration);
        Button btnSave = findViewById(R.id.btnSave);

        db = MedicineDatabase.getInstance(this);

        btnSave.setOnClickListener(v -> saveMedicine());
    }
    private long getTimeInMillis(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setLenient(false);
            Date date = sdf.parse(time);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                Calendar now = Calendar.getInstance();
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, now.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

                // If time has already passed today, schedule for next day
                if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                return calendar.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }



    private void saveMedicine() {
        String name = editName.getText().toString().trim();
        String dosage = editDosage.getText().toString().trim();
        String timeText = editTime.getText().toString().trim();
        String duration = editDuration.getText().toString().trim();

        if (name.isEmpty() || dosage.isEmpty() || timeText.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert time string (e.g., "08:30 AM") to milliseconds
        long triggerTime = getTimeInMillis(timeText);

        // Save to database (Assuming you already have Room database set up)
        Medicine medicine = new Medicine(name, dosage, timeText, duration);
        MedicineDatabase.getInstance(this).medicineDao().insertMedicine(medicine);

        // Schedule notification
        scheduleNotification(this, triggerTime, name, dosage);

        Toast.makeText(this, "Medicine added!", Toast.LENGTH_SHORT).show();
        finish(); // Go back to MainActivity
    }
    private void scheduleNotification(Context context, long triggerTime, String name, String dosage) {
        Log.d("AddMedActivity", "Scheduling notification at: " + triggerTime);

        Intent intent;
        intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("medicine_name", name);
        intent.putExtra("dosage", dosage);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e("AlarmManager", "Exact alarms NOT allowed! Request permission.");
                    return;
                }
            }

            // Use setExactAndAllowWhileIdle() for precise alarms
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
            Log.d("AlarmManager", "Alarm set successfully.");
        } else {
            Log.e("AlarmManager", "AlarmManager is null.");
        }
    }


}
