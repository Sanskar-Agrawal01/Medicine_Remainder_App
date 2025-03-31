package com.example.medicineremainder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MedicineDao {
    @Insert
    void insertMedicine(Medicine medicine);

    @Query("SELECT * FROM medicines")
    List<Medicine> getAllMedicines();
}