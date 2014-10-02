package de.hsos.richwps.mb.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import layout.TableLayout;

/**
 * A Panel containing a ComboBox and a delete button which enables deleting the
 * selected CB item.
 *
 * @author dziegenh
 */
public class ComboBoxWithDeletePanel<E> extends JPanel {
    private JComboBox<E> comboBox;
    private JButton deleteButton;
    private JTextField textField = null;

    public ComboBoxWithDeletePanel() {
        createComboBox();
        createDeleteButton();

        setLayout(new TableLayout(new double[][]{{TableLayout.FILL, TableLayout.PREFERRED}, {TableLayout.PREFERRED}}));
        add(comboBox, "0 0");
        add(deleteButton, "1 0");
    }

    private void createComboBox() {
        comboBox = new JComboBox<>();

        // find the input component of the combobox
        for (Component comp : comboBox.getComponents()) {
            if (comp instanceof JTextField) {
                textField = (JTextField) comp;

                // update delete button state if the textfield's text changes
                textField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        updateDeleteButtonState();
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        updateDeleteButtonState();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        updateDeleteButtonState();
                    }
                });
            }
        }
    }

    private void createDeleteButton() {
        deleteButton = new JButton("Delete");
        deleteButton.setBorder(null);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = comboBox.getSelectedIndex();

                if (idx > -1) {
                    if (1 == comboBox.getItemCount()) {
                        deleteButton.setEnabled(false);
                    }
                    comboBox.removeItemAt(idx);
                }
            }
        });
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JComboBox<E> getComboBox() {
        return comboBox;
    }

    void updateDeleteButtonState() {
        String selectedItem = comboBox.getSelectedItem().toString();
        boolean enabled = (null != selectedItem) && (textField.getText().trim().equals(selectedItem));
        deleteButton.setEnabled(enabled);
    }

    public void setEditable(boolean editable) {
        comboBox.setEditable(editable);
    }

}
