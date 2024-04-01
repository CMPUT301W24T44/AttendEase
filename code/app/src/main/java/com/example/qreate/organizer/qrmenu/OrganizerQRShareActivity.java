package com.example.qreate.organizer.qrmenu;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;


import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


<<<<<<< Updated upstream
=======
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
>>>>>>> Stashed changes
import java.util.ArrayList;
import java.util.List;


public class OrganizerQRShareActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
<<<<<<< Updated upstream
=======
    private Button testButton;
    Bitmap idk;
    private OrganizerEvent selectedEvent;
    //File cacheDir = getCacheDir();
>>>>>>> Stashed changes
    private FirebaseFirestore db;
    Uri firebaseUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Spinner eventsSpinner;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;
    //temporary fake id
    String documentId = "LrXKKSgx3TmrSWiWZnQc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_share_qr_code_screen);

        db = FirebaseFirestore.getInstance();


        events = new ArrayList<OrganizerEvent>();


        addEventsInit();


        eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);


        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        eventsSpinner = findViewById(R.id.share_qr_code_spinner);


        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                //getPromoQR();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
        //TODO get promo qr grabs the qr for the selected event so call it everytime a new event is selected
        getPromoQR();


        eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);


        eventsSpinner.setAdapter(eventSpinnerArrayAdapter);


        Button shareButton = findViewById(R.id.share_qr_code_sharebutton);

        //this way just crashes it too
        /*try {
            URL url = new URL("https://firebasestorage.googleapis.com/v0/b/qreate-bb8b8.appspot.com/o/qr_codes%2F537b55b1-7ed9-4202-9a82-815cca1715a5.png?alt=media&token=77d6e246-c9de-47ad-8a90-838889935feb");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Now you have the image bitmap
            // Set it to your ImageView or use it as needed
            String path = MediaStore.Images.Media.insertImage(
                    this.getContentResolver(),
                    bitmap,
                    "Title", // Provide a title for the image
                    null
            );
            firebaseUri = Uri.parse(path);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/qreate-bb8b8.appspot.com/o/qr_codes%2F537b55b1-7ed9-4202-9a82-815cca1715a5.png?alt=media&token=77d6e246-c9de-47ad-8a90-838889935feb");


        shareButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                //GOTTA PUT THE IMAGE LOCATION HERE
                //Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/qricon.png");

                sharingIntent.putExtra(Intent.EXTRA_STREAM, firebaseUri);
<<<<<<< Updated upstream
                startActivity(Intent.createChooser(sharingIntent, "Share Image"));
=======
                startActivity(Intent.createChooser(sharingIntent, "Share Image"));*/
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png"); // Set the appropriate image type
                //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(downloadUrl));
                //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("https://firebasestorage.googleapis.com/v0/b/qreate-bb8b8.appspot.com/o/qr_codes%2F537b55b1-7ed9-4202-9a82-815cca1715a5.png?alt=media&token=77d6e246-c9de-47ad-8a90-838889935feb"));
                shareIntent.putExtra(Intent.EXTRA_STREAM, idk);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
>>>>>>> Stashed changes
            }
        }));


        //Back Button
        ImageButton backButton = findViewById(R.id.share_qr_code_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPromoQR(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            storageRef.child(document.getString("promo_qr_code")).getDownloadUrl().addOnSuccessListener(uri -> {
<<<<<<< Updated upstream
=======
                            /*promoRef.child("537b55b1-7ed9-4202-9a82-815cca1715a5.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri) {
                                    firebaseUri = uri;
                                }*/
                                //Content uri code this fix didn't work
                                //firebaseUri = FileProvider.getUriForFile(context, "com.example.qreate.organizer.qrmenu", new File(uri.getPath()));
                                //String imageUrl = String.valueOf(uri);
>>>>>>> Stashed changes
                                firebaseUri = uri;
                            }).addOnFailureListener(exception -> {
<<<<<<< Updated upstream
                                // Handle any errors (e.g., image not found, network issues)
                            });
=======
                                // this error doesn't pop up
                                Log.e("ImageError", "Error downloading image: " + exception.getMessage());
                            });
                            //different content uri fix this didn't work either
                            StorageReference imageRef = storageRef.child(document.getString("promo_qr_code"));
                            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Convert byte array to Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                                    //String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Promo", null);
                                    //firebaseUri = Uri.parse(path);
                                    idk = bitmap;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
>>>>>>> Stashed changes
                        } else {
                            // Task failed with an exception
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
    }


    //Temporary to test swap this with the firebase data
    private void addEventsInit(){


        // TODO THIS CODE CRASHES IF THERES NO DETAIL OR DATE SO I COMMENTED IT OUT UNCOMMENT WHEN DATA IS FIXED
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
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


                                List<DocumentReference> referenceArray = (List<DocumentReference>) document.get("events_list");


                                //assert createdEvents != null;
                                for (DocumentReference reference : referenceArray) {
                                    reference.get().addOnCompleteListener(referencedTask -> {
                                        if (referencedTask.isSuccessful()) {
                                            DocumentSnapshot referencedDocument = referencedTask.getResult();
                                            if (referencedDocument.exists()) {
                                                //TODO description/dates are not set in most firebase stuff this will cause it to crash
                                                String eventName = referencedDocument.getString("name");
                                                //String eventDetails = document.getString("description");
                                                //String eventDate = document.getString("date");
                                                String eventOrganizer = referencedDocument.getString("organizer");
                                                String eventID = referencedDocument.getId();
                                                events.add(new OrganizerEvent(eventName, "details", "date", eventOrganizer, eventID));
                                            } else {
                                                System.out.println("Referenced document does not exist");
                                            }
                                        } else {
                                            System.out.println("Error fetching referenced document: " + referencedTask.getException());
                                        }
                                    });
                                }




                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
    }
}



