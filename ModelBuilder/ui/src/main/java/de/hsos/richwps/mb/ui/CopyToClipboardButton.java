package de.hsos.richwps.mb.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class CopyToClipboardButton extends JButton {

        public CopyToClipboardButton(final String content) {
            super("Copy To Clipboard");
            this.setSize(120, 48);
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
