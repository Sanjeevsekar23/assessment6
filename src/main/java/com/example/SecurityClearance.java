package com.example.employeeaccess;

public enum SecurityClearance {
    NONE(0),
    BASIC(1),
    CONFIDENTIAL(2),
    SECRET(3),
    TOP_SECRET(4);

    private final int level;

    SecurityClearance(int level) {
        this.level = level;
    }

    public boolean meets(AccessLevel accessLevel) {
        return this.level >= accessLevel.ordinal();
    }
}