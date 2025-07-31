package ru.diefrein.pricechecker.service.dto.enums;

public enum ProcessableSite {
    GOLD_APPLE("goldapple");

    private final String domainName;

    ProcessableSite(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
