package edu.umkc.eradford.ctrl_f;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends ActionBarActivity {

    public static final int IMAGE_CAPTURE = 100;
    private Uri fileUri;
    private TessBaseAPI ocr = new TessBaseAPI();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(prefs.getBoolean("firstTime", true)) {

            try {

                Log.v("FirstTime","Extracting assets...");
                File outFile = new File(getFilesDir(), "tessdata/");
                Log.v("FirstTime","Creating Output Directory: "+outFile.getAbsolutePath());
                outFile.mkdirs();
                Log.v("FirstTime","Output Directory Created: "+outFile.getAbsolutePath());
                outFile = new File(outFile, "eng.traineddata");
                Log.v("FirstTime","Creating Output File: "+outFile.getAbsolutePath());
                outFile.createNewFile();

                Log.v("FirstTime","Output File Created: "+outFile.toString());

                copyStream(getAssets().open("eng.traineddata"),
                        new FileOutputStream(outFile));

            } catch (IOException ioe) {
                Log.d("FirstTime", ioe.getLocalizedMessage());
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
        }
        setContentView(R.layout.activity_main);

        fileUri = Uri.fromFile(new File(getApplicationContext().
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ,"document.jpg"));


        //File traineddata = new File(getFilesDir(),"tessdata/");
        Log.v("trainedDataPath",getFilesDir().getAbsolutePath());
        ocr.init(getFilesDir().getAbsolutePath(), "eng");
        //ocr.setImage(new File(fileUri.getPath()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_get_image:
                startActivityForResult(
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE).
                                putExtra(MediaStore.EXTRA_OUTPUT, fileUri).
                                putExtra("return-data", true),
                        IMAGE_CAPTURE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case IMAGE_CAPTURE:

                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                ((ImageView) findViewById(R.id.imageView)).
                        setImageBitmap(BitmapFactory.decodeFile
                                (fileUri.getPath(),options));
                                */
                Log.v("ImageCaptureResult","Setting Image");
                ocr.setImage(normalizeBitmapOrientation(new File(fileUri.getPath())));

                String result = ocr.getUTF8Text();
                Log.v("ImageCaptureResult","Result:\n"+result);
                //new AlertDialog.Builder(this).setMessage(result).create();
        }
    }

    private void copyStream(InputStream in, OutputStream out) {
        Log.v("CopyStream","Copying Stream...");
        try {

            int inInt;
            while ((inInt=in.read())!=-1) {
                out.write(inInt);

            }
            out.close();
            in.close();

        } catch (IOException ioe) {
            Log.d("MainActivity.CopyStream", ioe.getLocalizedMessage());
        }
        Log.v("CopyStream","Copy Finished...");
    }

    public Bitmap normalizeBitmapOrientation(File path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path.getAbsolutePath());

        } catch (IOException ioe) {
            Log.d("NormalizeBitmapOrientation",ioe.getLocalizedMessage());
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

        return rotateBitmap(path, rotate);
    }


    public Bitmap rotateBitmap(File path, int rotate) {
        Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
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
}
