package com.example.antrp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ApkHashChecker implements IntegrityChecker {

    private static String TAG = "ApkHashChecker";

    private String mExpectedHash;
    private String mCalculatedHash;


    ApkHashChecker() {
        calculateApkHash();
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

    public String readFileToString(String filePath) throws IOException {
        int read = 0;
        byte[] buffer = new byte[8192];
        StringBuilder sb = new StringBuilder();
        FileInputStream fis = new FileInputStream(filePath);
        while ((read = fis.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, read));
        }

        return sb.toString();
    }
    public void loadExpectedHash() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "checksum.txt");
            mExpectedHash  = readFileToString(file.toString()).replaceAll("\\s+", "");
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to read file %s. Reason - %s", "checksum.txt", e.toString()));
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

    private void calculateApkHash() {
        String apkFilePath = MyApplication.context.getPackageCodePath();
        try {
            int read;
            byte[] buffer = new byte[8192];
            FileInputStream fis = new FileInputStream(apkFilePath);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            while ((read = fis.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            mCalculatedHash = toHexString(digest.digest());
            fis.close();

        } catch (FileNotFoundException e) {
            Log.e("ApkHashChecker", "Failed to get path to the [self] APK");
        } catch (IOException e) {
            Log.e("ApkHashChecker", "Failed to open the [self] APK");
        } catch (NoSuchAlgorithmException e) {
            Log.e("ApkHashChecker", "Failed to instantiate SHA-256");
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
