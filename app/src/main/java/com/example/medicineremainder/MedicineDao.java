package com.example.medicineremainder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MedicineDao {
    @Insert
    void insertMedicine(Medicine medicine);

    @Update
    void updateMedicine(Medicine medicine);

    @Query("SELECT * FROM medicines WHERE taken = 0")
    List<Medicine> getActiveMedicines();

    @Query("SELECT * FROM medicines WHERE taken = 1 ORDER BY takenTimestamp DESC")
    List<Medicine> getMedicineHistory();

    @Query("SELECT * FROM medicines")
    List<Medicine> getAllMedicines();
}