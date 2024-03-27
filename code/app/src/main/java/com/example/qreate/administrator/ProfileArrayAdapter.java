package com.example.qreate.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qreate.R;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter designed to handle lists of {@link AdministratorProfile} objects. This adapter is tailored
 * for displaying administrator profiles within a ListView in the administrator dashboard. Each list item features a
 * profile name, an optional profile image, and a radio button to select a specific profile, allowing for single-choice
 * selection behavior within the list.
 */
public class ProfileArrayAdapter extends ArrayAdapter<AdministratorProfile> {
    private int selectedPosition = -1; // Track the selected position
    private ProfileArrayAdapter.OnProfileSelectedListener mListener;

    /**
     * Constructs a new {@link ProfileArrayAdapter}.
     *
     * @param context  The current context. This value cannot be null.
     * @param profiles An ArrayList of {@link AdministratorProfile} objects to be represented in the ListView.
     *                 This value cannot be null.
     */
    public ProfileArrayAdapter(Context context, ArrayList<AdministratorProfile> profiles, ProfileArrayAdapter.OnProfileSelectedListener listener) {
        super(context, 0, profiles);
        mListener = listener;
    }

    /**
     * Provides a view (of profiles list) for the ListView
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_profiles_list, parent, false);
        } else {
            view = convertView;
        }

        AdministratorProfile profile = getItem(position);
        TextView profile_name = view.findViewById(R.id.profile_name_text);
        ImageView profile_image = view.findViewById(R.id.profile_image);
        RadioButton radioButton = view.findViewById(R.id.choose_profile_radio_button);

        profile_name.setText(profile.getProfileName());
        //profile_image.setImageResource(R.drawable.profile);
        radioButton.setChecked(position == selectedPosition);

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
                mListener.onProfileSelected();
            }
        });
        return view;
    }

    public interface OnProfileSelectedListener {
        void onProfileSelected();
    }

    public void clearSelection() {
        selectedPosition = -1; // Reset the selected position
        notifyDataSetChanged(); // Notify the adapter to refresh the list view
    }

    public String getSelectedProfileId() {
        if (selectedPosition != -1) {
            AdministratorProfile selectedProfile = getItem(selectedPosition);
            return selectedProfile.getId();
        }
        return null;
    }
}
