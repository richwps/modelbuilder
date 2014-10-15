package de.hsos.richwps.mb.ui;

import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 *
 * @author dziegenh
 */
public class MultilineLabel extends JTextArea {

    public MultilineLabel() {
        this("");
    }

    public MultilineLabel(String text) {
        this(text, false);
    }

    public MultilineLabel(String text, boolean editable) {
        super(text);
        setFont(UIManager.getFont("Panel.font"));
        setBorder(null);
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(editable);
        setFocusable(editable);
    }
}
