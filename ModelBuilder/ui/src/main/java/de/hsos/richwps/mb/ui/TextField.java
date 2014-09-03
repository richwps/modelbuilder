package de.hsos.richwps.mb.ui;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * A JTextField with an user object.
 * @author dziegenh
 */
public class TextField<E> extends JTextField {

    private E userObject;

    public TextField() {
        super();
    }

    public TextField(String text) {
        super(text);
    }

    public TextField(int columns) {
        super(columns);
    }

    public TextField(String text, int columns) {
        super(text, columns);
    }

    public TextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }

    public E getUserObject() {
        return userObject;
    }

    public void setUserObject(E userObject) {
        this.userObject = userObject;
    }

}
