package de.hsos.richwps.mb.ui;

import java.awt.Graphics;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * Label for displaying multiple lines with breaks / line wraps.
 * @author dziegenh
 */
public class MultilineLabel extends JTextArea {

    public MultilineLabel() {
        this("");
    }

    public MultilineLabel(String text) {
        super(text);
        setFont(UIManager.getFont("Panel.font"));
        setBorder(null);
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
    }
    
}
