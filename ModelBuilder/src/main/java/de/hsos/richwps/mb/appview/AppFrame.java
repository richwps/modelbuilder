/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appview;

import de.hsos.richwps.mb.treeview.TreeView;
import javax.swing.JFrame;

/**
 *
 * @author fbensman
 */
public class AppFrame extends JFrame{
    
    public AppFrame(){
        super();
        setTitle("ModelBuilder");
        add(new TreeView());
        pack();
        setVisible(true);
    }
}
