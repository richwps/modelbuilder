/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author dziegenh
 */
public class LabelIconClickAdapter implements Icon {

    private JLabel label;
    private Icon icon;

    protected List<MouseListener> mouseListener = new LinkedList<MouseListener>();
    private Point iconPos;


    public LabelIconClickAdapter(JLabel label, final Icon icon) {
        super();
        this.icon = icon;
        this.label = label;

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (null == iconPos) {
                    return;
                }

                if (    e.getX() >= iconPos.x
                        && e.getY() >= iconPos.y
                        && e.getX() <= iconPos.x + icon.getIconWidth()
                        && e.getY() <= iconPos.y + icon.getIconHeight()
                        ) {
                    fireMouseEvent(e);
                }
            }
        });
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        this.iconPos = null;
        label.invalidate();
        label.repaint();
    }
    
    protected void fireMouseEvent(MouseEvent e) {
        for (MouseListener listener : mouseListener) {
            listener.mouseClicked(e);
        }
    }

    public void addMouseListener(MouseListener listener) {
        mouseListener.add(listener);
    }

    public boolean removeMouseListener(MouseListener listener) {
        return mouseListener.remove(listener);
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        getIconPos().setLocation(x, y);
        icon.paintIcon(c, g, x, y);
    }

    private Point getIconPos() {
        if (null == iconPos) {
            iconPos = new Point();
        }

        return iconPos;
    }

    public int getIconWidth() {
        return icon.getIconWidth();
    }

    public int getIconHeight() {
        return icon.getIconHeight();
    }

}
