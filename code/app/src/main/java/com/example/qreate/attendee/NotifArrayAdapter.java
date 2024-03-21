package com.example.qreate.attendee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qreate.R;

import java.util.ArrayList;
/**
 * NotifArrayAdapter is a custom ArrayAdapter designed to display notification objects (Notif) within a ListView.
 * This adapter is for displaying each notification's details, such as its description
 * and the organizer's name, in a structured format as defined in the attendee_notifications_list_rows layout.
 *
 * It extends ArrayAdapter to leverage built-in Android functionality while providing a custom
 * view for each item in the list.
 *
 * @author Shraddha Mehta
 */

public class NotifArrayAdapter extends ArrayAdapter<Notif> {
    private final ArrayList<Notif> notifs;
    private final Context context;

    /**
     * Constructs a new NotifArrayAdapter.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param notifs An ArrayList of Notif objects to display in a list.
     */

    public NotifArrayAdapter(Context context, ArrayList<Notif> notifs){
        super(context,0,notifs);
        this.notifs = notifs;
        this.context=context;
    }
    /**
     * Provides a view for an AdapterView
     * Checks if an existing view is being reused, otherwise inflates the view.
     *
     *
     * @param position The position in the list of data that should be displayed in the list item view.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.attendee_notifications_list_rows, parent,false);
        }

        Notif notif = notifs.get(position);

        TextView notifName = view.findViewById(R.id.notif_description_text);
        TextView organizerName = view.findViewById(R.id.notif_organizer_text);

        notifName.setText(notif.getNotificationDescription());
        organizerName.setText(notif.getOrganizerName());

        return view;
    }

}
