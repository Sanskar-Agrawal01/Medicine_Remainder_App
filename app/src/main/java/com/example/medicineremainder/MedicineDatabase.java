package com.example.medicineremainder;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Medicine.class}, version = 2)
public abstract class MedicineDatabase extends RoomDatabase {
    public abstract MedicineDao medicineDao();
    private static MedicineDatabase instance;

    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE medicines ADD COLUMN taken INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE medicines ADD COLUMN takenTimestamp INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static synchronized MedicineDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MedicineDatabase.class, "medicine_db")
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}