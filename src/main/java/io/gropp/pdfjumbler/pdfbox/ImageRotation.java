package io.gropp.pdfjumbler.pdfbox;

import java.awt.image.BufferedImage;

final class ImageRotation {
    private ImageRotation() {}

    static BufferedImage rotate(BufferedImage source, int degrees) {
        return switch (degrees) {
            case 0 -> source;
            case 90 -> rotate90(source);
            case 180 -> rotate180(source);
            case 270 -> rotate270(source);
            default -> throw new IllegalArgumentException("Unsupported rotation: " + degrees);
        };
    }

    private static BufferedImage rotate90(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage target = new BufferedImage(height, width, source.getType());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                target.setRGB(height - y - 1, x, source.getRGB(x, y));
            }
        }
        return target;
    }

    private static BufferedImage rotate180(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage target = new BufferedImage(width, height, source.getType());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                target.setRGB(width - x - 1, height - y - 1, source.getRGB(x, y));
            }
        }
        return target;
    }

    private static BufferedImage rotate270(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage target = new BufferedImage(height, width, source.getType());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                target.setRGB(y, width - x - 1, source.getRGB(x, y));
            }
        }
        return target;
    }
}
