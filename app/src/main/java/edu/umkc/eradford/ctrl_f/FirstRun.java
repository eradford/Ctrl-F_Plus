package edu.umkc.eradford.ctrl_f;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


public class FirstRun extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);


    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initialize();
                updateText();
            }
        }).start();
    }

    private void initialize() {

        try {
            new File(getApplicationContext().
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), ".nomedia").createNewFile();
        } catch (IOException e) {
            Log.d("Initialize", e.getLocalizedMessage());
        }


        AssetExtractor.extractAssets(getApplicationContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((TextView)findViewById(R.id.extractProgress)).setText("Extraction finished!");

            }
        });
    }
}
