package ng.riby.androidtest.Database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import ng.riby.androidtest.Dao.UserDao;
import ng.riby.androidtest.Entities.User;
import ng.riby.androidtest.Utils.Constants;
import ng.riby.androidtest.Utils.DateRoomConverter;

/**
 * Created by Manuel Chris-Ogar on 5/31/2019.
 */
@Database(entities = {User.class}, version = 1)
@TypeConverters({DateRoomConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao getUserDao();
    private static AppDatabase db;



    public static AppDatabase getInstance(Context context){
        if (null == db){
            db = buildDatabase(context);
        }return db;
    }

    private static AppDatabase buildDatabase(Context context){
        return Room.databaseBuilder(context,AppDatabase.class, Constants.USER_LOCATION_TABLE).allowMainThreadQueries().build();
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {
        db = null;

    }
}
