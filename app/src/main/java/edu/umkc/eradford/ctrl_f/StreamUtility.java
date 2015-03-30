package edu.umkc.eradford.ctrl_f;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

}
