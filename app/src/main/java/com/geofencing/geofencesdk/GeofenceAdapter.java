package com.geofencing.geofencesdk;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.geofencing.R;
import com.geofencing.database.GeofenceEventEntity;

import java.util.ArrayList;
import java.util.List;

public class GeofenceAdapter extends RecyclerView.Adapter<GeofenceAdapter.BaseViewHolder> {
    private ArrayList<GeofenceEventEntity> geofenceEventsList;

    public GeofenceAdapter(ArrayList<GeofenceEventEntity> geofenceEventsList) {
        this.geofenceEventsList = geofenceEventsList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(view);
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        try {
            GeofenceEventEntity geofenceEventEntity = (GeofenceEventEntity) geofenceEventsList.get(position);
            holder.userIdTV.setText(geofenceEventEntity.getId());
        }
        catch (Exception e) {
            Log.v("LIST_SIZE_BIND", e.toString());
        }

    }

    @Override
    public int getItemCount() {
        return geofenceEventsList.size();
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public TextView userIdTV;
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTV = itemView.findViewById(R.id.user_id);
        }
    }
}
