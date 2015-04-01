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

    private Context context;

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
        Log.v("AssetExtractor","Extracting assets at \""+path+"\"");
        String assetList[];
        try {
            assetList = assets.list(path);
            if (assetList == null || assetList.length==0) {
                extractAsset(assets, path);
            } else {
                for (String asset:assetList) {
                    switch (asset) {
                        case "images":
                        case "sounds":
                        case "webkit":
                        case "webkitsec":
                            continue;
                    }
                    (new File(context.getFilesDir(),path)).mkdir();
                    Log.v("AssetExtractor","Encountered asset: "+path+"/"+asset);
                    if (path.equals("")) {
                        extractAssets(assets, asset);
                    } else {
                        extractAssets(assets, path + "/" + asset);
                    }
                }
            }
        } catch (IOException e) {
            Log.v("AssetExtractor","I/O Error: "+e.getLocalizedMessage());
        }
    }

    private void extractAsset(AssetManager assets, String path) throws IOException {
        Log.v("AssetExtractor","Copying asset at : "+path);
        StreamUtility.copyStream(assets.open(path), openOutputStream(path));
    }

    private FileOutputStream openOutputStream(String path) throws IOException{
        File outputFile = new File(context.getFilesDir(),path);
        outputFile.createNewFile();
        Log.v("AssetExtractor","Opening stream: "+outputFile);
        return new FileOutputStream(outputFile);
    }
}
