package ng.riby.androidtest.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ng.riby.androidtest.Entities.User;
import ng.riby.androidtest.Utils.Constants;

/**
 * Created by Manuel Chris-Ogar on 5/31/2019.
 */

@Dao
public interface UserDao  {

    @Query("SELECT * FROM user_location_table " + Constants.USER_LOCATION_TABLE)
    List<User> getAllCoordinates();

    @Insert
    Long addLocation(User user);

    @Update
    void updateLocation(User user);

    @Delete
    void deleteLocation(User user);
}
