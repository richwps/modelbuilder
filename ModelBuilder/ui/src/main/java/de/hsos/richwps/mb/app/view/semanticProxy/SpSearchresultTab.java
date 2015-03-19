package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.view.treeView.ProcessesTreeViewController;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.Component;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import layout.TableLayout;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author dziegenh
 */
public class SpSearchresultTab extends JPanel {

    private final String query;

    private final ProcessesTreeViewController treeView;

    private final ProcessProvider processProvider;

    public SpSearchresultTab(String query, ProcessProvider processProvider) {
        Validate.notNull(query);
        Validate.notNull(processProvider);

        this.query = query;
        this.processProvider = processProvider;
        
        this.treeView = new ProcessesTreeViewController();
        this.treeView.setProcessProvider(processProvider);

        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        JTree tree = treeView.getTreeView().getGui();
        add(new JScrollPane(tree), "0 0");
    }

    public void setGraphDndProxy(Component graphDndProxy) {
        treeView.setGraphDndProxy(graphDndProxy);
    }

    public void setGraphView(GraphView graphView) {
        treeView.setGraphView(graphView);
    }

    public void setParent(JFrame parent) {
        treeView.setParent(parent);
    }

    public void setProcessTransferHandler(TransferHandler processTransferHandler) {
        treeView.setProcessTransferHandler(processTransferHandler);
    }

    /**
     * Performs a search via SP client and adds the result to the tree.
     *
     * @return search result count or a negative number if an error occured.
     */
    protected int search() {
        List<ProcessEntity> processes = processProvider.getProcessesByKeyword(query);

        // null is returned if an error occured
        if (null != processes) {
            treeView.setProcesses(processes);
            treeView.fillTree();

            return processes.size();
        }

        // returning a negative number indicates occured errors.
        return -1;
    }

    void initDnd() {
        treeView.initDnd();
    }

}
