package ru.diefrein.pricechecker.bot.bot.response;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.diefrein.pricechecker.bot.bot.buttons.Button;
import ru.diefrein.pricechecker.bot.bot.buttons.ButtonLayout;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResponseCreator {

    /**
     * Create response for user
     *
     * @param processResult result of command processing
     * @return message to user
     */
    public SendMessage createResponse(ProcessResult processResult) {
        SendMessage message = new SendMessage();
        message.setText(processResult.response());
        if (processResult.buttonLayout() != null) {
            addButtonLayout(message, processResult);
        }
        return message;
    }

    private void addButtonLayout(SendMessage message, ProcessResult processResult) {
        message.enableMarkdown(true);

        List<List<InlineKeyboardButton>> keyboardButtonRows = new ArrayList<>();

        ButtonLayout buttonLayout = processResult.buttonLayout();
        var sortedButtonsRows = buttonLayout.buttonRows().stream()
                .sorted(Comparator.comparingInt(ButtonLayout.ButtonRow::rowNumber))
                .toList();
        for (ButtonLayout.ButtonRow sortedButtonsRow : sortedButtonsRows) {
            List<Button> buttonsRow = sortedButtonsRow.buttons();

            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
            for (Button button : buttonsRow) {
                InlineKeyboardButton removeBtn = InlineKeyboardButton.builder()
                        .text(button.displayText())
                        .callbackData(button.callbackData())
                        .build();
                keyboardButtonRow.add(removeBtn);
            }
            keyboardButtonRows.add(keyboardButtonRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboardButtonRows);
        message.setReplyMarkup(markup);
        message.setText(processResult.response());
    }
}
