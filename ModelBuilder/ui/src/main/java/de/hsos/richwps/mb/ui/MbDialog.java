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

    public final static int BTN_ID_NONE = 0;
    public final static int BTN_ID_CLOSE = 1 << 0;
    public final static int BTN_ID_OK = 1 << 1;
    public final static int BTN_ID_CANCEL = 1 << 2;

    /**
     * Holds instances of the dialog buttons ("ok", "cancel" etc).
     */
    private JButton[] dialogButtons = new JButton[3];

    protected Window parent;

    private JPanel contentPane = new JPanel();

    private static Insets borderInsets = new Insets(2, 2, 2, 2);

    public MbDialog(Window parent, String title) {
        this(parent, title, MbDialog.BTN_ID_NONE);
    }

    public MbDialog(Window parent, String title, int buttonIds) {
        super(parent, title, JDialog.ModalityType.APPLICATION_MODAL);

        this.parent = parent;
        contentPane.setBorder(new EmptyBorder(MbDialog.borderInsets));
        createDialogButtons(buttonIds);

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

    private void createDialogButtons(int buttons) {
        buttonPanel = null;
        JButton okButton = null;
        JButton cancelButton = null;
        JButton closeButton = null;
        double[][] size;

        // create desired button panel
        if ((buttons & BTN_ID_CLOSE) > 0) {
            buttonPanel = new JPanel();

            size = new double[][]{
                {TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL},
                {TableLayout.PREFERRED}
            };
            buttonPanel.setLayout(new TableLayout(size));
            closeButton = new JButton(AppConstants.DIALOG_BTN_CLOSE);
            buttonPanel.add(closeButton, "1 0");
        }

        if ((buttons & (BTN_ID_CANCEL | BTN_ID_OK)) > 0) {
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
        }

        if (null != okButton) {
            setDialogButton(BTN_ID_OK, okButton);
            okButton.setFont(okButton.getFont().deriveFont(Font.BOLD));
        }

        if (null != closeButton) {
            setDialogButton(BTN_ID_CLOSE, closeButton);
        }

        if (null != cancelButton) {
            setDialogButton(BTN_ID_CANCEL, cancelButton);
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
     * Gets a dialog button by its id.
     *
     * @param buttonId
     * @return
     */
    protected JButton getDialogButton(int buttonId) {
        if (isValidButtonId(buttonId)) {
            try {
                return dialogButtons[UiHelper.getExponent(buttonId)];
            } catch (Exception e) {
                // ignore
            }
        }

        // no such button
        return null;
    }

    /**
     * Puts the specified dialog button into the button array and adds an
     * actionListener as the default action handler.
     *
     * @param buttonId
     * @param button
     */
    protected void setDialogButton(final int buttonId, JButton button) {
        if (isValidButtonId(buttonId)) {
            dialogButtons[UiHelper.getExponent(buttonId)] = button;

            // delegate action to handler
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleDialogButton(buttonId);
                }
            });
        }
    }

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
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDialogButton(BTN_ID_CANCEL);
            }
        });
        // "ok" & "close" key (enter/return)
        Object windowOkKey = new Object();
        KeyStroke windowOkStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        im.put(windowOkStroke, windowOkKey);
        am.put(windowOkKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDialogButton(BTN_ID_OK);
            }
        });
    }

    private boolean isValidButtonId(int buttonId) {
        // limit for valid button ids
        return buttonId < (1 << 30);
    }

    /**
     * Can be implemented by subbers to respond to dialog button actions.
     */
    protected void handleDialogButton(int buttonId) {
        dispose();
    }

    protected boolean isTheDialogButton(int questionableButtonId, int desiredButtonId) {
        return (questionableButtonId & desiredButtonId) > 1;
    }

}
