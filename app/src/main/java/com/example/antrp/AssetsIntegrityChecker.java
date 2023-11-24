package com.example.antrp;

import static com.example.antrp.Util.loadAssetTxtFile;
import static com.example.antrp.Util.loadHashFile;

import android.util.Log;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class AssetsIntegrityChecker extends ApkContentIntegrityChecker {

    private static final String TAG = AssetsIntegrityChecker.class.getSimpleName();
    private static final String HASH_FILE = "assets_hash.txt";
    private static final String ASSETS_FOLDER = "assets/";

    @Override
    public String name() {
        return TAG;
    }

    @Override
    protected String loadExpectedHash() {
        return loadHashFile(HASH_FILE);
    }

    @Override
    public void registerHashingTask(ApkContentHashingThread contentHashing) throws NoSuchAlgorithmException {
        contentHashing.addHashingTask(TAG, filePath -> {
            return filePath.startsWith(ASSETS_FOLDER) && !filePath.endsWith(HASH_FILE);
        });
    }
}
