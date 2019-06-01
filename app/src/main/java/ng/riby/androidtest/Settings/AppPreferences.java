package ng.riby.androidtest.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import ng.riby.androidtest.Utils.Constants;

/**
 * Created by Manuel Chris-Ogar on 5/31/2019.
 */
public class AppPreferences {
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public AppPreferences(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(Constants.isTracking,
                Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }
    public boolean isTrackingUser() {
        return appSharedPrefs.getBoolean(Constants.isTracking, false);
    }

    public void trackUser() {
        prefsEditor.putBoolean(Constants.isTracking, true);
        prefsEditor.commit();
    }

    public void stopTracking(){
        prefsEditor.putBoolean(Constants.isTracking, false);
        prefsEditor.commit();
    }

    public void putLocationId(long id){
        prefsEditor.putLong(Constants.current_id, id);
        prefsEditor.commit();
    }
    public long getLocationId(){
        return appSharedPrefs.getLong(Constants.current_id, 0);
    }
}
