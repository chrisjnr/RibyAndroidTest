package ng.riby.androidtest.Utils;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Manuel Chris-Ogar on 5/31/2019.
 */
public class DateRoomConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }

}
