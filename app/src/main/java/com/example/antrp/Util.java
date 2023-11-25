package com.example.antrp;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class Util {

    private static final String TAG = Util.class.getSimpleName();

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static String loadAssetTxtFile(String filename) throws IOException {
        AssetManager assetManager = MyApplication.context.getAssets();
        try (InputStream inputStream = assetManager.open(filename)) {
            return FileReader.readToString(inputStream).trim();
        }
    }

    public static String loadHashFile(String assetFile) {
        try {
            Log.i(TAG, String.format("Reading hash file %s", assetFile));
            return loadAssetTxtFile(assetFile);
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file from assets. Reason - %s", e.toString()));
            return null;
        }
    }

    public static String loadHashFileExternal(String filePath) {
        try {
            return FileReader.readToString(filePath).trim();
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file. Reason - %s", e.toString()));
            return null;
        }
    }
}
