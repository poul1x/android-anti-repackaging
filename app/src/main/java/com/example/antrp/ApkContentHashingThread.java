package com.example.antrp;

import static com.example.antrp.Util.toHexString;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ApkContentHashingThread extends Thread {
    private static final String TAG = ApkContentHashingThread.class.getSimpleName();
    private byte[] buffer = new byte[4096];

    private int mDelayMillis = 0;

    public interface FileGroupFilter {
        boolean matches(String filePath);
    }

    public interface HashingFinishedCallback {
        void callback();
    }

    private class FileGroupHashingTask {

        private String mName;
        private FileGroupFilter mFilter;
        private MessageDigest mDigest;

        public String getName() {
            return mName;
        }

        public FileGroupFilter getFileGroupFilter() {
            return mFilter;
        }

        public MessageDigest getMessageDigestObject() {
            return mDigest;
        }

        FileGroupHashingTask(String name, FileGroupFilter filter) throws NoSuchAlgorithmException {
            mDigest = MessageDigest.getInstance("SHA-256");
            mFilter = filter;
            mName = name;
        }
    }

    private ArrayList<FileGroupHashingTask> hashingTasks = new ArrayList<>();
    private ArrayList<HashingFinishedCallback> resultCallbacks = new ArrayList<>();

    ApkContentHashingThread(int delayMillis) {
        super();
        mDelayMillis = delayMillis;
    }

    public void addHashingTask(String name, FileGroupFilter filter) throws NoSuchAlgorithmException {
        hashingTasks.add(new FileGroupHashingTask(name, filter));
    }

    public String getHashingTaskResult(String taskName) {
        for (FileGroupHashingTask task : hashingTasks) {
            if (task.getName() == taskName) {
                return toHexString(task.getMessageDigestObject().digest());
            }
        }

        return null;
    }

    public void onComplete(HashingFinishedCallback callback) {
        resultCallbacks.add(callback);
    }

    private void updateHashesForTasks(ZipInputStream zipInputStream, ArrayList<FileGroupHashingTask> tasks) throws IOException {
        int bytes_read;
        while ((bytes_read = zipInputStream.read(buffer)) > 0) {
            for (FileGroupHashingTask task : tasks) {
                MessageDigest md = task.getMessageDigestObject();
                md.update(buffer, 0, bytes_read);
            }
        }
    }

    @Override
    public void run() {

        if (mDelayMillis > 0) {
            try {
                sleep(mDelayMillis);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted!");
                return;
            }
        }

        String apkFilePath = MyApplication.context.getPackageCodePath();
        try (
                FileInputStream fis = new FileInputStream(apkFilePath);
                ZipInputStream zipInputStream = new ZipInputStream(fis);
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    ArrayList tasksMatching = new ArrayList<>();
                    for (FileGroupHashingTask task : hashingTasks) {
                        FileGroupFilter filter = task.getFileGroupFilter();
                        if (filter.matches(zipEntry.getName())) {
                            Log.d(TAG, String.format("File %s accepted for task %s", zipEntry.getName(), task.getName()));
                            tasksMatching.add(task);
                        }
                    }

                    updateHashesForTasks(zipInputStream, tasksMatching);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (HashingFinishedCallback callback : resultCallbacks) {
            callback.callback();
        }
    }
}
