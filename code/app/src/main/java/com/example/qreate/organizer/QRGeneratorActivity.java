package com.example.qreate.organizer;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.qreate.Event;
import com.example.qreate.EventSpinnerArrayAdapter;
import com.example.qreate.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class QRGeneratorActivity extends AppCompatActivity {
    ArrayList<Event> events;
    Spinner eventsSpinner;
    EventSpinnerArrayAdapter eventSpinnerArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_generate_qr_code_screen);

        ImageButton backButton = findViewById(R.id.generate_qr_code_screen_backbutton);
        Button createCodesButton = findViewById(R.id.generate_qr_code_confirmbutton);
        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        events = new ArrayList<Event>();

        addEventsInit();

        eventSpinnerArrayAdapter = new EventSpinnerArrayAdapter(this, events);

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        eventsSpinner = findViewById(R.id.generate_qr_code_spinner);

        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            @Override
            public void onClick(View v) {
                try{
                    //this code needs to be changed to the actual text on the selected spinner item
                    BitMatrix bitMatrix = multiFormatWriter.encode(eventsSpinner.getSelectedItem().toString(), BarcodeFormat.QR_CODE, 250,250);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qrCodeSolo.setImageBitmap(bitmap);
                    // SAVE THE BITMAP TO DATA BASE
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
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
            events.add((new Event(cities[i], provinces[i])));
        }
    }
}
