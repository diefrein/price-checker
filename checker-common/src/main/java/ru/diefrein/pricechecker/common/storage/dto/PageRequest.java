package ru.diefrein.pricechecker.common.storage.dto;

public record PageRequest(long pageSize, long pageNumber) {

    public PageRequest {
        if (pageSize < 0) {
            throw new IllegalArgumentException("PageSize cannot be less then zero");
        }
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("PageNumber cannot be less then or equal to zero");
        }
    }
}
