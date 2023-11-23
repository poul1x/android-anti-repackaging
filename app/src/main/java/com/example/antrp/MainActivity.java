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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int STORAGE_PERMISSION_CODE = 1;

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
        runIntegrityCheck(new ApkIntegrityChecker(), R.id.apkHashCheckResult);
    }

    private void runIntegrityCheck(IntegrityChecker checker, int resultViewId) {
        TextView view = findViewById(resultViewId);
        view.setText(checker.result().toString());

        Log.i(TAG, String.format("Running checker: %s", checker.name()));
        Log.i(TAG, String.format("expectedHash=%s", checker.expectedHash()));
        Log.i(TAG, String.format("calculatedHash=%s", checker.calculatedHash()));
        Log.i(TAG, String.format("result=%s", checker.result().toString()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkStoragePermission()) {
            runApkHashCheck();
        }

        int[] resultsViews = {
                R.id.dexFilesCheckResult,
                R.id.resourcesCheckResult,
                R.id.nativeLibsCheckResult,
                // TODO: Android manifest checker
                // TODO: Signature metadata checker
                // TODO: Assets folder checker
                // TODO: Native lib folder checker
                // TODO: Resources.arsc checker
        };

        List<IntegrityChecker> checkers = new ArrayList<>();
        checkers.add(new DexIntegrityChecker());
        //assert checkers.size() == resultsViews.length;

        int size = checkers.size();
        for (int i = 0; i < size; i++) {
            runIntegrityCheck(checkers.get(i), resultsViews[i]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (checkStoragePermission()) {
                runApkHashCheck();
            } else {
                Log.i(TAG, "MANAGE_EXTERNAL_STORAGE permission denied");
            }
        }
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
}