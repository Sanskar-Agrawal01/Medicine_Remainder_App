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
    private boolean taken;
    private long takenTimestamp;

    public Medicine(String name, String dosage, String time, String duration) {
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.duration = duration;
        this.taken = false;
        this.takenTimestamp = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public String getTime() { return time; }
    public String getDuration() { return duration; }
    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) { this.taken = taken; }
    public long getTakenTimestamp() { return takenTimestamp; }
    public void setTakenTimestamp(long takenTimestamp) { this.takenTimestamp = takenTimestamp; }
}