package com.example.antrp;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class Util {
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
}
