/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author dziegenh
 */
public class JLabelWithBackground extends JLabel {

    public JLabelWithBackground() {
        super();
    }

    public JLabelWithBackground(Icon icon) {
        super(icon);
    }

    public JLabelWithBackground(Icon icon, int horizontalAlignment) {
        super(icon, horizontalAlignment);
    }

    public JLabelWithBackground(String text) {
        super(text);
    }

    public JLabelWithBackground(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public JLabelWithBackground(Icon icon, String text, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color tmp = g.getColor();
        Rectangle bounds = getBounds();
        g.setColor(getBackground());
        g.fillRect(0, 0, bounds.width, bounds.height);
        g.setColor(tmp);
        super.paintComponent(g);
    }



}
