package com.example.qreate.organizer.qrmenu;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
/**
 * The following class is responsible for the qr generator screen and functionality
 *
 * Outstanding Issue: Qr code is generated from spinner item  but doesn't get sent to firebase just yet spinner is also not pulling from firebase
 * @author Denis Soh
 */
public class OrganizerQRGeneratorActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
    Spinner eventsSpinner;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;


    /**
     * Creates the view and inflates the organizer_generate_qr_code_screen layout
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_generate_qr_code_screen);

        ImageButton backButton = findViewById(R.id.generate_qr_code_screen_backbutton);
        Button createCodesButton = findViewById(R.id.generate_qr_code_confirmbutton);
        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        events = new ArrayList<OrganizerEvent>();

        addEventsInit();

        eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        eventsSpinner = findViewById(R.id.generate_qr_code_spinner);

        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * gets selected item string
             *
             * @param parent the adapter-view of the view
             * @param view current view
             * @param position current position in spinner
             * @param id current id
             *
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addEventsInit();

        eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);

        eventsSpinner.setAdapter(eventSpinnerArrayAdapter);

        ImageView qrCodeSolo = findViewById(R.id.generate_qr_code_qr_image);

        createCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this code needs to be changed to the actual text on the selected spinner item
                try {
                    qrCodeSolo.setImageBitmap(generateQR(eventsSpinner.getSelectedItem().toString()));
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
                // SAVE THE BITMAP TO DATA BASE
                //This code is to make the qr code only generate when 1 of the options are selected not sure why it isn't working
                /*if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    createCodesButton.setText("erm");
                    String selectedText = selectedRadioButton.getText().toString();
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode("REPLACE THIS WITH THE SELECTED EVENT TEXT", BarcodeFormat.QR_CODE, 250,250);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        qrCodeSolo.setImageBitmap(bitmap);
                        createCodesButton.setText("uhhh");
                        // SAVE THE BITMAP TO DATA BASE
                    } catch (WriterException e) {
                        throw new RuntimeException(e);
                    }
                }*/
            }
        });
        backButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

    }

    //Temporary to test swap this with the firebase data
    private void addEventsInit(){
        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};
        for(int i=0;i<cities.length;i++){
            events.add((new OrganizerEvent(cities[i], provinces[i],"date", "doesnt work here")));
        }
    }
    /**
     * generates bitmap of qr code based on string
     *
     * @param key string
     *
     */
    public Bitmap generateQR(String key) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(key, BarcodeFormat.QR_CODE, 250,250);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.createBitmap(bitMatrix);
    }
}
