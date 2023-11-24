package com.example.antrp;

public abstract class IntegrityChecker {

    public abstract String name();

    public abstract boolean passed();

    public IntegrityCheckResult result() {
        return passed() ? IntegrityCheckResult.PASSED : IntegrityCheckResult.FAILED;
    }
}
