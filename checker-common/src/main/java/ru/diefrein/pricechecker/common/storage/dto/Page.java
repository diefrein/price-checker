package ru.diefrein.pricechecker.common.storage.dto;

import java.util.List;

public record Page<T>(List<T> data, PageMeta meta) {

    public record PageMeta(PageRequest pageRequest, boolean hasNext) {
    }
}
