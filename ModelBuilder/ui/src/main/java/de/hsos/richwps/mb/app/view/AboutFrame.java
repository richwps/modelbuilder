/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.view;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class AboutFrame extends JDialog {

    private final Window parent;

    public AboutFrame(Window parent) {
        super(parent, AppConstants.ABOUT_DIALOG_TITLE);

        if (parent == null) {
            throw new IllegalArgumentException("AboutFrame parent can not be null");
        }

        this.parent = parent;

        setSize(400, 300);
        setResizable(false);
        setModal(true);

        addComponents();
    }

    @Override
    public void setVisible(boolean visible) {

        // set location centered to parent
        if (visible) {
            Point pLoc = parent.getLocation();
            Dimension pSize = parent.getSize();
            Dimension size = getSize();

            // calculate center location
            int x = (int) (pLoc.x + .5 * (pSize.width - size.width));
            int y = (int) (pLoc.y + .5 * (pSize.height - size.height));
            setLocation(x, y);
        }

        super.setVisible(visible);
    }

    // TODO add "about" content
    private void addComponents() {
        JPanel content = new JPanel();
        double F = TableLayout.FILL;
        double P = TableLayout.PREFERRED;

        JButton closeButton = new JButton();
        closeButton.setAction(new AbstractAction() {
            @Override
            public Object getValue(String key) {
                if(key.equals(NAME)) {
                    return AppConstants.DIALOG_BTN_CLOSE;
                }
                return super.getValue(key);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        content.setLayout(new TableLayout(new double[][]{{F, P, F}, {F, P}}));
        content.add(new JLabel("RichWPS ModelBuilder"), "0 0 2 0");
        content.add(closeButton, "1 1");

        EmptyBorder emptyBorder = new EmptyBorder(2, 2, 2, 2);
        content.setBorder(emptyBorder);
        content.setBackground(Color.WHITE);
        
        add(content);
    }

}
