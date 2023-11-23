package com.example.antrp;

import static com.example.antrp.Util.toHexString;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ApkIntegrityChecker implements IntegrityChecker {

    private static final String TAG = ApkIntegrityChecker.class.getSimpleName();

    private String mExpectedHash;
    private String mCalculatedHash;


    ApkIntegrityChecker() {
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

    public void loadExpectedHash() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "checksum.txt");
            Log.i(TAG, String.format("Reading hash file %s", file.toString()));
            mExpectedHash = FileReader.readToString(file.toString()).trim();
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read hash file. Reason - %s", e.toString()));
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
        Log.i(TAG, String.format("Calculating own APK hash"));
        String apkFilePath = MyApplication.context.getPackageCodePath();
        try {
            int bytes_read;
            byte[] buffer = new byte[4096];
            FileInputStream fis = new FileInputStream(apkFilePath);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            while ((bytes_read = fis.read(buffer)) > 0) {
                md.update(buffer, 0, bytes_read);
            }
            mCalculatedHash = toHexString(md.digest());
            fis.close();

        } catch (IOException e) {
            String reason = "Failed to read own APK file";
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", reason));
        } catch (NoSuchAlgorithmException e) {
            String reason = "Failed to instantiate SHA-256";
            Log.e(TAG, String.format("Hash calculation failed. Reason - %s", reason));
        }
    }

}
