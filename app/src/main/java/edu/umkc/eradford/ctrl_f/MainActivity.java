package edu.umkc.eradford.ctrl_f;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    public static final int IMAGE_CAPTURE = 100;
    private TessBaseAPI ocr = new TessBaseAPI();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isFirstRun()) {
            initialize();
        }
        trainOCR();
    }
    
    private boolean isFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("firstTime", true);
    }

    private void initialize() {

        try {
            new File(getApplicationContext().
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), ".nomedia").createNewFile();
        } catch (IOException e) {
            Log.d("Initialize", e.getLocalizedMessage());
        }


        new AssetExtractor(getApplicationContext()).extractAssets();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();
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
                takePicture();
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
                Log.v("ImageCaptureResult","Setting Image");
                ocr.clear();
                ocr.setImage(BitmapUtility.normalizeBitmapOrientation(new File(data.getDataString())));
                String result = ocr.getUTF8Text();
                Log.v("ImageCaptureResult","Result:\n"+result);
        }
    }

    private void takePicture() {
        Uri fileUri = Uri.fromFile(new File(getApplicationContext().
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ,"document.jpg"));
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).
                        putExtra(MediaStore.EXTRA_OUTPUT, fileUri).
                        putExtra("return-data", true), IMAGE_CAPTURE);
    }

    private void trainOCR() {
        String trainedData = getFilesDir().getAbsolutePath();
        Log.v("trainedDataPath",trainedData);
        ocr.init(trainedData, "eng");
    }

    
}
