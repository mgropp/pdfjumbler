package io.gropp.pdfjumbler.testutil;

import io.gropp.pdfjumbler.ui.Dialogs;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

public final class RecordingDialogs implements Dialogs {
    public record Message(String title, @Nullable String text) {}

    public final List<Message> confirmations = new ArrayList<>();
    public final List<Message> errors = new ArrayList<>();
    public boolean confirmationAnswer = true;
    public @Nullable List<File> filesToOpen;
    public @Nullable File fileToSave;

    @Override
    public boolean confirmWarning(String text, String title) {
        confirmations.add(new Message(title, text));
        return confirmationAnswer;
    }

    @Override
    public void showError(@Nullable String message, String title) {
        errors.add(new Message(title, message));
    }

    @Override
    public Optional<List<File>> chooseFilesToOpen() {
        return Optional.ofNullable(filesToOpen);
    }

    @Override
    public Optional<File> chooseFileToSave() {
        return Optional.ofNullable(fileToSave);
    }
}
