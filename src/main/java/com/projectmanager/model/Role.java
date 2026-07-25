package com.projectmanager.model;

public enum Role {
    ADMIN("Admin"),
    MANAGER("Manager"),
    DEVELOPER("Developer"),
    DESIGNER("Designer"),
    QA("QA Engineer");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
