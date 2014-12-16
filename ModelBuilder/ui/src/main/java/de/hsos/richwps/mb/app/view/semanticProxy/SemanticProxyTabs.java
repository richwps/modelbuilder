package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
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
    HashMap<String, SpTabTitle> searchResultTabComponents;
    
    private final SemanticProxyInteractionComponents interactionComponents;

    private boolean appHasModel = false;

    
    
    public SemanticProxyTabs(SemanticProxyInteractionComponents components) {
        this.interactionComponents = components;

        this.searchResultTabs = new HashMap<>();
        this.searchResultTabComponents = new HashMap<>();
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

        SpSearchresultTab tab;
        SpTabTitle tabComponent;

        if (!searchResultTabs.containsKey(query)) {

            // create and add tab
            final SpSearchresultTab theTab = new SpSearchresultTab(query, interactionComponents);
            if (appHasModel) {
                theTab.initDnd();
            }

            searchResultTabs.put(query, theTab);
            addTab(query, theTab);
            final int tabIndex = indexOfComponent(theTab);

            final SpTabTitle tabTitle = new SpTabTitle(this, query, query);
            tabComponent = tabTitle;
            searchResultTabComponents.put(query, tabTitle);
            setTabComponentAt(tabIndex, tabTitle);

            // select tab when it's title component is clicked
            tabTitle.addClickListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelectedIndex(indexOfComponent(theTab));
                }
            });
            tab = theTab;

        } else {

            tab = searchResultTabs.get(query);
            tabComponent = searchResultTabComponents.get(query);
        }

        int numResults = tab.search();

        // check for errors
        if (numResults < 0) {
            removeTab(query);
            JOptionPane.showMessageDialog(
                    this.interactionComponents.parent,
                    "Error while performing SemanticProxy search."
                    + "\nSee SemenaticProxy tab for details.",
                    "SemanticProxy Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } else {
            String title = String.format(titleTemplate, query, numResults);
            tabComponent.setTitle(title);
            setSelectedComponent(tab);
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
