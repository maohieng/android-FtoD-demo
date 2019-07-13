package edu.niptict.cs2.android.demo.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.niptict.cs2.android.demo.db.dao.ContributorDAO;
import edu.niptict.cs2.android.demo.db.entity.ContributorEntity;

@Database(version = 1, entities = {
        ContributorEntity.class
})
public abstract class AppDatabase extends RoomDatabase {

    // SINGLETON

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class, "cs2-demo.db")
                            .build();
                }
            }
        }

        return sInstance;
    }

    public abstract ContributorDAO contributorDAO();

}
