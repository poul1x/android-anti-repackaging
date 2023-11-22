package com.example.antrp;

public enum IntegrityCheckResult  {
    PASSED(R.string.passed),
    FAILED(R.string.failed);

    private int mResourceId;

    private IntegrityCheckResult(int id) {
        mResourceId = id;
    }

    @Override
    public String toString() {
        return MyApplication.context.getString(mResourceId);
    }
}
