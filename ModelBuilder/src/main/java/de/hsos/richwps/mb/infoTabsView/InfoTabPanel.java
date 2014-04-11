/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.infoTabsView;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class InfoTabPanel extends JPanel {

    private JTextArea textArea;

    public InfoTabPanel() {

        TableLayout fillLayout = new TableLayout(new double[][] {{TableLayout.FILL},{TableLayout.FILL}});


        textArea = new JTextArea();
        textArea.setEnabled(false);
//        textArea.

        JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setLayout(fillLayout);

        setLayout(fillLayout);
        add(scrollPane, "0 0");
    }

    void appendOutput(String text) {
        textArea.append(text);
    }

    void setOutput(String text) {
        textArea.setText(text);
    }

    String getOutput() {
        return textArea.getText();
    }

}
