package edu.umkc.eradford.ctrl_f;

import android.test.AndroidTestCase;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by Ethan on 5/7/15.
 */
public class OCRTest extends AndroidTestCase {

    public void testOCR() {
        TessBaseAPI ocr = new TessBaseAPI();
        ocr.setImage(new File("Test.bmp"));
        System.out.print(ocr.getUTF8Text());
    }

}
