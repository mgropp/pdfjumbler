package io.gropp.pdfjumbler.ui;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.tinylog.Logger;

public interface Dialogs {
    boolean confirmWarning(String text, String title);

    void showError(@Nullable String message, String title);

    default void showError(Throwable throwable) {
        Logger.error(throwable);
        showError(throwable.getLocalizedMessage(), throwable.getClass().getSimpleName());
    }

    Optional<List<File>> chooseFilesToOpen();

    Optional<File> chooseFileToSave();
}
