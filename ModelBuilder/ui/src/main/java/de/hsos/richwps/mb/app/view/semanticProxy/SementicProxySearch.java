package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
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

        Icon searchIcon = UIManager.getIcon(AppConstants.ICON_SEARCH_KEY);
        JButton startSearch = new JButton(searchIcon);
        startSearch.setToolTipText("Search for keyword at SemanticProxy"); // TODO move to app constants
        startSearch.addActionListener(actionListener);

        // wrap button in toolbar to use it's styling
        JToolBar startSearchWrapper = new JToolBar();
        startSearchWrapper.setFloatable(false);
        startSearchWrapper.add(startSearch);
        
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL, TableLayout.PREFERRED}, {TableLayout.PREFERRED, TableLayout.FILL}}));
        add(searchInput, "0 0");
        add(startSearchWrapper, "1 0");
        add(tabs, "0 1 1 1");
    }

    public void setAppHasModel(boolean appHasModel) {
        tabs.setAppHasModel(appHasModel);
    }
    
}
