/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;

/**
 *
 * @author dziegenh
 */
public class DndProxyLabel extends JLabel {

    public DndProxyLabel() {
        super();
        setBackground(new Color(0f, 1f, 0f, .1f));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color tmpColor = g.getColor();
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(tmpColor);
        super.paintComponent(g);
    }

}
