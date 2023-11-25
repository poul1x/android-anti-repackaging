package com.example.antrp;

import static com.example.antrp.Util.loadHashFile;

import java.security.NoSuchAlgorithmException;

public class NativeLibIntegrityChecker extends ApkContentIntegrityChecker {

    private static final String TAG = NativeLibIntegrityChecker.class.getSimpleName();
    private static final String HASH_FILE = "lib_hash.txt";
    private static final String LIB_FOLDER = "lib/";

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
        contentHashing.addHashingTask(TAG, filePath -> filePath.startsWith(LIB_FOLDER));
    }
}
