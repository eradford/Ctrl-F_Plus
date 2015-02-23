package edu.umkc.eradford.ctrl_f;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    public static final int IMAGE_CAPTURE = 100;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileUri = Uri.fromFile(new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"document.jpg"));

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
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(BitmapFactory.decodeFile(fileUri.getPath(),options));
        }
    }

}
