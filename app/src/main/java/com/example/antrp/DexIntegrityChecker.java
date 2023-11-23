package com.example.antrp;

import static com.example.antrp.Util.loadAssetTxtFile;

import android.util.Log;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DexIntegrityChecker implements IntegrityChecker {

    private static final String EXT = ".dex";
    private static final String TAG = DexIntegrityChecker.class.getSimpleName();

    private static final String HASH_FILE = "dex_hash.txt";

    private String mExpectedHash;
    private String mCalculatedHash;

    DexIntegrityChecker() {
        calculateDexHash();
        loadExpectedHash();
    }

    @Override
    public String name() {
        return TAG;
    }

    @Override
    public boolean passed() {
        return (
                mCalculatedHash != null && mExpectedHash != null &&
                        mCalculatedHash.equals(mExpectedHash)
        );
    }

    private void loadExpectedHash() {
        try {
            Log.i(TAG, String.format("Reading hash file %s", HASH_FILE));
            mExpectedHash = loadAssetTxtFile(HASH_FILE);
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file from assets. Reason - %s", e.toString()));
        }
    }

    @Override
    public IntegrityCheckResult result() {
        return passed() ? IntegrityCheckResult.PASSED : IntegrityCheckResult.FAILED;
    }

    @Override
    public String expectedHash() {
        return mExpectedHash;
    }

    @Override
    public String calculatedHash() {
        return mCalculatedHash;
    }

    private void calculateDexHash() {
        try {
            Log.i(TAG, String.format("Calculating dex files hash", HASH_FILE));
            mCalculatedHash = ApkContentIntegrity.getHashFor(name -> name.endsWith(EXT));
        } catch (IOException e) {
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", e.toString()));
        } catch (NoSuchAlgorithmException e) {
            String reason = "Failed to instantiate SHA-256";
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", reason));
        }
    }
}
