package edu.umkc.eradford.ctrl_f;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ethan on 3/30/15.
 */
public class AssetExtractor {

    Context context;

    AssetExtractor(Context context) {
        this.context = context;
    }

    public void extractAssets() {
        extractAssets(context.getAssets());
    }

    public void extractAssets(AssetManager assets) {
        extractAssets(assets, "");
    }

    public void extractAssets(AssetManager assets, String path) {
        try {
            String[] assetList = assets.list(path);
            if (assetList == null)
                return;
            for (String asset:assetList) {
                if (new File(asset).isDirectory())
                    extractAssets(assets,path+asset);
                else
                    extractAsset(assets,asset);
            }
        } catch (IOException ioe) {
            Log.d("ExtractAssets", ioe.getLocalizedMessage());
        }
    }

    private void extractAsset(AssetManager assets, String path) throws IOException {
        Log.v("ExtractAsset",path);
        StreamUtility.copyStream(assets.open(path),openOutputStream(path));
    }

    private FileOutputStream openOutputStream(String path) throws IOException{
        File outputFile = new File(context.getFilesDir(),path);
        outputFile.createNewFile();
        return new FileOutputStream(outputFile);
    }
}
