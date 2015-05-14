package edu.umkc.eradford.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ethan on 3/30/15.
 */
public class BitmapUtility {

    public static Bitmap normalizeBitmapOrientation(File path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path.getAbsolutePath());


        } catch (IOException ioe) {
            Log.d("NormalizeBitmapOrientat", ioe.getLocalizedMessage());
        }

        int rotate = 0;
        switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate=90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate=180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate=270;
        }

        return rotateBitmap(BitmapFactory.decodeFile(path.getAbsolutePath()), rotate);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {

        if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


        return bitmap;
    }

    public static Bitmap loadBitmapFromFile(File source) {
        return BitmapFactory.decodeFile(source.getAbsolutePath());
    }
}