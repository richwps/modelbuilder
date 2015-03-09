package de.hsos.richwps.mb.infoTabsView;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import layout.TableLayout;

/**
 * A content panel for an info tab.
 *
 * @author dziegenh
 */
public class InfoTabPanel extends JPanel {

    private JTextArea textArea;
    private final JScrollPane scrollPane;

    public InfoTabPanel() {

        TableLayout fillLayout = new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}});

        textArea = new JTextArea();
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea);

        setLayout(fillLayout);
        add(scrollPane, "0 0");
    }

    void setFontSize(float size) {
        textArea.setFont(textArea.getFont().deriveFont(size));
    }

    void setTextColor(Color color) {
        textArea.setForeground(color);
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

    void scrollToBottom() {
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }
    
    void clear() {
        textArea.setText("");
    }

}
