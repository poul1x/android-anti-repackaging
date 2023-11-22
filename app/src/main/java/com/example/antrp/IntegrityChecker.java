package com.example.antrp;

public interface IntegrityChecker {
    public boolean passed();

    public IntegrityCheckResult result();

    public String name();

    public String expectedHash();

    public String calculatedHash();
}
