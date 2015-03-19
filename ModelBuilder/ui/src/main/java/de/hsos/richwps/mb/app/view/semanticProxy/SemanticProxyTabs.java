package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.TransferHandler;
import org.apache.commons.lang.Validate;

/**
 *
 * @author dziegenh
 */
class SemanticProxyTabs extends JTabbedPane {

    HashMap<String, SpSearchresultTab> searchResultTabs;
    HashMap<String, SpTabTitle> searchResultTabComponents;

    private boolean appHasModel = false;
    
    private ProcessProvider processProvider;
    private JFrame parent;
    private Component graphDndProxy;
    private GraphView graphView;
    private TransferHandler processTransferHandler;

    public SemanticProxyTabs() {
        this.searchResultTabs = new HashMap<>();
        this.searchResultTabComponents = new HashMap<>();
    }

    final static String titleTemplate = "\"%s\" (%d)";

    public void setProcessProvider(ProcessProvider processProvider) {
        this.processProvider = processProvider;
    }

    public void setParent(JFrame parent) {
        this.parent = parent;
    }

    public void setGraphDndProxy(Component graphDndProxy) {
        this.graphDndProxy = graphDndProxy;
    }

    public void setGraphView(GraphView graphView) {
        this.graphView = graphView;
    }

    public void setProcessTransferHandler(TransferHandler processTransferHandler) {
        this.processTransferHandler = processTransferHandler;
    }

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
            final SpSearchresultTab theTab = new SpSearchresultTab(query, processProvider);
            theTab.setParent(parent);
            theTab.setGraphDndProxy(graphDndProxy);
            theTab.setGraphView(graphView);
            theTab.setProcessTransferHandler(processTransferHandler);

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
                    this.parent,
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
