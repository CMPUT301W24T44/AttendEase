package com.example.qreate.organizer;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.qreate.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGeneratorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr_code_screen);

        ImageButton backButton = findViewById(R.id.generate_qr_code_screen_backbutton);
        Button createCodesButton = findViewById(R.id.generate_qr_code_confirmbutton);
        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        Spinner eventsSpinner = findViewById(R.id.generate_qr_code_spinner);








        ///////THIS PART IS CAUSING THE FRAGMENT TO NOT OPEN///////////////////////////////////////////
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, Integer.parseInt("make this the event array"), android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventsSpinner.setAdapter(adapter);
        /////////////////////////////////////////////////////////////////////////////////////////////





        ImageView qrCodeSolo = findViewById(R.id.generate_qr_code_qr_image);

        createCodesButton.setOnClickListener(new View.OnClickListener() {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            @Override
            public void onClick(View v) {
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedText = selectedRadioButton.getText().toString();
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode(eventsSpinner.getSelectedItem().toString() + selectedText, BarcodeFormat.QR_CODE, 250,250);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        qrCodeSolo.setImageBitmap(bitmap);
                        // SAVE THE BITMAP TO DATA BASE
                    } catch (WriterException e) {
                        throw new RuntimeException(e);
                    }
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
}
