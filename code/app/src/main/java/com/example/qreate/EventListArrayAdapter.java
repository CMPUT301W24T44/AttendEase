package com.example.qreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventListArrayAdapter extends RecyclerView.Adapter<AViewHolder> {
    private ArrayList<Event> events;
    private Context context;

    public EventListArrayAdapter(Context context, ArrayList<Event> events){
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public AViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_event_list_recycler_row_layout,parent,false);
        return new AViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AViewHolder holder, int position) {
        holder.eventName.setText(events.get(position).getEventName());

    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
class AViewHolder extends RecyclerView.ViewHolder{
    Button eventName;
    public AViewHolder(@NonNull View itemView) {
        super(itemView);
        eventName = itemView.findViewById(R.id.event_list_item);
    }
}

