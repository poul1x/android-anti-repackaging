package com.example.antrp;

import static com.example.antrp.Util.loadAssetTxtFile;
import static com.example.antrp.Util.loadHashFile;

import android.util.Log;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DexIntegrityChecker extends ApkContentIntegrityChecker {

    private static final String TAG = DexIntegrityChecker.class.getSimpleName();
    private static final String HASH_FILE = "dex_hash.txt";
    private static final String EXT = ".dex";

    @Override
    protected String loadExpectedHash() {
        return loadHashFile(HASH_FILE);
    }

    @Override
    public void registerHashingTask(ApkContentHashingThread contentHashing) throws NoSuchAlgorithmException {
        contentHashing.addHashingTask(TAG, filePath -> filePath.endsWith(EXT));
    }

    @Override
    public String name() {
        return TAG;
    }
}
