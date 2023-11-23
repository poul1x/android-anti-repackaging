package com.example.antrp;

import static com.example.antrp.Util.loadAssetTxtFile;

import java.io.StringReader;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class NativeLibIntegrityChecker implements IntegrityChecker {

    private static final String LIB_FOLDER = "lib/";
    private static final String TAG = NativeLibIntegrityChecker.class.getSimpleName();

    private static final String HASH_FILE = "lib_hash.json";

    private String mExpectedHash;
    private String mCalculatedHash;

    NativeLibIntegrityChecker() {
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

    public static String getCPUArch() throws RuntimeException {
        String[] supportedABIs = Build.SUPPORTED_ABIS;
        if (supportedABIs == null || supportedABIs.length == 0) {
            throw new RuntimeException("Empty Build.SUPPORTED_ABIS");
        }

        return supportedABIs[0];
    }

    private void loadExpectedHash() {
        try {
            Log.i(TAG, String.format("Reading hash file %s", HASH_FILE));
            JSONObject jsonObject = new JSONObject(loadAssetTxtFile(HASH_FILE));
            mExpectedHash = jsonObject.getString(getCPUArch());

        } catch (JSONException e) {
            Log.e(TAG, String.format("Failed to load hash file. Reason - %s", e.toString()));
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file from assets. Reason - %s", e.toString()));
        } catch (RuntimeException e) {
            Log.e(TAG, String.format("RuntimeException: %s", e.toString()));
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
            Log.i(TAG, String.format("Calculating lib files hash", HASH_FILE));
            mCalculatedHash = ApkContentIntegrity.getHashFor(name -> {
                return name.startsWith(LIB_FOLDER + getCPUArch());
            });
        } catch (IOException e) {
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", e.toString()));
        } catch (NoSuchAlgorithmException e) {
            String reason = "Failed to instantiate SHA-256";
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", reason));
        }
    }
}
