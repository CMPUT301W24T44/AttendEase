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
    private ArrayList<Notif> notifs;
    private Context mContext;

    /**
     * Constructs a new NotifArrayAdapter.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param notifs An ArrayList of Notif objects to display in a list.
     */

    public NotifArrayAdapter(Context context, ArrayList<Notif> notifs){
        super(context,0,notifs);
        this.notifs = notifs;
        this.mContext=context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewItem = convertView;

        if(viewItem == null){
            viewItem = LayoutInflater.from(mContext).inflate(R.layout.attendee_notifications_list_rows, parent,false);
        }

        Notif currentNotif = notifs.get(position);

        TextView notifName = viewItem.findViewById(R.id.notif_description_text);
        TextView organizerName = viewItem.findViewById(R.id.notif_organizer_text);

        notifName.setText(currentNotif.getNotificationDescription());
        organizerName.setText(currentNotif.getOrganizerName());

        return viewItem;
    }

}
