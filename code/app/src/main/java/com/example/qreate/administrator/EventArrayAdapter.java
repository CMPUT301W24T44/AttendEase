package com.example.qreate.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qreate.R;

import java.util.ArrayList;

/**
 * An ArrayAdapter subclass for displaying a list of {@link AdministratorEvent} objects.
 * This adapter is designed to be used with a ListView in the administrator dashboard to show events
 * with their names, organizers, and a radio button to select a specific event. Only one event
 * can be selected at a time.
 */
public class EventArrayAdapter extends ArrayAdapter<AdministratorEvent> {
    private int selectedPosition = -1; // Track the selected position
    private OnEventSelectedListener mListener;

    /**
     * Constructs a new {@code EventArrayAdapter}.
     * @param context The current context. Used to inflate the layout file.
     * @param events An ArrayList of {@link AdministratorEvent} objects to display in the list.
     */
    public EventArrayAdapter(Context context, ArrayList<AdministratorEvent> events, OnEventSelectedListener listener) {
        super(context, 0, events);
        mListener = listener;
    }

    /**
     * Provides a view (of events list) for the ListView
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_event_list, parent, false);
        } else {
            view = convertView;
        }

        AdministratorEvent event = getItem(position);
        TextView event_name = view.findViewById(R.id.event_name_text);
        TextView event_organizer = view.findViewById(R.id.event_subtitle_text);
        RadioButton radioButton = view.findViewById(R.id.choose_event_radio_button);

        event_name.setText(event.getEventName());
        event_organizer.setText(event.getEventOrganizer());
        radioButton.setChecked(position == selectedPosition);

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
                mListener.onEventSelected();
            }
        });
        return view;
    }

    public interface OnEventSelectedListener {
        void onEventSelected();
    }

    /*
    public void clearSelection() {
        selectedPosition = -1; // Reset the selected position
        notifyDataSetChanged(); // Notify the adapter to refresh the list view
    }

     */

    public String getSelectedEventId() {
        if (selectedPosition != -1) {
            AdministratorEvent selectedEvent = getItem(selectedPosition);
            return selectedEvent.getId();
        }
        return null;
    }
}
