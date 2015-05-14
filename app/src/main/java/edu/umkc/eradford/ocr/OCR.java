package edu.umkc.eradford.ocr;

import android.graphics.Bitmap;

import java.io.File;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;

/**
 * Created by Ethan on 5/13/15.
 */
public interface OCR {

    public void initialize();
    public void setProperties(Map<String, Object> properties) throws InvalidPropertiesFormatException;
    public void setProperty(String attributeName, Object value) throws InvalidPropertiesFormatException;
    public String parseImage(File source);
    public String parseImage(Bitmap image);

}
