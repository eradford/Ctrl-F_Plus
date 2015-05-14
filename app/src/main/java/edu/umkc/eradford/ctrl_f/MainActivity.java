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
import android.widget.EditText;
import android.widget.ListView;

import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

import edu.umkc.eradford.ocr.OCR;
import edu.umkc.eradford.util.StreamUtility;


public class MainActivity extends ActionBarActivity {

    public static final int IMAGE_CAPTURE = 100, GET_FILE = 200;
    private OCR ocr;
    private ArrayItemAdapter<String> adapter;
    private ArrayList<String> listItems = new ArrayList<>();
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private String result;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Are we properly prepared to run?
        if (isFirstRun()) {
            startActivity(new Intent(this, FirstRun.class));
        }
        unwrap(savedInstanceState);
        setupGUI();
        fileUri = Uri.fromFile(new File(getApplicationContext().
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , "document.jpg"));
        ocr = new TesseractOcr();
        setupOCR();
    }

    private void unwrap(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        if (savedInstanceState.containsKey("listItems")) {
            listItems = savedInstanceState.getStringArrayList("listItems");
        }
        if (savedInstanceState.containsKey("selectedItems")) {
            selectedItems = savedInstanceState.getIntegerArrayList("selectedItems");
        }

    }

    private boolean isFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("firstTime", true);
    }

    private void setupGUI() {
        ListView listView = (ListView) this.findViewById(R.id.keywordList);
        adapter = new ArrayItemAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listItems.remove(position);
                        adapter.setSelectedItems(selectedItems);
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
        adapter.setSelectedItems(selectedItems);
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
            case R.id.action_view_last_results:
                Intent last_results = new Intent(this, ResultActivity.class);
                last_results.putExtra("result", result);
                startActivity(last_results);
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case IMAGE_CAPTURE:
                Log.v("ImageCaptureResult", "Setting Image");
                adapter.clearSelections();
                Log.v("OCR Recognition", "Searching image for text");
                result = ocr.parseImage(new File(fileUri.getPath()));
                Log.v("ImageCaptureResult", "Result:\n" + result);
                result = result.toLowerCase();
                for (String keyword : listItems) {
                    if (result.contains(keyword.toLowerCase())) {
                        adapter.selectItem(listItems.indexOf(keyword));
                    }
                }
                break;
            case GET_FILE:
                File resultFile = new File(data.toUri(0));
                Log.v("GetFileResult", resultFile.toString());
                try {
                    StreamUtility.findKeywords(new FileInputStream(resultFile), listItems);
                } catch (FileNotFoundException e) {
                    Log.e("GetFileResult", "File not found: ", e);
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("listItems", listItems);
        outState.putIntegerArrayList("selectedItems", adapter.getSelectedItems());
    }

    private void setupOCR() {
        String trainedData = getFilesDir().getAbsolutePath();
        Log.v("trainedDataPath", trainedData);
        try {
            ocr.setProperty("trainedData", trainedData);
        } catch (InvalidPropertiesFormatException e) {
            Log.e("setupOCR","Problem setting OCR properties",e);
        }
    }

}