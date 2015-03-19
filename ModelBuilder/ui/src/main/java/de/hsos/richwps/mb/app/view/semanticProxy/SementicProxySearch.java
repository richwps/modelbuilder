package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * SearchPanel for searching processes.
 * <pre>
 *
 * +--[SemanticProxySearch]--+
 * | +----[JTextField]-----+ |
 * | |                     | |
 * | +---------------------+ |
 * | +-[SemanticProxyTabs]-+ |
 * | |                     | |
 * | +---------------------+ |
 * +-------------------------+
 * </pre>
 *
 * @see JPanel
 * @see JTextField
 * @see SemanticProxyTabs
 * @author dziegenh
 */
public class SementicProxySearch extends JPanel {

    private final SemanticProxyTabs tabs;
    private final JTextField searchInput;

    public SementicProxySearch() {
        this.tabs = new SemanticProxyTabs();

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
        startSearch.setToolTipText(AppConstants.SEMANTIC_PROXY_SEARCH_TOOLTIP);
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

    public void setGraphDndProxy(Component graphDndProxy) {
        this.tabs.setGraphDndProxy(graphDndProxy);
    }

    public void setGraphView(GraphView graphView) {
        this.tabs.setGraphView(graphView);
    }

    public void setParent(JFrame parent) {
        this.tabs.setParent(parent);
    }

    public void setProcessProvider(ProcessProvider processProvider) {
        this.tabs.setProcessProvider(processProvider);
    }

    public void setProcessTransferHandler(TransferHandler processTransferHandler) {
        this.tabs.setProcessTransferHandler(processTransferHandler);
    }

    public void setAppHasModel(boolean appHasModel) {
        tabs.setAppHasModel(appHasModel);
    }

}
