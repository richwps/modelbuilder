package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
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
    public final static int BTN_ID_CLOSE = 1;
    public final static int BTN_ID_OK = 1 << 1;
    public final static int BTN_ID_CANCEL = 1 << 2;
    public final static int BTN_ID_BACK = 1 << 3;
    public final static int BTN_ID_NEXT = 1 << 4;

    /**
     * Holds instances of the dialog buttons ("ok", "cancel" etc).
     */
    private JButton[] dialogButtons = new JButton[5];

    protected Window parent;

    private final JPanel contentPane = new JPanel();

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

    protected void createDialogButtons(int buttons) {
        buttonPanel = null;
        JButton okButton = null;
        JButton cancelButton = null;
        JButton closeButton = null;
        JButton backButton = null;
        JButton nextButton = null;

        double[][] size;

        int numButtons = UiHelper.countIntsOneBits(buttons);

        double[] sizeX = new double[numButtons + 1]; // number of buttons + spacing
        Arrays.fill(sizeX, TableLayout.PREFERRED);
        sizeX[0] = TableLayout.FILL;

        size = new double[][]{
            sizeX,
            {TableLayout.PREFERRED}
        };

        // Button's X constraint
        int btnX = 1;

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new TableLayout(size));

        // just a close button => center it
        if ((buttons & BTN_ID_CLOSE) == BTN_ID_CLOSE) {

            closeButton = new JButton(AppConstants.DIALOG_BTN_CLOSE);

            if (numButtons == 1) {
                buttonPanel = new JPanel();

                size = new double[][]{
                    {TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL},
                    {TableLayout.PREFERRED}
                };
                buttonPanel.setLayout(new TableLayout(size));
            }

            buttonPanel.add(closeButton, btnX + " 0");
            setDialogButton(BTN_ID_CLOSE, closeButton);
            btnX++;
        }

        if ((buttons & BTN_ID_CANCEL) == BTN_ID_CANCEL) {
            cancelButton = new JButton(AppConstants.DIALOG_BTN_CANCEL);
            buttonPanel.add(cancelButton, btnX + " 0");
            setDialogButton(BTN_ID_CANCEL, cancelButton);
            btnX++;
        }

        if ((buttons & BTN_ID_BACK) == BTN_ID_BACK) {
            backButton = new JButton(AppConstants.DIALOG_BTN_BACK);
            buttonPanel.add(backButton, btnX + " 0");
            setDialogButton(BTN_ID_BACK, backButton);
            btnX++;
        }

        if ((buttons & BTN_ID_NEXT) == BTN_ID_NEXT) {
            nextButton = new JButton(AppConstants.DIALOG_BTN_NEXT);
            buttonPanel.add(nextButton, btnX + " 0");
            setDialogButton(BTN_ID_NEXT, nextButton);
            btnX++;
        }

        if ((buttons & BTN_ID_OK) == BTN_ID_OK) {
            okButton = new JButton(AppConstants.DIALOG_BTN_OK);
            buttonPanel.add(okButton, btnX + " 0");
            setDialogButton(BTN_ID_OK, okButton);
            okButton.setFont(okButton.getFont().deriveFont(Font.BOLD));
            btnX++;
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
        if (isTheDialogButton(buttonId, BTN_ID_CLOSE)
                || isTheDialogButton(buttonId, BTN_ID_CANCEL)
                || isTheDialogButton(buttonId, BTN_ID_OK)) {
            dispose();
        }
    }

    protected boolean isTheDialogButton(int questionableButtonId, int desiredButtonId) {
        return (questionableButtonId & desiredButtonId) > 0;
    }

}
