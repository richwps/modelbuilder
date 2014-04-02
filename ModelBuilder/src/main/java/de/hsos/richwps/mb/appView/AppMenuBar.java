/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appView;

import de.hsos.richwps.mb.AppConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author dziegenh
 */
public class AppMenuBar extends JMenuBar {

    private final AppFrame parent;
    private JMenuItem miSave;

    private List<ActionListener> saveActionListener;

    public AppMenuBar(final AppFrame parent) {
        super();

        this.parent = parent;

        saveActionListener =  new LinkedList<ActionListener>();

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

        miSave = new JMenuItem(AppConstants.MENU_FILE_SAVE);
        miSave.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("XML-Files", "*.xml") );

                int state = fc.showSaveDialog(parent);
                if (state == JFileChooser.APPROVE_OPTION) {
                    String filename = fc.getSelectedFile().getPath();
                    if(!filename.toLowerCase().endsWith(".xml"))
                        filename = filename.concat(".xml");

                    ActionEvent event = new ActionEvent(parent, ActionEvent.ACTION_LAST, filename);
                    for(ActionListener listener : saveActionListener) {
                        listener.actionPerformed(event);
                    }
                }
                
            }
        });

        JMenuItem miExit = new JMenuItem(AppConstants.MENU_FILE_EXIT);
        miExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING) {
                });
            }
        });

        mFile.add(miSave);
        mFile.add(miPref);
        mFile.add(miExit);

        return mFile;
    }

    public void addSaveActionListener(ActionListener listener) {
        saveActionListener.add(listener);
    }

}
