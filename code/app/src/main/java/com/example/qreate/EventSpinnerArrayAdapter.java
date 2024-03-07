package com.example.qreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class EventSpinnerArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EventSpinnerArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.organizer_spinner_items, parent,false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.event_name);

        eventName.setText(event.getEventName());

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View customDropdownView = LayoutInflater.from(getContext())
                .inflate(R.layout.organizer_spinner_items, parent, false);

        TextView listItem = customDropdownView.findViewById(R.id.event_name);
        listItem.setText(Objects.requireNonNull(getItem(position)).getEventName());

        return customDropdownView;
    }
}
