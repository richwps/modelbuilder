package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.processProvider.exception.SpClientNotAvailableException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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

        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        JTree tree = treeView.getTreeView().getGui();
        add(new JScrollPane(tree), "0 0");
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
