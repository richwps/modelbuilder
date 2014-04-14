/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.ui;

import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 *
 * @author dziegenh
 */
public class MultilineLabel extends JTextArea {

    public MultilineLabel(String text) {
        super(text);
        setFont(UIManager.getFont("Panel.font"));
        setEditable(false);
        setBorder(null);
        setLineWrap(true);
        setWrapStyleWord(true);
        setFocusable(false);
    }
}
