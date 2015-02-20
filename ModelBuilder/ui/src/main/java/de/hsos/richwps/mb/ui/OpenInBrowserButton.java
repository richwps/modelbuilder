package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class OpenInBrowserButton extends JButton {

        public OpenInBrowserButton(final URL link) {
            super("Open In Browser");
            this.setSize(120, 32);
            ImageIcon icon = (ImageIcon) (UIManager.get(AppConstants.ICON_OPEN_IN_BROWSER));
            this.setIcon(icon);
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            try {
                                desktop.browse(link.toURI());
                            } catch (Exception exp) {
                                Logger.log(this.getClass(), "mouseClicked()", exp);
                            }
                        }

                    }
                }
            });
            this.setVisible(true);
        }
    }