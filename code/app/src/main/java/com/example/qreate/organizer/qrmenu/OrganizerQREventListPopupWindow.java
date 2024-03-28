package com.example.qreate.organizer.qrmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;



import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.qreate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

// https://www.youtube.com/watch?v=qCoidM98zNk

public class OrganizerQREventListPopupWindow {

    private Context context;
    private PopupWindow popupWindow;
    private View popupView;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button uploadPosterButton;

    private Date selectedDate;
    private Uri selectedImageUri;
    private String imagePath;
    private String imageName;
    private String name;
    private String description;
    private String signupLimit;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int IMAGE_PICK_CODE = 1000;

    public OrganizerQREventListPopupWindow(Context context) {

        this.context = context;

        // Inflate the custom layout/view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.organizer_fragment_create_event, null);

        initializePopupWindow(); // Creates the pop up menu

        // Sets up the date picker button
        dateButton = popupView.findViewById(R.id.dateselector);
        dateButton.setText(getTodaysDate());
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDatePickerDialog();
                initDatePicker();
            }
        });

        uploadPosterButton = popupView.findViewById(R.id.uploadButton);
        uploadPosterButton.setOnClickListener(view -> {
            openImageSelector();

        });

        // Confirm button actions
        Button confirmDataButton = popupView.findViewById(R.id.buttonCreateEvent);
        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setInfoUp(popupView);
                //Log.w("nameCheck", String.valueOf(name));

                if (imageName != null && !Objects.equals(name, "") && !Objects.equals(description, "")) {
                    // Uploads the image and then creates the event automatically
                    uploadImageToFirebaseStorage(selectedImageUri);
                    //createEvent(popupView); // had to insert this in uploadImageToFirebaseStorage as its an asynchronous event
                    popupWindow.dismiss();
                } else {
                    // imageName is null, show a Toast message
                    Toast.makeText(context, "Fill in All fields", Toast.LENGTH_SHORT).show();
                }




            }
        });

    }

    private void openImageSelector() {
        Activity activity = (Activity) context;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public void setImageUri(Uri imageUri) {
        this.selectedImageUri = imageUri;
        Log.w("UriImage", String.valueOf(selectedImageUri));
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
        uploadPosterButton.setText(imageName);
        Log.w("UriImage", String.valueOf(imageName));
    }


    public String getFileNameByUri(Context context, Uri uri) {
        String fileName = "unknown"; // Default to "unknown" if the file name can't be found
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            // If the device version is Android Q or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Try to get the file name from the column DISPLAY_NAME
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                fileName = cursor.getString(nameIndex);
            } else {
                // For devices below Android Q, you may need to manually parse the last segment
                // of the URI path as the file name
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments != null && pathSegments.size() > 0) {
                    fileName = pathSegments.get(pathSegments.size() - 1);
                }
            }
            cursor.close();
        }
        return fileName;
    }


    private void uploadImageToFirebaseStorage(Uri fileUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("posters/" + UUID.randomUUID().toString());
        storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Saves the image path linking to the firebase storage
                imagePath = storageRef.getPath();
                // Creates the event after getting the storage path
                createEvent(popupView);
            }
        });
    }

    
    private void setInfoUp(View view) {

        EditText editTextName = view.findViewById(R.id.editTextEventName);
        EditText editTextDescription = view.findViewById(R.id.editTextEventDescription);
        EditText editTextLimitSignup = view.findViewById(R.id.signupNumber);


        name = editTextName.getText().toString();
        description = editTextDescription.getText().toString();
        signupLimit = editTextLimitSignup.getText().toString();




    }



    private void createEvent(View view){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setInfoUp(view);

        // Database insertion goes here
        Map<String, Object> device = new HashMap<>();
        device.put("name", name);
        device.put("description", description);
        device.put("date", selectedDate);
        device.put("poster", imagePath);
        device.put("signup_limit", signupLimit);


        // Creates a new Events document in Firestore
        db.collection("Events").add(device)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addEventToOrganizer(documentReference);
                        Log.w("EventFirestore", "Yayy");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EventFirestore", "Nayy");

                    }
                });

    }




    private void addEventToOrganizer(DocumentReference eventRef){


        String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieves User document and updates it
        db.collection("Organizers")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {

                                // Since the unique ID is unique, we only expect one result
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String documentId = document.getId();
                                Log.d("Firestore", documentId);

                                // Update the document directly
                                DocumentReference docRef = db.collection("Organizers").document(documentId);
                                docRef.update("events_list", FieldValue.arrayUnion(eventRef));



                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });


    }












    private void initializePopupWindow() {

        // Create PopupWindow
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        // Dismiss the popup window when touched outside
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        selectedDate = cal.getTime(); // Store the selected date fir firestore use

        return makeDateString(day, month, year);
    }



    private void initDatePicker() {

        Calendar cal = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);

                // sets the selected date for firebase use
                cal.set(year, month, day);
                selectedDate = cal.getTime(); // Store the selected date
            }
        };


        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(context, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

        datePickerDialog.show();

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void showPopupWindow() {
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }


}
