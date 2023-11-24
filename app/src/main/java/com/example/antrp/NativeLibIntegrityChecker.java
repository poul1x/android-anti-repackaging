package com.example.antrp;

import static com.example.antrp.Util.getCPUArch;
import static com.example.antrp.Util.loadHashFileArch;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class NativeLibIntegrityChecker extends ApkContentIntegrityChecker {

    private static final String TAG = NativeLibIntegrityChecker.class.getSimpleName();
    private static final String HASH_FILE = "lib_hash.json";
    private static final String CPU_ARCH = getCPUArch();
    private static final String LIB_FOLDER = "lib/" + CPU_ARCH;

    @Override
    public String name() {
        return TAG;
    }

    @Override
    protected String loadExpectedHash() {
        return loadHashFileArch(HASH_FILE, CPU_ARCH);
    }

    @Override
    public void registerHashingTask(ApkContentHashingThread contentHashing) throws NoSuchAlgorithmException {
        contentHashing.addHashingTask(TAG, filePath -> filePath.startsWith(LIB_FOLDER));
    }
}
