package com.example.antrp;

import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static String getCPUArch() throws RuntimeException {
        String[] supportedABIs = Build.SUPPORTED_ABIS;
        if (supportedABIs == null || supportedABIs.length == 0) {
            throw new RuntimeException("Empty Build.SUPPORTED_ABIS");
        }

        return supportedABIs[0];
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
            return FileReader.readToString(filePath);
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file. Reason - %s", e.toString()));
            return null;
        }
    }

    public static String loadHashFileArch(String assetFile, String arch) {
        String expectedHash = null;
        try {
            Log.i(TAG, String.format("Reading hash file from assets: %s", assetFile));
            JSONObject jsonObject = new JSONObject(loadAssetTxtFile(assetFile));
            expectedHash = jsonObject.getString(arch);

        } catch (JSONException e) {
            Log.e(TAG, String.format("Failed to load hash file. Reason - %s", e.toString()));
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file from assets. Reason - %s", e.toString()));
        }
        return expectedHash;
    }

    public static String loadHashFileArchExternal(String filePath, String arch) {
        String expectedHash = null;
        try {
            Log.i(TAG, String.format("Reading hash file from sdcard: %s", filePath));
            JSONObject jsonObject = new JSONObject(FileReader.readToString(filePath));
            expectedHash = jsonObject.getString(arch);

        } catch (JSONException e) {
            Log.e(TAG, String.format("Failed to load hash file. Reason - %s", e.toString()));
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file from sdcard. Reason - %s", e.toString()));
        }
        return expectedHash;
    }

}
