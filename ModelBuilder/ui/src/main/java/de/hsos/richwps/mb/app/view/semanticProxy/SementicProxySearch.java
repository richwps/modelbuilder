package de.hsos.richwps.mb.app.view.semanticProxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultTreeCellEditor;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class SementicProxySearch extends JPanel {

    private final SemanticProxyTabs tabs;
    private final JTextField searchInput;

    public SementicProxySearch(SemanticProxyInteractionComponents components) {
        this.tabs = new SemanticProxyTabs(components);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchInput.getText().trim();
                searchInput.setText(query);

                if (!query.isEmpty()) {
                    tabs.addTab(query);
                }
            }
        };

        searchInput = new JTextField();
        searchInput.addActionListener(actionListener);

        JButton startSearch = new JButton("Search"); // TODO replace text with search icon
        startSearch.addActionListener(actionListener);

        setLayout(new TableLayout(new double[][]{{TableLayout.FILL, TableLayout.PREFERRED}, {TableLayout.PREFERRED, TableLayout.FILL}}));
        add(searchInput, "0 0");
        add(startSearch, "1 0");
        add(tabs, "0 1 1 1");
    }

}
