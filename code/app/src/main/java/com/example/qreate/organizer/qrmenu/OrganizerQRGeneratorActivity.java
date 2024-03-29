package com.example.qreate.organizer.qrmenu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The following class is responsible for the qr generator screen and functionality
 *
 * @author Akib Zaman Choudhury
 */
public class OrganizerQRGeneratorActivity extends AppCompatActivity {

    String documentId = "t1IPHNATtoCOXrEHphlF" ; // Dummy variable containing event doc id, should be the spinner value
    int selectedId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_generate_qr_code_screen);

        changeRadioColor(); // Sets the radio color button to be orange when selected

        ImageButton backButton = findViewById(R.id.generate_qr_code_screen_backbutton);
        Button confirmButton = findViewById(R.id.generate_qr_code_confirmbutton);
        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);
        ImageView qrCodeImageView = findViewById(R.id.generate_qr_code_qr_image);

        //DummyLoadUpData(); // Dummy Variable func call

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If statement to check if QRcode for that event already exists

                selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // No radio item selected
                    Toast.makeText(OrganizerQRGeneratorActivity.this, "Error: Select QR type", Toast.LENGTH_SHORT).show();

                } else {

                    // Sets up field value
                    String field = null;
                    if (selectedId == R.id.generate_qr_code_radio_attendee) {
                        field = "attendee_qr_code";
                    } else if (selectedId == R.id.generate_qr_code_radio_promo) {
                        field = "promo_qr_code";

                    }

                    // Checks if field exists
                    checkIfQRCodePathExists(field, documentId, new QRCodeExistsCallback() {
                        @Override
                        public void onResult(boolean exists) {

                            if (!exists) {
                                // QR code path exists
                                try {
                                    String randomString = UUID.randomUUID().toString();
                                    Bitmap bitmap = generateQRCode(randomString);
                                    qrCodeImageView.setImageBitmap(bitmap);
                                    uploadQRCode(bitmap, randomString);
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // QR code path does not exist
                                Toast.makeText(OrganizerQRGeneratorActivity.this, "Error: QRcode already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error
                        }
                    });
                    //


                }

            }
        });


        backButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

    }


    interface QRCodeExistsCallback {
        void onResult(boolean exists);
        void onError(Exception e);
    }

    // Method to check if QR code path exists
    public void checkIfQRCodePathExists(String field, String documentId, QRCodeExistsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists() && document.contains(field)) {
                                String qrCodePath = document.getString(field);
                                // Invoke callback based on whether the QR code path is not null or empty
                                callback.onResult(qrCodePath != null && !qrCodePath.isEmpty());
                            } else {
                                // Document does not exist or does not have the attendee_qr_code field
                                callback.onResult(false);
                            }
                        } else {
                            // Task failed with an exception
                            callback.onError(task.getException());
                        }
                    }
                });
    }











    // Generates the qr code
    private Bitmap generateQRCode(String text) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
        Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);

        for (int x = 0; x < 512; x++) {
            for (int y = 0; y < 512; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    // Uploads the image to storage
    private void uploadQRCode(Bitmap bitmap, final String imageName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // This is the storage path we're going to save in Firestore
        String storagePath = "qr_codes/" + imageName + ".png";
        StorageReference qrCodeRef = storageRef.child(storagePath);

        qrCodeRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {

                    if (selectedId == R.id.generate_qr_code_radio_attendee) {
                        saveImagePathToFirestore(storagePath, documentId, "attendee_qr_code");

                    } else if (selectedId == R.id.generate_qr_code_radio_promo) {
                        saveImagePathToFirestore(storagePath, documentId, "promo_qr_code");

                    }


                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                });
    }

    private void saveImagePathToFirestore(String storagePath, String documentId, String QRtype) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put(QRtype, storagePath);

        db.collection("Events").document(documentId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "QRcode was successfully generated", Toast.LENGTH_SHORT).show();
                    //finish();
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }






    // Change radio Group color
    private void changeRadioColor() {

        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);

        // Define the color state list for checked and unchecked states
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked state
                        new int[]{android.R.attr.state_checked} // checked state
                },
                new int[]{
                        Color.parseColor("#CCCCCC"), // gray color for unchecked state in hex
                        Color.parseColor("#FCA311") // red color for checked state in hex
                }
        );

        // Iterate over all RadioButtons in the RadioGroup
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View child = radioGroup.getChildAt(i);

            // Check if the view is a RadioButton
            if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;

                // Apply the color state list to the RadioButton
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    radioButton.setButtonTintList(colorStateList);
                } else {
                    CompoundButtonCompat.setButtonTintList(radioButton, colorStateList); // Support library for pre-Lollipop
                }
            }
        }

    }









