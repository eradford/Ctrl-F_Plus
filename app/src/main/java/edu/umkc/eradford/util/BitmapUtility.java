package edu.umkc.eradford.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for performing operations on Bitmap Objects and Bitmap Files.
 * Created by Ethan on 3/30/15.
 */
public class BitmapUtility {

    /**
     * Normalizes the orientation of a Bitmap using EXIF metadata
     * @param path A File representing the location of a Bitmap
     * @return A Bitmap which has had its orientation normalized
     */
    public static Bitmap normalizeBitmapOrientation(File path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path.getAbsolutePath());
        } catch (IOException ioe) {
            Log.d("NormalizeBitmapOrientat", ioe.getLocalizedMessage());
        }

        int rotate = getNormalizationAngle(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL));

        return rotateBitmap(BitmapFactory.decodeFile(path.getAbsolutePath()), rotate);
    }

    /**
     * Converts an orientation angle to the angle needed to normalize the image.
     * Only works for multiples of 90 degrees.
     * @param orientation The orientation to be converted
     * @return The rotation needed to normalize the image
     */
    public static int getNormalizationAngle(Integer orientation) {
        int rotate = 0;
        if (orientation != null) {
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
            }
        }
        return rotate;
    }

    /**
     * Rotates a bitmap by the specified angle.
     * @param bitmap The Bitmap to transform
     * @param angle The angle to use for rotation
     * @return The rotated Bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        if (angle != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(angle);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return bitmap;
    }

    /**
     * Loads a Bitmap from a File.
     * @param source The File to load from
     * @return A Bitmap manufactured from <code>source</code>
     */
    public static Bitmap loadBitmapFromFile(File source) {
        return BitmapFactory.decodeFile(source.getAbsolutePath());
    }
}