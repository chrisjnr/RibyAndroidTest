package ng.riby.androidtest.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import ng.riby.androidtest.R;

/**
 * Created by Manuel Chris-Ogar on 5/31/2019.
 */
public class TrackingService extends Service {
    FusedLocationProviderClient client;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
    }
    PendingIntent broadcastIntent;

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stop_broadcast, new IntentFilter(stop));
        broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_CANCEL_CURRENT);
        // Create the persistent notification
        createNotificationChannel("tracking Location", "livetracking");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "12345")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(broadcastIntent);
//                .setSmallIcon(R.drawable.ic_tracker);
        startForeground(1, builder.build());
        requestLocationUpdates();
    }

    private void createNotificationChannel(CharSequence name, String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance2 = NotificationManager.IMPORTANCE_HIGH;
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("12345", name, importance2);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected BroadcastReceiver stop_broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast", "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stop_broadcast);
            stopSelf();
        }
    };

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(100);
        request.setFastestInterval(10);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request,locationCallback
            , null);
        }
    }

    protected LocationCallback locationCallback  = new LocationCallback(){

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("tracker", "onDestroy: stopped");
        unregisterReceiver(stop_broadcast);
        client.removeLocationUpdates(locationCallback);
    }
}
