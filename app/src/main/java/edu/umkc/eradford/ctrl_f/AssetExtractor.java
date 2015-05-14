package edu.umkc.eradford.ctrl_f;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.umkc.eradford.util.StreamUtility;

/**
 * Created by Ethan on 3/30/15.
 */
public class AssetExtractor {

    private AssetExtractor(){}

    public static void extractAssets(Context context) {
        extractAssets(context, context.getAssets());
    }

    public static void extractAssets(Context context, AssetManager assets) {
        extractAssets(context, assets, "");
    }

    public static void extractAssets(Context context, AssetManager assets, String path) {
        Log.v("AssetExtractor","Extracting assets at \""+path+"\"");
        String assetList[];
        try {
            assetList = assets.list(path);
            if (assetList.length==0) {
                extractAsset(context, assets, path);
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
                        extractAssets(context, assets, asset);
                    } else {
                        extractAssets(context, assets, path + "/" + asset);
                    }
                }
            }
        } catch (IOException e) {
            Log.v("AssetExtractor","I/O Error: "+e.getLocalizedMessage());
        }
    }

    private static void extractAsset(Context context, AssetManager assets, String path) throws IOException {
        Log.v("AssetExtractor","Copying asset at : "+path);
        StreamUtility.copyStream(assets.open(path), openOutputStream(context, path));
    }

    private static FileOutputStream openOutputStream(Context context, String path) throws IOException{
        File outputFile = new File(context.getFilesDir(),path);
        outputFile.createNewFile();
        Log.v("AssetExtractor","Opening stream: "+outputFile);
        return new FileOutputStream(outputFile);
    }
}
