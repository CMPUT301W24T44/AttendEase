package com.example.qreate.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

/**
 * AttendeeScanFragment provides a user interface for attendees to scan QR codes within the event app.
 *
 * It features a simple layout with a scan button that, when clicked,
 * initiates the QR code scanning process.
 * The fragment is designed to be embedded within the attendee section of the application.
 *
 * References: - ankur035, GeeksforGeeks, How to Read QR Code using Zxing Library in Android?,
 *                  Last Updated: 15 Jan,2021, https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
 *
 *             - Cambo Tutorial, YouTube, Uploaded Mar 18, 2022,
 *                  Implement Barcode QR Scanner in Android Studio Barcode Reader | Cambo Tutorial,
 *                  https://www.youtube.com/watch?v=jtT60yFPelI
 *
 * @author Shraddha Mehta
 */

public class AttendeeScanFragment extends Fragment implements View.OnClickListener{

    ImageButton scanButton;
    TextView textContent;
    private ActivityResultLauncher<Intent> scanLauncher;

    /**
     * This method inflates the layout for the attendee QR code scanning page, initializes UI components,
     * and sets up a click listener for the scan button.
     *
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's
     *                 UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed
     *                 from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_tap_to_scan_page, container, false);

        scanButton = view.findViewById(R.id.tap_to_scan_qr_button);
        textContent = view.findViewById(R.id.text_content);
        scanButton.setOnClickListener(this);

        //initialize the scan launcher
        scanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK){
                Intent data = result.getData();
                IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);

                if(intentResult != null){

                    if (intentResult.getContents() == null){
                        popUpAlert("Scan Aborted");

                    } else if (intentResult.getContents() != null){
                        popUpResultDialog(intentResult.getContents());
                    }
                }
            } else{
                popUpAlert("Scan Aborted");
            }
        });

        return view;
    }

    private void popUpResultDialog(String resultMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Result from Scan");
        builder.setMessage(resultMessage);
        builder.setPositiveButton("OK", (dialog, which) -> {
            //can remove or change later depending on implementation
            //textContent.setText(result);
            dialog.dismiss();
        });

        builder.show();
    }

    private void popUpAlert(String scanAborted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(scanAborted);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.tap_to_scan_qr_button){
            startQRScan();
        }
    }


    private void startQRScan(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setPrompt("SCAN A QR CODE");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(CapActivityForAttendeeQRScannerPage.class);

        Intent scanIntent = intentIntegrator.createScanIntent();
        scanLauncher.launch(scanIntent);
    }



}
