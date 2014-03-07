/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appview;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author dziegenh
 */
public class AppMenuBar extends JMenuBar {

    public AppMenuBar() {
        super();
        
        // TODO just mocked
        JMenu m = new JMenu("File");
        JMenuItem mi1 = new JMenuItem("Exit");
        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        m.add(mi1);
        add(m);
    }
    
    
    
    
}
