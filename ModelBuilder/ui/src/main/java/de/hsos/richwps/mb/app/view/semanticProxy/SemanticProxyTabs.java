package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import org.apache.commons.lang.Validate;

/**
 *
 * @author dziegenh
 */
class SemanticProxyTabs extends JTabbedPane {

    HashMap<String, SpSearchresultTab> searchResultTabs;
    private final SemanticProxyInteractionComponents interactionComponents;

    private boolean appHasModel = false;

    public SemanticProxyTabs(SemanticProxyInteractionComponents components) {
        this.interactionComponents = components;

        this.searchResultTabs = new HashMap<>();
    }

    final static String titleTemplate = "\"%s\" (%d)";

    /**
     * Creates a new tab, starts the search and displays the results inside the
     * tab.
     *
     * @param query
     * @return ID of the tab
     */
    String addTab(String searchQuery) {
        Validate.notNull(searchQuery);
        Validate.notEmpty(searchQuery);

        final String query = searchQuery.trim();

        if (!searchResultTabs.containsKey(query)) {

            // create and add tab
            SpSearchresultTab tab = new SpSearchresultTab(query, interactionComponents);
            if (appHasModel) {
                tab.initDnd();
            }

            searchResultTabs.put(query, tab);
            addTab(query, tab);
            int tabIndex = indexOfComponent(tab);

            int numResults = tab.search();

            // replace title label with title + close button
            String title = String.format(titleTemplate, query, numResults);
            setTabComponentAt(tabIndex, new SpTabTitle(this, query, title));
            setSelectedIndex(tabIndex);
        }

        return query;
    }

    SpSearchresultTab getTab(String id) {
        return searchResultTabs.get(id);
    }

    void removeTab(String id) {
        SpSearchresultTab tab = searchResultTabs.get(id);

        if (null != tab) {
            remove(tab);
            searchResultTabs.remove(id);
        }
    }

    void setAppHasModel(boolean appHasModel) {
        this.appHasModel = appHasModel;

        if (appHasModel) {
            for (SpSearchresultTab tab : this.searchResultTabs.values()) {
                tab.initDnd();
            }
        }
    }

}
