package ng.riby.androidtest.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ng.riby.androidtest.Entities.User;
import ng.riby.androidtest.R;

/**
 * Created by Manuel Chris-Ogar on 5/31/2019.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {


    public LocationAdapter(OnLocationClicked clickListener, List<User> locationsList) {
        this.clickListener = clickListener;
        this.locationsList = locationsList;
    }

    OnLocationClicked clickListener;
    List<User> locationsList;

    public interface OnLocationClicked{
        void onClick(int position);
    }
    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.locations_viewholder, parent, false);
        return new LocationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, final int position) {
        User user = locationsList.get(position);
        Log.e("track", "onBindViewHolder: "+ user.getStartLatitude() );
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        holder.startCoordinates.setText(decimalFormat.format(user.getStartLatitude()) + ", ");
        holder.startCoordinates.append(decimalFormat.format(user.getStartLongitude()));
        holder.distanceCovered.setText(user.getDistanceCovered());
        if (user.getEndLatitude() == 0.0){
            holder.endCoordinates.setText(String.valueOf(user.getEndLatitude()) + ", ");
            holder.endCoordinates.append(String.valueOf((user.getEndLongitude())));
        }else{
            holder.endCoordinates.setText(decimalFormat.format(user.getEndLatitude()) + ", ");
            holder.endCoordinates.append(decimalFormat.format(user.getEndLongitude()));
        }


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        Log.d("track", "getItemCount: "+locationsList.size() );
        return locationsList.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        TextView distanceCovered;
        TextView startCoordinates;
        TextView endCoordinates;
        LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            distanceCovered = itemView.findViewById(R.id.distanceCovered);
            rootView = itemView.findViewById(R.id.rootView);
            startCoordinates = itemView.findViewById(R.id.startCoordinates);
            endCoordinates = itemView.findViewById(R.id.endCoordinates);
        }
    }
}
