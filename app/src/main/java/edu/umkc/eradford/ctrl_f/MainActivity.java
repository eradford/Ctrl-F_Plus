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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.googlecode.tesseract.android.TessBaseAPI;

import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final int IMAGE_CAPTURE = 100, GET_FILE = 200;
    private TessBaseAPI ocr;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listItems = new ArrayList<>();
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Are we properly prepared to run?
        if (isFirstRun()) {
            startActivity(new Intent(this, FirstRun.class));
        }

        setupGUI();
        unwrap(savedInstanceState);

        fileUri = Uri.fromFile(new File(getApplicationContext().
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ,"document.jpg"));
        ocr = new TessBaseAPI();
        trainOCR();

    }

    private void unwrap(Bundle savedInstanceState) {
        if (savedInstanceState==null) {
            savedInstanceState = new Bundle();
        }
        listItems = savedInstanceState.getStringArrayList("listItems");
        if(listItems==null) {
            listItems = new ArrayList<>();
        }

    }

    private boolean isFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("firstTime", true);
    }

    private void setupGUI() {
        ListView listView = (ListView)this.findViewById(R.id.keywordList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listItems.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Assert.assertNotNull(v);
                // Get string entered
                EditText et = (EditText) findViewById(R.id.txtKeyword);
                // Add string to underlying data structure
                listItems.add(et.getText().toString());
                // Notify adapter that underlying data structure changed
                adapter.notifyDataSetChanged();
                et.setText("");
            }
        });

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
            case R.id.action_search_file:
                getFile();
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
                ocr.setImage(BitmapUtility.normalizeBitmapOrientation(new File(fileUri.getPath())));
                String result = ocr.getUTF8Text();
                Log.v("ImageCaptureResult","Result:\n"+result);
                for (String keyword:listItems) {
                    if (result.contains(keyword)) {
                        listItems.set(listItems.indexOf(keyword),"*****" + keyword);
                    }
                }
                break;
            case GET_FILE:
                File resultFile = new File(data.toUri(0));
                Log.v("GetFileResult",resultFile.toString());
                try {
                    StreamUtility.findKeywords(new FileInputStream(resultFile), listItems);
                } catch (FileNotFoundException e) {
                    Log.e("GetFileResult","File not found: ", e);
                }
                break;
        }
    }

    private void takePicture() {

        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).
                        putExtra(MediaStore.EXTRA_OUTPUT, fileUri).
                        putExtra("return-data", true), IMAGE_CAPTURE);
    }

    private void getFile() {
        startActivityForResult(
                new Intent(Intent.ACTION_GET_CONTENT).setType("file/*"),
                GET_FILE);
    }

    private void trainOCR() {
        String trainedData = getFilesDir().getAbsolutePath();
        Log.v("trainedDataPath",trainedData);
        ocr.init(trainedData, "eng");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("listItems", listItems);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        unwrap(savedInstanceState);
    }
}
