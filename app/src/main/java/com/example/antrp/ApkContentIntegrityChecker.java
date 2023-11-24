package com.example.antrp;

import java.security.NoSuchAlgorithmException;

abstract class ApkContentIntegrityChecker extends IntegrityChecker {

    private String mExpectedHash;
    private String mCalculatedHash;

    ApkContentIntegrityChecker() {
        mExpectedHash = loadExpectedHash();
        mCalculatedHash = null;
    }

    protected abstract String loadExpectedHash();

    public abstract void registerHashingTask(ApkContentHashingThread contentHashing) throws NoSuchAlgorithmException;

    public abstract String name();

    public boolean passed() {
        return (
                mCalculatedHash != null && mExpectedHash != null &&
                        mCalculatedHash.equals(mExpectedHash)
        );
    }

    public IntegrityCheckResult result() {
        return passed() ? IntegrityCheckResult.PASSED : IntegrityCheckResult.FAILED;
    }

    public void loadHashingTaskResult(ApkContentHashingThread contentHashing) {
        mCalculatedHash = contentHashing.getHashingTaskResult(name());
    }


    public String getExpectedHash() {
        return mExpectedHash;
    }

    public String getCalculatedHash() {
        return mCalculatedHash;
    }
}
