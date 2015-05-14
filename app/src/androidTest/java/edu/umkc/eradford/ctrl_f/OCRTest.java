package edu.umkc.eradford.ctrl_f;

import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.util.InvalidPropertiesFormatException;

import edu.umkc.eradford.ocr.OCR;

/**
 * Created by Ethan on 5/13/15.
 */
public class OCRTest extends AndroidTestCase {

    public void testOCR() {
        OCR ocr = new TesseractOcr();
        String trainedData = getContext().getFilesDir().getAbsolutePath();
        Log.v("trainedDataPath", trainedData);
        try {
            ocr.setProperty("trainedData", trainedData);
        } catch (InvalidPropertiesFormatException e) {
            Log.e("setupOCR","Problem setting OCR properties",e);
        }
        ocr.initialize();
        File testBmp = new File(getContext().getFilesDir(),"Test.bmp");
        System.out.println(testBmp);
        String results = ocr.parseImage(testBmp);
        Log.v("parseResults",results);
        results = results.toLowerCase();
        assertEquals(true,results.contains("ctrl") && results.contains("ethan radford")
                && results.contains("test page"));
    }

}
