package com.example.qreate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.graphics.Bitmap;

import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.junit.jupiter.api.Test;

public class OrganizerTests {
//    @Test
//    void testCreateEvent() throws WriterException {
//        String eventName = "Event";
//        String eventDetails = "Event Details";
//        OrganizerEvent event = new OrganizerEvent(eventName, eventDetails);
//
//        String receivedEventName = event.getEventName();
//        String receivedDetails = event.getDetail();
//
//        assertEquals(eventName, receivedEventName);
//        assertEquals(eventDetails, receivedDetails);
//    }

    //Test fails due to comparing bitmaps instead of decoding both and comparing strings problem is you need a context and
    // I can't seem to find a function to decode bitmaps instead of image files or through camera

    @Test
    void testGenerateQR() throws WriterException {
        String testQRString = "Test 123";
        OrganizerQRGeneratorActivity qrGeneratorTest = new OrganizerQRGeneratorActivity();
        Bitmap receivedQR = qrGeneratorTest.generateQR(testQRString);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(testQRString, BarcodeFormat.QR_CODE, 250,250);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap expectedBitMap = barcodeEncoder.createBitmap(bitMatrix);
        assertEquals(receivedQR, expectedBitMap);
    }
}
