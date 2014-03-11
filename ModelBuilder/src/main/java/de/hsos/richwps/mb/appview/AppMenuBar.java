/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appview;

import de.hsos.richwps.mb.AppConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author dziegenh
 */
public class AppMenuBar extends JMenuBar {

    private final JFrame parent;

    public AppMenuBar(final JFrame parent) {
        super();

        this.parent = parent;

        // TODO additional menus
        add(getFileMenu());
    }

    private JMenu getFileMenu() {
        JMenu mFile = new JMenu("File");

        JMenuItem miPref = new JMenuItem(AppConstants.MENU_FILE_PREFERENCES);
        miPref.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        JMenuItem miExit = new JMenuItem(AppConstants.MENU_FILE_EXIT);
        miExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING) {});
            }
        });

        mFile.add(miPref);
        mFile.add(miExit);

        return mFile;
    }
    
    
    
}
