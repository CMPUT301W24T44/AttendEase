package com.example.qreate.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.annotation.Nullable;

/**
 * The AttendeeQRScanner activity provides QR code scanning functionality for attendees.
 * Utilizing the ZXing library, this activity allows users to scan QR codes to retrieve information
 * relevant to the event they are attending, such as checking in at an event.
 *
 * Outstanding: Not implemented fully yet. Make changes according to how it should look like and if we want any
 *              restrictions.
 *
 * References: ankur035, GeeksforGeeks, How to Read QR Code using Zxing Library in Android?, Last Updated: 15 Jan,2021,
 *                  https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
 *
 * @author Shraddha Mehta
 */

public class DoNotRemoveForNowAttendeeQRScanner extends AppCompatActivity{

    //variables
    ImageButton scanButton;
    TextView textContent;

    /**
     * Initializes the activity, setting up the UI components and button click listeners.
     * This method is called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_tap_to_scan_page);

        //initializing
        scanButton = findViewById(R.id.tap_to_scan_qr_button);
        textContent = findViewById(R.id.text_content);

        //button listener
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make object
                IntentIntegrator intentIntegrator = new IntentIntegrator(DoNotRemoveForNowAttendeeQRScanner.this);
                intentIntegrator.setPrompt("Scan a QR code");

                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
            }
        });

    }


    /**
     * Processes the result of the QR code scan, displaying the scanned content in a TextView.
     * If no content is found, it shows a toast message.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */

    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data){
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode,resultCode,data);

        if(intentResult != null){
            if(intentResult.getContents()== null){
                Toast.makeText(getBaseContext(), "Aborted",Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(getBaseContext(), "Successfully scanned", Toast.LENGTH_SHORT).show();
                textContent.setText(intentResult.getContents());

            }
        }
        else {
            //handle if null
            Toast.makeText(DoNotRemoveForNowAttendeeQRScanner.this, "Scan cancelled",Toast.LENGTH_SHORT).show();

        }


    }

}
