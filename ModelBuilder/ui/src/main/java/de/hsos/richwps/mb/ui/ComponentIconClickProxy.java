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

/**
 * Delegates mouse events to listeners if a event occured inside the icon of a
 * component. Use (add) this proxy instead of the icon, it does the icon
 * rendering.
 *
 * @author dziegenh
 */
public class ComponentIconClickProxy implements Icon {

    private static enum EVENT_TYPE {

        CLICKED, ENTERED, EXITED, PRESSED, RELEASED
    }

    private final Component component;
    private Icon icon;

    protected List<MouseListener> mouseListener = new LinkedList<>();
    private Point iconPos;

    public ComponentIconClickProxy(Component component, final Icon icon) {
        super();
        this.icon = icon;
        this.component = component;

        // delegates the mouse event if a click occured inside the icon bounds
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fireMouseEventIfInsideIcon(EVENT_TYPE.CLICKED, e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                fireMouseEventIfInsideIcon(EVENT_TYPE.ENTERED, e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fireMouseEventIfInsideIcon(EVENT_TYPE.EXITED, e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                fireMouseEventIfInsideIcon(EVENT_TYPE.PRESSED, e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                fireMouseEventIfInsideIcon(EVENT_TYPE.RELEASED, e);
            }

        });
    }

    private void fireMouseEventIfInsideIcon(EVENT_TYPE eventType, MouseEvent e) {
        if (null == iconPos) {
            return;
        }

        if (e.getX() >= iconPos.x
                && e.getY() >= iconPos.y
                && e.getX() <= iconPos.x + icon.getIconWidth()
                && e.getY() <= iconPos.y + icon.getIconHeight()) {
            fireMouseEvent(eventType, e);
        }
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        this.iconPos = null;
        component.invalidate();
        component.repaint();
    }

    private void fireMouseEvent(EVENT_TYPE eventType, MouseEvent e) {
        for (MouseListener listener : mouseListener) {
            switch (eventType) {
                case CLICKED:
                    listener.mouseClicked(e);
                    break;
                case ENTERED:
                    listener.mouseEntered(e);
                    break;
                case EXITED:
                    listener.mouseExited(e);
                    break;
                case PRESSED:
                    listener.mousePressed(e);
                    break;
                case RELEASED:
                    listener.mouseReleased(e);
                    break;
            }
        }
    }

    public void addMouseListener(MouseListener listener) {
        mouseListener.add(listener);
    }

    public boolean removeMouseListener(MouseListener listener) {
        return mouseListener.remove(listener);
    }

    @Override
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

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }

}
