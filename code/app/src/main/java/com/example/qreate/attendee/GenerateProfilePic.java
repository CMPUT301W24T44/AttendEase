package com.example.qreate.attendee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * This class is for generating a profile picture based on users initials
 * References: OpenAI, 2024, ChatGPT, Code to create a circular bitmap image
 */

public class GenerateProfilePic {

    /**
     * Generate a bitmap with initials drawn in
     * @param initials
     * @return a bitmap with users initials
     */
    public static Bitmap generateProfilePicture(String initials){
        int width = 160;
        int height = 160;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //background circle
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#FCA311"));

        canvas.drawCircle(width/2f, height/2f, width / 2f, backgroundPaint);

        //Draw initials in the text
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(72);

        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // "/ 2f" will calculate center of bitmap
        Rect textBounds = new Rect();
        textPaint.getTextBounds(initials, 0, initials.length(), textBounds);


        float x = canvas.getWidth()/ 2f;
        float y = (canvas.getHeight()/ 2f) + (textBounds.height()/ 2f);
        canvas.drawText(initials, x, y, textPaint);

        return bitmap;
    }
}