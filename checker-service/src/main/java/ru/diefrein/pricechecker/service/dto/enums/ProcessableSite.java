package ru.diefrein.pricechecker.service.dto.enums;

public enum ProcessableSite {
    LAMODA("lamoda"), GOLD_APPLE("goldapple");

    private final String domainName;

    ProcessableSite(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
