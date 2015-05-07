package edu.umkc.eradford.ctrl_f;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by Ethan on 3/30/15.
 */
public class StreamUtility {


    static void copyStream (InputStream in, OutputStream out) throws IOException {
        int inInt;
        while ((inInt=in.read())!=-1) {
            out.write(inInt);
        }
        out.close();
        in.close();
    }

    private static CharSequence readData(InputStream in) {
        String data = "";
        Reader inReader = new InputStreamReader(in);
        try {
            data += inReader.read();
        } catch (IOException e) {
            Log.d("readData", e.getLocalizedMessage());
        }
        return data;
    }

    public static Dictionary<CharSequence,Collection<Integer>> findKeywords(
            InputStream source, Collection<String> keywords) {
        CharSequence data = readData(source);
        Dictionary<CharSequence,Collection<Integer>> locations = new Hashtable<>();
        for (CharSequence keyword:keywords) {
            Collection<Integer> keywordLocations = new ArrayList<>();
            for (int i=0;i<data.length()-keyword.length();i++) {
                if (data.subSequence(i,keyword.length()).equals(keyword)) {
                    keywordLocations.add(i);
                }
            }
            if (!keywordLocations.isEmpty()) {
                locations.put(keyword,keywordLocations);
            }
        }
        return locations;
    }

}
