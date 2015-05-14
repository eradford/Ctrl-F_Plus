package edu.umkc.eradford.ctrl_f;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;

import edu.umkc.eradford.ocr.OCR;
import edu.umkc.eradford.util.BitmapUtility;

/**
 * Created by Ethan on 5/13/15.
 */
public class TesseractOcr implements OCR {

    TessBaseAPI ocr;

    public TesseractOcr() {
        ocr = new TessBaseAPI();
    }

    @Override
    public void initialize() {
        //Default settings are intialized here
        try {
            setProperty("PageSegMode", TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        } catch(InvalidPropertiesFormatException e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setProperties(Map<String, Object> properties) throws InvalidPropertiesFormatException {
        for (String key:properties.keySet()) {
            setProperty(key, properties.get(key));
        }
    }

    @Override
    public void setProperty(String key, Object value) throws InvalidPropertiesFormatException {
        Class expectedType = null;
        try {
            switch (key) {
                case "PageSegMode":
                    expectedType = Integer.class;
                    ocr.setPageSegMode((Integer) value);
                    break;
                case "trainedData":
                    expectedType = String[].class;
                    ocr.init((String) value, "eng");
                    break;
                //TODO Code rest of properties for Tesseract
                default:
                    throw new InvalidPropertiesFormatException("Unrecognized property '" + key + "'.");
            }
        } catch (ClassCastException e) {
            //If this exception throws null, then expectedType is not being
            // set by its corresponding case
            throw new InvalidPropertiesFormatException("Key '"+key+"' expects a value of type '"
                    +expectedType+"'. Instead, a(n) "+ value.getClass() + "' was found.");
        }
    }

    @Override
    public String parseImage(File source) {
        return parseImage(BitmapUtility.normalizeBitmapOrientation(source));
    }

    @Override
    public String parseImage(Bitmap image) {
        ocr.clear();
        ocr.setImage(image);
        return ocr.getUTF8Text();
    }
}

