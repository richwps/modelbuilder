package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class CopyToClipboardButton extends JButton {

        public CopyToClipboardButton(final String content) {
            super("Copy To Clipboard");
            this.setSize(120, 32);
            ImageIcon icon = (ImageIcon) (UIManager.get(AppConstants.ICON_EDIT_COPY_KEY));
            this.setIcon(icon);
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    final StringSelection stringSelection = new StringSelection(content);
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(stringSelection, null);
                }
            });
            this.setVisible(true);
        }
    }
