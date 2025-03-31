package com.example.medicineremainder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicines")
public class Medicine {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String dosage;
    private String time;
    private String duration;

    public Medicine(String name, String dosage, String time, String duration) {
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.duration = duration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public String getTime() { return time; }
    public String getDuration() { return duration; }
}