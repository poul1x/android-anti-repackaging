package com.example.antrp;

import static com.example.antrp.Util.loadHashFile;

import java.security.NoSuchAlgorithmException;

public class ResIntegrityChecker extends ApkContentIntegrityChecker {
    private static final String TAG = ResIntegrityChecker.class.getSimpleName();
    private static final String HASH_FILE = "res_hash.txt";
    private static final String RES_FILE = "resources.arsc";


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
        contentHashing.addHashingTask(TAG, filePath -> filePath.equals(RES_FILE));
    }
}