//    // Dummy Variable func
//    private void DummyLoadUpData() {
//
//        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Query the "Organizers" collection for the document with the given device_id
//        db.collection("Organizers")
//                .whereEqualTo("device_id", device_id)
//                .limit(1) // Assuming device_id is unique and we expect only one document
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        // Assuming 'event_list' is an array of DocumentReferences
//                        DocumentSnapshot organizerDoc = queryDocumentSnapshots.getDocuments().get(0);
//                        Object eventListObj = organizerDoc.get("event_list");
//                        Log.w("DummyData", eventListObj);
//
//                        if (eventListObj instanceof List && !((List) eventListObj).isEmpty()) {
//                            // Get the first item from the event_list
//                            DocumentReference firstEventRef = (DocumentReference) ((List) eventListObj).get(0);
//
//                            firstEventRef.get().addOnSuccessListener(eventDocSnapshot -> {
//                                if (eventDocSnapshot.exists()) {
//                                    // Assign the event document ID to the global variable
//                                    eventRef = eventDocSnapshot.getId();
//                                    Log.w("DummyData", eventRef);
//                                    // Do something with the event ID if necessary
//                                } else {
//                                    // Handle the case where the event document does not exist
//                                }
//                            }).addOnFailureListener(e -> {
//                                // Handle failure to retrieve the event document
//                            });
//                        } else {
//
//                            // Handle the case where event_list is not a list or is empty
//                        }
//                    } else {
//                        // Handle the case where no organizer document with the given device_id was found
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Handle the error
//                });
//
//    }


























}
//    ArrayList<OrganizerEvent> events;
//    Spinner eventsSpinner;
//    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;
//
//
//    /**
//     * Creates the view and inflates the organizer_generate_qr_code_screen layout
//     *
//     * @param savedInstanceState If non-null, this fragment is being re-constructed
//     * from a previous saved state as given here.
//     *
//     */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.organizer_generate_qr_code_screen);
//
//        ImageButton backButton = findViewById(R.id.generate_qr_code_screen_backbutton);
//        Button createCodesButton = findViewById(R.id.generate_qr_code_confirmbutton);
//        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);
//        int selectedId = radioGroup.getCheckedRadioButtonId();
//        events = new ArrayList<OrganizerEvent>();
//
//        addEventsInit();
//
//        eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);
//
//        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
//        eventsSpinner = findViewById(R.id.generate_qr_code_spinner);
//
//        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            /**
//             * gets selected item string
//             *
//             * @param parent the adapter-view of the view
//             * @param view current view
//             * @param position current position in spinner
//             * @param id current id
//             *
//             */
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        addEventsInit();
//
//        eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);
//
//        eventsSpinner.setAdapter(eventSpinnerArrayAdapter);
//
//        ImageView qrCodeSolo = findViewById(R.id.generate_qr_code_qr_image);
//
//        createCodesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //this code needs to be changed to the actual text on the selected spinner item
//                try {
//                    qrCodeSolo.setImageBitmap(generateQR(eventsSpinner.getSelectedItem().toString()));
//                } catch (WriterException e) {
//                    throw new RuntimeException(e);
//                }
//                // SAVE THE BITMAP TO DATA BASE
//                //This code is to make the qr code only generate when 1 of the options are selected not sure why it isn't working
//                /*if (selectedId != -1) {
//                    RadioButton selectedRadioButton = findViewById(selectedId);
//                    createCodesButton.setText("erm");
//                    String selectedText = selectedRadioButton.getText().toString();
//                    try{
//                        BitMatrix bitMatrix = multiFormatWriter.encode("REPLACE THIS WITH THE SELECTED EVENT TEXT", BarcodeFormat.QR_CODE, 250,250);
//                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//                        qrCodeSolo.setImageBitmap(bitmap);
//                        createCodesButton.setText("uhhh");
//                        // SAVE THE BITMAP TO DATA BASE
//                    } catch (WriterException e) {
//                        throw new RuntimeException(e);
//                    }
//                }*/
//            }
//        });
//        backButton.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        }));
//
//    }
//
//    //Temporary to test swap this with the firebase data
//    private void addEventsInit(){
//        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
//        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};
//        for(int i=0;i<cities.length;i++){
//            events.add((new OrganizerEvent(cities[i], provinces[i],"date", "doesnt work here")));
//        }
//    }
//    /**
//     * generates bitmap of qr code based on string
//     *
//     * @param key string
//     *
//     */
//    public Bitmap generateQR(String key) throws WriterException {
//        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//        BitMatrix bitMatrix = multiFormatWriter.encode(key, BarcodeFormat.QR_CODE, 250,250);
//        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//        return barcodeEncoder.createBitmap(bitMatrix);
//    }
//}