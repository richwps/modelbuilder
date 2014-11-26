package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
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
    
    public SpSearchresultTab(String query, SemanticProxyInteractionComponents components) {
        Validate.notNull(query);
        Validate.notNull(components);

        this.query = query;
        this.treeView = new ProcessesTreeViewController(components);
        this.processProvider = components.processProvider;

        setLayout(new TableLayout(new double[][] {{TableLayout.FILL}, {TableLayout.FILL}}));
        JTree tree = treeView.getTreeView().getGui();
        add(new JScrollPane(tree), "0 0");
    }
    
    /**
     * Performs a search via SP client and adds the result to the tree.
     */
    protected int search() {
        List<ProcessEntity> processes = processProvider.getProcessesByKeyword(query);
        treeView.setProcesses(processes);
        treeView.fillTree();
        return processes.size();
    }

    void initDnd() {
        treeView.initDnd();
    }

}
