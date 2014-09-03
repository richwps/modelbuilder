/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dziegenh
 */
public class MbDialog extends JDialog {

    protected Window parent;

    private JPanel contentPane = new JPanel();

    private static Insets borderInsets = new Insets(2, 2, 2, 2);

    public MbDialog(Window parent, String title) {
        super(parent, title, JDialog.ModalityType.APPLICATION_MODAL);

        this.parent = parent;
        contentPane.setBorder(new EmptyBorder(MbDialog.borderInsets));
        super.getContentPane().add(contentPane);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            UiHelper.centerToWindow(this, parent);
        }

        super.setVisible(b);
    }

    @Override
    public Container getContentPane() {
        return contentPane;
    }

}
