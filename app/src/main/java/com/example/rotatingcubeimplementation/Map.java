package com.example.rotatingcubeimplementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Map {
    int mHeight = 2000;
    int mWidth = 4000;
    String imgLoc = "../../res/drawable/map.jpg";
    Bitmap bitmap = null;
    Canvas Map;
    Paint white = null;
    Paint black = null;

    public Map() {
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Map = new Canvas(bitmap);

        white = new Paint();
        white.setStyle(Paint.Style.FILL);
        white.setColor(Color.WHITE);

        black = new Paint();
        black.setStyle(Paint.Style.FILL);
        black.setColor(Color.BLACK);

        Map.drawPaint(white);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imgLoc);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void drawPoint(float x, float y) {
        Map.drawCircle(x, y, 25, black);
    }


}
