package com.minerdev.greformanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {House.class, Image.class}, version = 1, exportSchema = false)
public abstract class GreDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile GreDatabase INSTANCE;
    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                INSTANCE.houseDao().deleteAll();
                INSTANCE.imageDao().deleteAll();
            });
        }
    };

    public static GreDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GreDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GreDatabase.class, "gre_db")
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract HouseDao houseDao();

    public abstract ImageDao imageDao();
}
