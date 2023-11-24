package com.example.antrp;

import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Browser;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int STORAGE_PERMISSION_CODE = 1;

    private ApkContentHashingThread apkContentHashing = new ApkContentHashingThread(1500);

    static {
        System.loadLibrary("antrp");
    }

    private boolean checkStoragePermission() {
        if (SDK_INT >= 30) {
            Log.i(TAG, "Checking MANAGE_EXTERNAL_STORAGE permission for SDK_INT >= 30");
            if (Environment.isExternalStorageManager()) {
                Log.i(TAG, "MANAGE_EXTERNAL_STORAGE permission granted");
                return true;
            } else {
                Log.i(TAG, "MANAGE_EXTERNAL_STORAGE permission not granted");
                Log.i(TAG, "Show snack bar to request MANAGE_EXTERNAL_STORAGE permission");
                Snackbar.make(findViewById(android.R.id.content), "Storage permission needed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Uri uri = Uri.parse("package:" + getApplicationContext().getPackageName());
                                    Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                                    startActivityForResult(intent, STORAGE_PERMISSION_CODE);
                                } catch (Exception ex) {
                                    Intent intent = new Intent();
                                    intent.setAction(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    startActivityForResult(intent, STORAGE_PERMISSION_CODE);
                                }
                            }
                        })
                        .show();
                return false;
            }
        } else {
            Log.i(TAG, "Checking READ_EXTERNAL_STORAGE permission for SDK_INT < 30");
            int requestCode = STORAGE_PERMISSION_CODE;
            String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "READ_EXTERNAL_STORAGE granted");
                return true;
            } else {
                Log.i(TAG, "READ_EXTERNAL_STORAGE not granted");
                Log.i(TAG, "Show snack bar to request READ_EXTERNAL_STORAGE permission");
                Snackbar.make(findViewById(android.R.id.content), "Storage permission needed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Grant", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
                            }
                        })
                        .show();
                return false;
            }
        }
    }

    private void runApkHashCheck() {
        TextView textView = findViewById(R.id.apkHashCheckResult);
        ProgressBar progressBar = findViewById(R.id.apkHashCheckProgressBar);

        textView.setText(R.string.running);
        progressBar.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> {
            ApkIntegrityChecker checker = new ApkIntegrityChecker();
            runOnUiThread(() -> {
                textView.setText(checker.result().toString());
                progressBar.setVisibility(View.GONE);

                Log.i(TAG, String.format("Checker: %s", checker.name()));
                Log.i(TAG, String.format("expectedHash=%s", checker.getExpectedHash()));
                Log.i(TAG, String.format("calculatedHash=%s", checker.getCalculatedHash()));
                Log.i(TAG, String.format("result=%s", checker.result().toString()));
            });
        };

        handler.postDelayed(runnable, 1500);
    }


    private void scheduleIntegrityCheck(ApkContentIntegrityChecker checker, TextView textView, ProgressBar progressBar) throws NoSuchAlgorithmException {
        textView.setText(R.string.running);
        progressBar.setVisibility(View.VISIBLE);

        checker.registerHashingTask(apkContentHashing);
        apkContentHashing.onComplete(() -> {
            runOnUiThread(() -> {
                checker.loadHashingTaskResult(apkContentHashing);
                textView.setText(checker.result().toString());
                progressBar.setVisibility(View.GONE);

                Log.i(TAG, String.format("Checker: %s", checker.name()));
                Log.i(TAG, String.format("expectedHash=%s", checker.getExpectedHash()));
                Log.i(TAG, String.format("calculatedHash=%s", checker.getCalculatedHash()));
                Log.i(TAG, String.format("result=%s", checker.result().toString()));
            });
        });
    }

    private void runIntegrityChecks() {

        // TODO: Signature metadata checker
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(findViewById(R.id.dexFilesCheckResult));
        textViews.add(findViewById(R.id.resourcesCheckResult));
        textViews.add(findViewById(R.id.manifestCheckResult));
        textViews.add(findViewById(R.id.assetsCheckResult));
        textViews.add(findViewById(R.id.nativeLibsCheckResult));

        ArrayList<ProgressBar> progressBars = new ArrayList<>();
        progressBars.add(findViewById(R.id.dexFilesCheckProgressBar));
        progressBars.add(findViewById(R.id.resourcesCheckProgressBar));
        progressBars.add(findViewById(R.id.manifestCheckProgressBar));
        progressBars.add(findViewById(R.id.assetsCheckProgressBar));
        progressBars.add(findViewById(R.id.nativeLibsCheckProgressBar));

        List<ApkContentIntegrityChecker> checkers = new ArrayList<>();
        checkers.add(new DexIntegrityChecker());
        checkers.add(new ResIntegrityChecker());
        checkers.add(new ManifestIntegrityChecker());
        checkers.add(new AssetsIntegrityChecker());
        checkers.add(new NativeLibIntegrityChecker());

        assert textViews.size() == progressBars.size();
        assert checkers.size() == textViews.size();

        try {
            int size = checkers.size();
            for (int i = 0; i < size; i++) {
                scheduleIntegrityCheck(checkers.get(i),
                        textViews.get(i), progressBars.get(i)
                );
            }
            apkContentHashing.start();

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to run integrity checks. Reason - %s", e.toString()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ensure JNI Hello world works
        Log.d(TAG, "JNI: " + stringFromJNI());

        if (checkStoragePermission()) {
            runApkHashCheck();
        }

        runIntegrityChecks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (checkStoragePermission()) {
                runApkHashCheck();
            } else {
                Log.i(TAG, "READ_EXTERNAL_STORAGE permission denied");
            }
        }
    }

    public native String stringFromJNI();
}