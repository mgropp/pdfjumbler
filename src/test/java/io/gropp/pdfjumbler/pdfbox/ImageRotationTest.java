package io.gropp.pdfjumbler.pdfbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class ImageRotationTest {
    private static final int MARK = 0xFFFF0000;
    private static final int BACKGROUND = 0xFFFFFFFF;

    private static BufferedImage imageWithTopLeftMark() {
        BufferedImage image = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 2; y++) {
                image.setRGB(x, y, BACKGROUND);
            }
        }
        image.setRGB(0, 0, MARK);
        return image;
    }

    @Test
    void zeroDegreesReturnsSameImage() {
        BufferedImage image = imageWithTopLeftMark();

        assertThat(ImageRotation.rotate(image, 0)).isSameAs(image);
    }

    @Test
    void rotates90DegreesClockwise() {
        BufferedImage rotated = ImageRotation.rotate(imageWithTopLeftMark(), 90);

        assertThat(rotated.getWidth()).isEqualTo(2);
        assertThat(rotated.getHeight()).isEqualTo(3);
        assertThat(rotated.getRGB(1, 0)).isEqualTo(MARK);
    }

    @Test
    void rotates180Degrees() {
        BufferedImage rotated = ImageRotation.rotate(imageWithTopLeftMark(), 180);

        assertThat(rotated.getWidth()).isEqualTo(3);
        assertThat(rotated.getHeight()).isEqualTo(2);
        assertThat(rotated.getRGB(2, 1)).isEqualTo(MARK);
    }

    @Test
    void rotates270DegreesClockwise() {
        BufferedImage rotated = ImageRotation.rotate(imageWithTopLeftMark(), 270);

        assertThat(rotated.getWidth()).isEqualTo(2);
        assertThat(rotated.getHeight()).isEqualTo(3);
        assertThat(rotated.getRGB(0, 2)).isEqualTo(MARK);
    }

    @Test
    void rejectsOtherAngles() {
        assertThatThrownBy(() -> ImageRotation.rotate(imageWithTopLeftMark(), 45))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
