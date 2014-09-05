/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class MbDialog extends JDialog {

    private JPanel buttonPanel;


    public enum MB_DIALOG_BUTTONS {

        NONE, CLOSE, CANCEL_OK
    }

    protected Window parent;

    private JPanel contentPane = new JPanel();

    private static Insets borderInsets = new Insets(2, 2, 2, 2);

    public MbDialog(Window parent, String title) {
        this(parent, title, MB_DIALOG_BUTTONS.NONE);
    }

    public MbDialog(Window parent, String title, MB_DIALOG_BUTTONS buttons) {
        super(parent, title, JDialog.ModalityType.APPLICATION_MODAL);

        this.parent = parent;
        contentPane.setBorder(new EmptyBorder(MbDialog.borderInsets));
        createDialogButtons(buttons);

        addKeyBindings();
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

    private void createDialogButtons(MB_DIALOG_BUTTONS buttons) {
        buttonPanel = null;
        JButton okButton = null;
        JButton cancelButton = null;
        double[][] size;

        // create desired button panel
        if (null != buttons) {
            switch (buttons) {
                case CLOSE:
                    buttonPanel = new JPanel();

                    size = new double[][]{
                        {TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL},
                        {TableLayout.PREFERRED}
                    };
                    buttonPanel.setLayout(new TableLayout(size));
                    okButton = new JButton(AppConstants.DIALOG_BTN_CLOSE);
                    buttonPanel.add(okButton, "1 0");
                    break;

                case CANCEL_OK:
                    buttonPanel = new JPanel();

                    size = new double[][]{
                        {TableLayout.FILL, TableLayout.PREFERRED, TableLayout.PREFERRED},
                        {TableLayout.PREFERRED}
                    };
                    buttonPanel.setLayout(new TableLayout(size));

                    cancelButton = new JButton(AppConstants.DIALOG_BTN_CANCEL);
                    okButton = new JButton(AppConstants.DIALOG_BTN_OK);

                    buttonPanel.add(cancelButton, "1 0");
                    buttonPanel.add(okButton, "2 0");
                    break;

                default:
            }
        }

        if (null != okButton) {
            okButton.setFont(okButton.getFont().deriveFont(Font.BOLD));
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleOk();
                }
            });
        }

        if (null != cancelButton) {
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleCancel();
                }
            });
        }

        if (null != buttonPanel) {
            buttonPanel.setBorder(new EmptyBorder(borderInsets));
            JPanel contentWrapper = new JPanel();
            contentWrapper.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED}}));
            contentWrapper.add(contentPane, "0 0");
            contentWrapper.add(buttonPanel, "0 1");
            super.getContentPane().add(contentWrapper);

        } else {
            super.getContentPane().add(contentPane);
        }
    }

    /**
     * Can be implemented by subclasses to respond to a click on the Ok-Button.
     */
    protected void handleOk() {
        dispose();
    }

    /**
     * Can be implemented by subclasses to respond to cancel-like dialog
     * closing..
     */
    protected void handleCancel() {
        dispose();
    }

//    @Override
//    public Component add(Component comp) {
//        return getContentPane().add(comp);
//    }
//
//    @Override
//    public void add(Component comp, Object constraints) {
//        if (!comp.equals(getContentPane())) {
//            super.getContentPane().add(comp, constraints);
//        } else {
//            super.add(comp, constraints);
//        }
//    }
//
//    @Override
//    public Component add(Component comp, int index) {
//        return getContentPane().add(comp, index);
//    }
//
//    @Override
//    public void add(Component comp, Object constraints, int index) {
//        super.getContentPane().add(comp, constraints, index);
//    }
    @Override
    public void setBackground(Color bgColor) {
        if (null != buttonPanel) {
            buttonPanel.setBackground(bgColor);
        }
        super.setBackground(bgColor);
    }

    private void addKeyBindings() {
        // Default listeners for keys which close the dialog.
        ActionMap am = getRootPane().getActionMap();
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // "cancel" key (escape)
        Object windowCloseKey = new Object();
        KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        im.put(windowCloseStroke, windowCloseKey);
        am.put(windowCloseKey, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleCancel();
            }
        });
        // "ok" & "close" key (enter/return)
        Object windowOkKey = new Object();
        KeyStroke windowOkStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        im.put(windowOkStroke, windowOkKey);
        am.put(windowOkKey, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleOk();
            }
        });
    }
}
