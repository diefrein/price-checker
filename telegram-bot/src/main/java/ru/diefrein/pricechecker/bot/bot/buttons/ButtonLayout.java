package ru.diefrein.pricechecker.bot.bot.buttons;

import java.util.List;

public record ButtonLayout(List<ButtonRow> buttonRows, ButtonLayoutType buttonLayoutType) {

    public ButtonLayout {
        if (buttonLayoutType == null) {
            throw new IllegalArgumentException("ButtonLayoutType cannot be null");
        }
    }

    public record ButtonRow(int rowNumber, List<Button> buttons) {
    }
}
