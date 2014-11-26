package de.hsos.richwps.mb.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;

/**
 * Colored border with custom thickness for each side. Setters allow different
 * colors for each side (default via constructor is one color for all sides).
 *
 * @author dziegenh
 */
public class ColorBorder extends EmptyBorder {

    protected Color topColor;
    protected Color leftColor;
    protected Color bottomColor;
    protected Color rightColor;

    public ColorBorder(Color color, int top, int left, int bottom, int right) {
        super(top, left, bottom, right);
        topColor = color;
        leftColor = color;
        bottomColor = color;
        rightColor = color;
    }

    public ColorBorder(Color color, Insets insets) {
        this(color, insets.top, insets.left, insets.bottom, insets.right);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();

        g.setColor(rightColor);
        g.fillRect(x + width - right, y, right, height);
        g.setColor(leftColor);
        g.fillRect(x, y, left, height);
        g.setColor(topColor);
        g.fillRect(x, y, width, top);
        g.setColor(bottomColor);
        g.fillRect(x, y + height - bottom, width, bottom);

        g.setColor(oldColor);
    }

    public void setTopColor(Color topColor) {
        this.topColor = topColor;
    }

    public void setLeftColor(Color leftColor) {
        this.leftColor = leftColor;
    }

    public void setBottomColor(Color bottomColor) {
        this.bottomColor = bottomColor;
    }

    public void setRightColor(Color rightColor) {
        this.rightColor = rightColor;
    }

}
