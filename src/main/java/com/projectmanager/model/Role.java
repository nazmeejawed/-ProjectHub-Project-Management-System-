package com.projectmanager.model;

public enum Role {
    ADMIN("Admin"),
    MANAGER("Manager"),
    PROJECT_MANAGER("Project Manager"),
    DEVELOPER("Developer"),
    DESIGNER("Designer"),
    QA("QA Engineer"),
    TESTER("Tester");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
