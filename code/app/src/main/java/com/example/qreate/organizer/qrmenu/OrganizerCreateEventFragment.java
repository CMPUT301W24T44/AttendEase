package com.example.qreate.organizer.qrmenu;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.example.qreate.MainActivity;
import com.example.qreate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * The following class is responsible for the create event popup
 *
 * Outstanding Issue: Created event isn't stored in firebase yet
 * @author Denis Soh
 */
public class OrganizerCreateEventFragment extends DialogFragment {
    private static final int REQUEST_IMAGE_PICKER = 1;
    private ImageButton addPoster;
    private TextView dateText;
    Uri selectedImageUri;
    private StorageReference storageRef;

    /*private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                        }
                    });*/

    interface AddEventDialogListener {
        void addEvent(OrganizerEvent event);
    }

    private AddEventDialogListener listener;

    /**
     * attaches fragment and adds listener
     *
     * @param context current context
     */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener) {
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }

    /**
     * creates fragment
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_create_event, null);
        EditText addName = view.findViewById(R.id.editTextEventName);
        EditText addDescription = view.findViewById(R.id.editTextEventDescription);
        Button createButton = view.findViewById(R.id.buttonCreateEvent);
        Button dateButton = view.findViewById(R.id.dateButton);
        ImageButton addPoster = view.findViewById(R.id.buttonUploadPoster);
        TextView posterName = view.findViewById(R.id.imageName);
        TextView dateText = view.findViewById(R.id.dateText);
        addPoster.setOnClickListener(v -> {
            openImagePicker();
            posterName.setVisibility(View.VISIBLE);
            //addPoster.setVisibility(View.INVISIBLE);
        });
        storageRef = FirebaseStorage.getInstance().getReference();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        //TODO don't need this cancel button we should change it such that the fragment closes once you click off
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        createButton.setOnClickListener(v -> {
            String eventName = addName.getText().toString();
            String eventDescription = addDescription.getText().toString();
            listener.addEvent(new OrganizerEvent(eventName, eventDescription, dateText.getText().toString(), Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID)));
            uploadEventData(new OrganizerEvent(eventName, eventDescription,dateText.getText().toString(), Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID)));
            dialog.dismiss();
        });
        return dialog;
    }

    /**
     * Creates the view and inflates the fragment_create_event layout
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        EditText addName = view.findViewById(R.id.editTextEventName);
        EditText addDescription = view.findViewById(R.id.editTextEventDescription);
        ImageButton addPosterButton = view.findViewById(R.id.buttonUploadPoster);
        Button createButton = view.findViewById(R.id.buttonCreateEvent);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);

    }

    /**
     * opens image picker
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICKER);
    }
//TODO ADD THE JAVA DOCS
    private void openDatePicker(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Handle the selected date (e.g., update UI)
                        String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        dateText.setText(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }



    /**
     * gets image picker result might need to be updated as there are depreciated components
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            // Handle the selected image here
            selectedImageUri = data.getData();
            // Load the image into your ImageView or process it further
            //TODO THIS CRASHES CODE IDK WHY
            //addPoster.setImageURI(selectedImageUri);
        }
    }

    private void uploadEventData(OrganizerEvent event) {

        // TODO date, organizer, location, time, the qr code
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //db.collection("Events").document(event.getEvent()).set(event);
        Map<String, Object> eventHash = new HashMap<>();
        eventHash.put("organizer", event.getOrganizer());
        eventHash.put("name", event.getEvent());
        eventHash.put("description", event.getDetail());
        eventHash.put("date", event.getDate());
        eventHash.put("poster", "posters/" + event.getEvent() + "Poster.jpg");
        uploadImage(event);

        db.collection("Events").add(eventHash);
    }
//something here isn't working >:(
    private void uploadImage(OrganizerEvent event) {
        if (selectedImageUri != null) {
            StorageReference fileRef = storageRef.child("posters/" + event.getEvent() + "Poster.jpg");
            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            Toast.makeText(requireContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure (e.g., show an error message)
                            Toast.makeText(requireContext(), "Image upload failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(requireContext(), "Image upload failed! no image", Toast.LENGTH_SHORT).show();
        }
    }
}