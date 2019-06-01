package ng.riby.androidtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.riby.androidtest.Adapters.LocationAdapter;
import ng.riby.androidtest.Database.AppDatabase;
import ng.riby.androidtest.Entities.User;
import ng.riby.androidtest.Retrofit.ApiClient;
import ng.riby.androidtest.Retrofit.ApiInterface;
import ng.riby.androidtest.Retrofit.models.GoogleDistanceApiResponseModel;
import ng.riby.androidtest.Services.TrackingService;
import ng.riby.androidtest.Settings.AppPreferences;
import ng.riby.androidtest.Utils.Constants;
import ng.riby.androidtest.Utils.GPSTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationAdapter.OnLocationClicked{

    private static final int PERMISSIONS_REQUEST = 1;
    private static final String GOOGLE_DISTANCE_MATRIX_KEY = "AIzaSyCoA6vL0t2is5LiW1W1C8HvjkFPOjT4MO4";

    Button startStop;
    boolean isGpsEnabled;
    AppDatabase db;
    RecyclerView recyclerView;
    LocationAdapter adapter;
    List<User> locationList;
    GPSTracker gpsTracker;
    User currentLocationObject;
    static AppPreferences prefManager;

//    static {
//        System.loadLibrary("native-lib");
//    }
//
//    public native String getNativeKey();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();

//        String key1 = new String(Base64.decode(getNativeKey(), Base64.DEFAULT));
//        Log.d("nativedup", "onCreate: "+ key1);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            isGpsEnabled = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

    }

    private void setupViews() {
//        getDistance(40.6655101,-73.89188969999998,40.6905615,-73.9976592);
        db = AppDatabase.getInstance(this);
        locationList = new ArrayList<>();
        adapter = new LocationAdapter(this, locationList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        gpsTracker  = new GPSTracker(this);
        startStop = findViewById(R.id.startStop);
        locationList.addAll(db.getUserDao().getAllCoordinates());
        Log.e("track", "setupViews: "+ db.getUserDao().getAllCoordinates().size() );
        prefManager = new AppPreferences(MainActivity.this);
        if (prefManager.isTrackingUser()) {
            startStop.setText("Stop Tracking Me");


        }else {
            startStop.setText("Track Me");
        }
            startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefManager.isTrackingUser()) {
                    startStop.setText("Track Me");
                    prefManager.stopTracking();
//                    Log.d("tracks", "onClick: "+gpsTracker.getLongitude());
                    stopService(new Intent(MainActivity.this, TrackingService.class));
                    int position = locationList.size();
                    int po = position-=1;
                    User user = locationList.get(po);
                    user.setId(prefManager.getLocationId());
                    user.setEndLongitude(gpsTracker.getLongitude());
                    user.setEndLatitude(gpsTracker.getLatitude());
                    getDistance(user, po);


                }else {
                    prefManager.trackUser();
                    startStop.setText("Stop Tracking Me");
                    if (isGpsEnabled){
                        startTrackerService();
                    }
                }


            }
        });
    }

    @Override
    public void onClick(int position) {
        Toast.makeText(this, ""+ position, Toast.LENGTH_SHORT).show();
        User user = locationList.get(position);
        Log.d("track", "onClick: "+gpsTracker.getLongitude());
        user.setEndLongitude(gpsTracker.getLongitude());
        user.setEndLatitude(gpsTracker.getLatitude());
        new UpdateTask(this, user, position).execute();

    }

    private void startTrackerService() {
        User user = new User(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        currentLocationObject = user;
        new InsertTask(this,user).execute();
        startService(new Intent(this, TrackingService.class));
//        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
                grantResults) {
            if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start the service when the permission is granted
    //            startTrackerService();
            } else {
                finish();
            }
        }



    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {

        WeakReference<MainActivity> activityWeakReference;
        User user;

        public InsertTask(MainActivity context, User user){
            activityWeakReference = new WeakReference<>(context);
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            long id = activityWeakReference.get().db.getUserDao().addLocation(user);
            prefManager.putLocationId(id);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                int size = activityWeakReference.get().locationList.size() - 1;
                activityWeakReference.get().locationList.add(user);
                activityWeakReference.get().adapter.notifyDataSetChanged();
                activityWeakReference.get().recyclerView.scrollToPosition(activityWeakReference.get().adapter.getItemCount() -1);
            }
        }
    }

    private static class UpdateTask extends AsyncTask<Void,Void,Boolean>{
        WeakReference<MainActivity> activityWeakReference;
        User user;
        int adapterPosition;

        public UpdateTask(MainActivity context, User user, int position) {
            this.activityWeakReference = new WeakReference<>(context);
            this.user = user;
            this.adapterPosition = position;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            activityWeakReference.get().db.getUserDao().updateLocation(this.user);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                Log.d("tracks", "finished: "+user.getId()+""+user.getEndLatitude());
                activityWeakReference.get().locationList.set(adapterPosition,this.user);
                activityWeakReference.get().adapter.notifyDataSetChanged();
                activityWeakReference.get().recyclerView.scrollToPosition(activityWeakReference.get().adapter.getItemCount() -1);
            }

        }
    }


    private void getDistance(final User user, final int po){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.ORIGINS, "" + user.getStartLatitude() + "," + user.getStartLongitude());
        params.put(Constants.DESTINATION, "" + user.getEndLatitude() + "," + user.getEndLongitude());
        params.put(Constants.GOOGLE_MAPS_KEY, GOOGLE_DISTANCE_MATRIX_KEY);
        Call<GoogleDistanceApiResponseModel> call = apiInterface.getDistance("json", params);
        call.enqueue(new Callback<GoogleDistanceApiResponseModel>() {
            @Override
            public void onResponse(Call<GoogleDistanceApiResponseModel> call, Response<GoogleDistanceApiResponseModel> response) {
//                Log.d("distance", "onResponse: "+ response.code());
//                Log.d("distance", "onResponse: "+ response.headers());
//                Log.d("distance", "onResponse: "+ response.body().getRows().size());
                user.setDistanceCovered(response.body().getRows().get(0).getElements().get(0).getDistance().getText());
                new UpdateTask(MainActivity.this,user, po).execute();
            }


            /**
             * Fallback Just incase there is no internet to use Google Maps API*/
            @Override
            public void onFailure(Call<GoogleDistanceApiResponseModel> call, Throwable t) {
                Log.d("distance", "onResponse: "+ t.getLocalizedMessage());;
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(user.getStartLatitude());
                location.setLongitude( user.getStartLongitude());
                Location destination = new Location(LocationManager.GPS_PROVIDER);
                destination.setLatitude(user.getEndLatitude());
                destination.setLongitude( user.getEndLongitude());
                float distance = location.distanceTo(destination);
                Log.d("distance", "onFailure: "+ distance);
                user.setDistanceCovered(String.valueOf(distance)+ "m");
                new UpdateTask(MainActivity.this,user, po).execute();



            }
        });
    }



}
