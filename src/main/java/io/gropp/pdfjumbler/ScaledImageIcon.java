package io.gropp.pdfjumbler;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

final class ScaledImageIcon extends ImageIcon {
    private static final long serialVersionUID = 1L;

    ScaledImageIcon(Image image) {
        super(image);
    }

    @Override
    public int getIconWidth() {
        return UIScale.scale(super.getIconWidth());
    }

    @Override
    public int getIconHeight() {
        return UIScale.scale(super.getIconHeight());
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(getImage(), x, y, getIconWidth(), getIconHeight(), c);
    }
}
