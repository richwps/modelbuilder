/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.AppConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dziegenh
 */
public class ComponentTitle extends JLabel {

    private Color bgColor;

    public ComponentTitle() {
        this("", null, SwingConstants.LEFT);
    }

    public ComponentTitle(String title) {
        this(title, null, SwingConstants.LEFT);
    }

    public ComponentTitle(String title, int horizontalAlignment) {
        this(title, null, horizontalAlignment);
    }

    public ComponentTitle(Icon image) {
        this("", image, SwingConstants.LEFT);
    }

    public ComponentTitle(Icon image, int horizontalAlignment) {
        this("", image, horizontalAlignment);
    }

    public ComponentTitle(String title, Icon icon, int horizontalAlignment) {
        super(title, icon, horizontalAlignment);
        this.bgColor = new Color(0, true);
        init();
    }

    private void init() {
        // TODO move magic numbers to config/constants
        setBorder(new EmptyBorder(5,5,5,5));
    }

    public void setFontStyle(int fontStyle, boolean keepOtherStyles) {
        Font font = getFont();
        int newStyle = keepOtherStyles ? font.getStyle() & fontStyle : fontStyle;
        setFont(font.deriveFont(newStyle));
    }

    public void setBold() {
        setFontStyle(Font.BOLD, false);
    }

    public void setItalic() {
        setFontStyle(Font.ITALIC, false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        // TODO move magic numbers to config/constants
        Color color1 = Color.WHITE;
        Color color2 = AppConstants.bgColor;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        g2d.setColor(AppConstants.bgColor.darker());
        g2d.drawLine(0, h-1, w, h-1);
        super.paintComponent(g);
    }

}
