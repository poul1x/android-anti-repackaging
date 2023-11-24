package com.example.antrp;

import java.security.NoSuchAlgorithmException;

public abstract class IntegrityChecker {

    public abstract String name();

    public abstract boolean passed();

    public IntegrityCheckResult result() {
        return passed() ? IntegrityCheckResult.PASSED : IntegrityCheckResult.FAILED;
    }
}
