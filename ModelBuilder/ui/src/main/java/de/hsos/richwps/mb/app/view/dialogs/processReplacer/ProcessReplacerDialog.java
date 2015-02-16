package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.AppGraphView;
import de.hsos.richwps.mb.app.view.appFrame.AppFrame;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.graphView.GraphNodeCreator;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Point;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class ProcessReplacerDialog extends MbDialog {

    private final ProcessProvider processProvider;
    private final AppGraphView graphView;
    private SelectProcessTree selectProcessTree;
    private CardLayout cardLayout;
    private MapPortsPanel mapPortsPanel;

    enum CARD {

        SELECT_PROCESS,
        MAP_PORTS
    }

    private CARD currentCard = CARD.SELECT_PROCESS;

    private ProcessEntity sourceProcess;
    private mxCell sourceCell;
    private ProcessEntity targetProcess;

    private final ProcessReplacer processReplacer;
    
    public ProcessReplacerDialog(AppFrame frame, ProcessProvider processProvider, AppGraphView graphView) {
        super(frame, AppConstants.PROCESS_REPLACER_DIALOG_TITLE, MbDialog.BTN_ID_BACK | MbDialog.BTN_ID_NEXT | MbDialog.BTN_ID_CANCEL | MbDialog.BTN_ID_OK);

        this.processProvider = processProvider;
        this.graphView = graphView;
        this.processReplacer = new ProcessReplacer();
        
        setSize(600, 400);

        getDialogButton(MbDialog.BTN_ID_BACK).setEnabled(false);
        getDialogButton(MbDialog.BTN_ID_NEXT).setEnabled(false);
        getDialogButton(MbDialog.BTN_ID_OK).setEnabled(false);

        sourceProcess = graphView.getSelectedProcesses().get(0);
        sourceCell = (mxCell) graphView.getGraph().getSelectionCell();

        init();
    }

    private void init() {
        Container contentPane = getContentPane();
        cardLayout = new CardLayout();
        contentPane.setLayout(cardLayout);

        // temporary disable discovering managed remote servers
        boolean managedRemotesEnabled = processProvider.isManagedRemotesEnabled();
        processProvider.setManagedRemotesEnabled(false);

        // create "select process" panel
        Collection<WpsServer> processes = this.processProvider.getAllServerWithProcesses();
        JPanel selectProcessPanel = new JPanel();
        selectProcessPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        selectProcessTree = new SelectProcessTree(processes);
        selectProcessPanel.add(new JScrollPane(selectProcessTree), "0 0");
        selectProcessTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) newLeadSelectionPath.getLastPathComponent();
                Object treeNodeObject = treeNode.getUserObject();

                boolean processSelected = treeNodeObject instanceof ProcessEntity;
                getDialogButton(MbDialog.BTN_ID_NEXT).setEnabled(processSelected);

                if (processSelected) {
                    targetProcess = (ProcessEntity) treeNodeObject;
                } else {
                    targetProcess = null;
                }
            }
        });

        // restore "discover managed remotes" flag
        processProvider.setManagedRemotesEnabled(managedRemotesEnabled);

        contentPane.add(selectProcessPanel, CARD.SELECT_PROCESS.name());
    }

    @Override
    protected void handleDialogButton(int buttonId) {

        // NEXT -> show "map ports" panel
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_NEXT)) {
            getDialogButton(MbDialog.BTN_ID_BACK).setEnabled(true);
            getDialogButton(MbDialog.BTN_ID_NEXT).setEnabled(false);
            getDialogButton(MbDialog.BTN_ID_OK).setEnabled(true);

            // complete process loading if necessary
            targetProcess = processProvider.getFullyLoadedProcessEntity(targetProcess);

            mapPortsPanel = new MapPortsPanel(sourceProcess, targetProcess);
            getContentPane().add(mapPortsPanel, CARD.MAP_PORTS.name());

            cardLayout.show(getContentPane(), CARD.MAP_PORTS.name());
        }

        // BACK -> show "select target process" tree
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_BACK)) {
            getDialogButton(MbDialog.BTN_ID_BACK).setEnabled(false);
            getDialogButton(MbDialog.BTN_ID_NEXT).setEnabled(true);

            cardLayout.show(getContentPane(), CARD.SELECT_PROCESS.name());

            getContentPane().remove(mapPortsPanel);
            mapPortsPanel = null;
        }

        // OK -> replace process
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {
           processReplacer.replaceProcess(graphView, mapPortsPanel, sourceCell, targetProcess);
        }

        super.handleDialogButton(buttonId);
    }

}
