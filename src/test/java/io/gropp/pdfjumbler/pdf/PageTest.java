package io.gropp.pdfjumbler.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PageTest {
    private final File file = new File("/docs/sample.pdf");
    private Locale previousLocale;

    @BeforeEach
    void useEnglishLocale() {
        previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(previousLocale);
    }

    @Test
    void pagesWithSameFileIndexAndRotationAreEqual() {
        Page page = new Page(file, 2);
        Page other = new Page(file, 2);

        assertThat(page).isEqualTo(other);
        assertThat(page).hasSameHashCodeAs(other);
    }

    @Test
    void pagesWithDifferentIndexAreNotEqual() {
        assertThat(new Page(file, 1)).isNotEqualTo(new Page(file, 2));
    }

    @Test
    void pagesFromDifferentFilesAreNotEqual() {
        assertThat(new Page(file, 1)).isNotEqualTo(new Page(new File("/docs/other.pdf"), 1));
    }

    @Test
    void pagesWithDifferentRotationAreNotEqual() {
        Page rotated = new Page(file, 1);
        rotated.setRotation(90);

        assertThat(rotated).isNotEqualTo(new Page(file, 1));
    }

    @Test
    void toStringShowsOneBasedPageNumberAndFileName() {
        assertThat(new Page(file, 0)).hasToString("1 (sample.pdf)");
    }
}
