package com.example.antrp;

import static com.example.antrp.Util.toHexString;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ApkContentIntegrity {
    private static final String TAG = ApkContentIntegrity.class.getSimpleName();

    public interface FileNameFilter {
        boolean matches(String fileName);
    }

    public static String getHashFor(FileNameFilter filter) throws IOException, NoSuchAlgorithmException {
        String apkFilePath = MyApplication.context.getPackageCodePath();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[4096];

        try (
                FileInputStream fis = new FileInputStream(apkFilePath);
                ZipInputStream zipInputStream = new ZipInputStream(fis);
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    if (filter.matches(zipEntry.getName())) {
                        Log.d(TAG, String.format("File accepted: %s", zipEntry.getName()));
                        int bytes_read;
                        while ((bytes_read = zipInputStream.read(buffer)) > 0) {
                            md.update(buffer, 0, bytes_read);
                        }
                    }
                }
            }
        }
        return toHexString(md.digest());
    }

    public static String getHashFor(String filePath) throws IOException, NoSuchAlgorithmException {
        String apkFilePath = MyApplication.context.getPackageCodePath();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[4096];

        try (
                FileInputStream fis = new FileInputStream(apkFilePath);
                ZipInputStream zipInputStream = new ZipInputStream(fis);
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    if (zipEntry.getName() == filePath) {
                        Log.d(TAG, String.format("File accepted: %s", zipEntry.getName()));
                        int bytes_read;
                        while ((bytes_read = zipInputStream.read(buffer)) > 0) {
                            md.update(buffer, 0, bytes_read);
                        }
                        break;
                    }
                }
            }
        }
        return toHexString(md.digest());
    }
}
