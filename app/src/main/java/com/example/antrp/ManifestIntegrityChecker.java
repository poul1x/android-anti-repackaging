

package com.example.antrp;

import static com.example.antrp.Util.loadAssetTxtFile;
import static com.example.antrp.Util.loadHashFile;

import android.util.Log;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ManifestIntegrityChecker extends ApkContentIntegrityChecker {
    private static final String TAG = ManifestIntegrityChecker.class.getSimpleName();
    private static final String HASH_FILE = "manifest_hash.txt";
    private static final String MANIFEST_FILE = "AndroidManifest.xml";

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
        contentHashing.addHashingTask(TAG, filePath -> filePath.equals(MANIFEST_FILE));
    }
}
