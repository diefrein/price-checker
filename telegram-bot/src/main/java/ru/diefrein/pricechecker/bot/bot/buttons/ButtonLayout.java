package ru.diefrein.pricechecker.bot.bot.buttons;

import java.util.List;

public record ButtonLayout(List<ButtonRow> buttonRows) {

    public ButtonLayout {
        if (buttonRows == null) {
            throw new IllegalArgumentException("buttonRows cannot be null");
        }
    }

    public record ButtonRow(int rowNumber, List<Button> buttons) {
    }
}
