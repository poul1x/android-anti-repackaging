package com.example.antrp;

import static com.example.antrp.Util.loadAssetTxtFile;

import android.util.Log;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class AssetsIntegrityChecker implements IntegrityChecker {

    private static final String ASSETS_FOLDER = "assets/";
    private static final String TAG = AssetsIntegrityChecker.class.getSimpleName();

    private static final String HASH_FILE = "assets_hash.txt";

    private String mExpectedHash;
    private String mCalculatedHash;

    AssetsIntegrityChecker() {
        loadExpectedHash();
        calculateHash();
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

    private void calculateHash() {
        try {
            Log.i(TAG, String.format("Calculating assets files hash", HASH_FILE));
            mCalculatedHash = ApkContentIntegrity.getHashFor(name -> {
                return name.startsWith(ASSETS_FOLDER) && !name.endsWith(HASH_FILE);
            });
        } catch (IOException e) {
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", e.toString()));
        } catch (NoSuchAlgorithmException e) {
            String reason = "Failed to instantiate SHA-256";
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", reason));
        }
    }
}
