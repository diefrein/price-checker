package ru.diefrein.pricechecker.service.dto.enums;

public enum ProcessableSite {
    GOLD_APPLE("goldapple"),
    STORE77("store77");

    private final String domainName;

    ProcessableSite(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
