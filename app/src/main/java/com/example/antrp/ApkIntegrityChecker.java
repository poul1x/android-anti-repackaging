package com.example.antrp;

import static com.example.antrp.Util.loadHashFileExternal;
import static com.example.antrp.Util.toHexString;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ApkIntegrityChecker extends IntegrityChecker {
    private static final String TAG = ApkIntegrityChecker.class.getSimpleName();
    private static final String DOCUMENTS_DIR = documentsDir();
    private static final String HASH_FILE = "apk_hash.txt";
    private String mExpectedHash;
    private String mCalculatedHash;

    private static String documentsDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
    }

    ApkIntegrityChecker() {
        loadExpectedHash();
        calculateHash();
    }

    @Override
    public String name() {
        return TAG;
    }

    public boolean passed() {
        return (
                mCalculatedHash != null && mExpectedHash != null &&
                        mCalculatedHash.equals(mExpectedHash)
        );
    }


    public String getExpectedHash() {
        return mExpectedHash;
    }

    public String getCalculatedHash() {
        return mCalculatedHash;
    }

    public void loadExpectedHash() {
        File file = new File(DOCUMENTS_DIR, HASH_FILE);
        mExpectedHash = loadHashFileExternal(file.toString());
    }

    private void calculateHash() {
        Log.i(TAG, String.format("Calculating own APK hash"));
        String apkFilePath = MyApplication.context.getPackageCodePath();
        try (
                FileInputStream fis = new FileInputStream(apkFilePath);
        ) {
            int bytes_read;
            byte[] buffer = new byte[4096];
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            while ((bytes_read = fis.read(buffer)) > 0) {
                md.update(buffer, 0, bytes_read);
            }
            mCalculatedHash = toHexString(md.digest());

        } catch (IOException e) {
            String reason = String.format("Failed to read own APK file: %s", e.toString());
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", reason));
        } catch (NoSuchAlgorithmException e) {
            String reason = "Failed to instantiate SHA-256";
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", reason));
        }
    }
}
