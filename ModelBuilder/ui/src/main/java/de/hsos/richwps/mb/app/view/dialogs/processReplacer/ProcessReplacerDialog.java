package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.app.App;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.AppGraphView;
import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.appFrame.AppFrame;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
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
    private JPanel mapPortsPanelWrapper;

    private final ProcessEntity sourceProcess;
    private final mxCell sourceCell;
    private ProcessEntity targetProcess;
    private final App app;

    enum CARD {

        SELECT_PROCESS,
        MAP_PORTS
    }

    private final ProcessReplacer processReplacer;

    private JLabel mappingCardCaption;

    public ProcessReplacerDialog(App app, AppFrame frame, ProcessProvider processProvider, AppGraphView graphView, TreeNode serverTreeNode) {
        super(frame, AppConstants.PROCESS_REPLACER_DIALOG_TITLE, MbDialog.BTN_ID_BACK | MbDialog.BTN_ID_NEXT | MbDialog.BTN_ID_CANCEL | MbDialog.BTN_ID_OK);

        this.app = app;
        this.processProvider = processProvider;
        this.graphView = graphView;
        this.processReplacer = new ProcessReplacer();

        setSize(600, 400);

        getDialogButton(MbDialog.BTN_ID_BACK).setEnabled(false);
        getDialogButton(MbDialog.BTN_ID_NEXT).setEnabled(false);
        getDialogButton(MbDialog.BTN_ID_OK).setEnabled(false);

        sourceProcess = graphView.getSelectedProcesses().get(0);
        sourceCell = (mxCell) graphView.getGraph().getSelectionCell();

        init(serverTreeNode);
    }

    private void init(TreeNode serverTreeNode) {
        Container contentPane = getContentPane();
        cardLayout = new CardLayout();
        contentPane.setLayout(cardLayout);

        // create "select process" panel
        JPanel selectProcessPanel = new JPanel();
        final double p = TableLayout.PREFERRED;
        selectProcessPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL, p}, {p, TableLayout.FILL, p}}));

        JLabel treeLabel = createTreeLabel();
        selectProcessPanel.add(treeLabel, "0 0 1 0");

        createSelectProcessTree(serverTreeNode);
        selectProcessPanel.add(new JScrollPane(selectProcessTree), "0 1 1 1");

        JButton refresh = createRefreshButton();
        selectProcessPanel.add(refresh, "1 2");

        contentPane.add(selectProcessPanel, CARD.SELECT_PROCESS.name());
    }

    /**
     * create Tree caption
     *
     * @return
     */
    private JLabel createTreeLabel() {
        String format = String.format(AppConstants.PROCESS_REPLACER_TREE_CAPTION, sourceProcess.getOwsIdentifier(), sourceProcess.getServer());
        JLabel treeLabel = new JLabel(format);
        treeLabel.setBackground(null);
        treeLabel.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
        treeLabel.setToolTipText(sourceProcess.getToolTipText());
        return treeLabel;
    }

    /**
     * create Tree
     *
     * @param serverTreeNode
     */
    private void createSelectProcessTree(TreeNode serverTreeNode) {
        selectProcessTree = new SelectProcessTree(processProvider, serverTreeNode);
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
    }

    /**
     * Create refresh button
     *
     * @return
     */
    private JButton createRefreshButton() {
        final AppAction appRefreshAction = app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.RELOAD_PROCESSES);
        String actionValue = (String) appRefreshAction.getValue(Action.NAME);
        Icon actionIcon = (Icon) appRefreshAction.getValue(Action.SMALL_ICON);
        JButton refreshButton = new JButton(actionIcon);
        refreshButton.setToolTipText(actionValue);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setWaitCursor();
                final Window window = getWindow();
                new Runnable() {

                    @Override
                    public void run() {
                        appRefreshAction.fireActionPerformed();
                        DefaultMutableTreeNode processesNode = app.getMainTreeProcessesNode();
                        selectProcessTree.setServerTreeNode(processesNode);
                        window.setCursor(Cursor.getDefaultCursor());
                    }
                }.run();

            }
        });

        return refreshButton;
    }

    void setWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private Window getWindow() {
        return this;
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

            // create mapping panel
            mapPortsPanel = new MapPortsPanel(sourceProcess, targetProcess);
            JScrollPane mapPortsPanelScroller = new JScrollPane(mapPortsPanel);
            mapPortsPanelWrapper = new JPanel();
            mapPortsPanelWrapper.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
            mapPortsPanelWrapper.add(getMappingCardCaption(), "0 0");
            mapPortsPanelWrapper.add(mapPortsPanelScroller, "0 1");

            getContentPane().add(mapPortsPanelWrapper, CARD.MAP_PORTS.name());
            cardLayout.show(getContentPane(), CARD.MAP_PORTS.name());
        }

        // BACK -> show "select target process" tree
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_BACK)) {
            getDialogButton(MbDialog.BTN_ID_BACK).setEnabled(false);
            getDialogButton(MbDialog.BTN_ID_NEXT).setEnabled(true);

            cardLayout.show(getContentPane(), CARD.SELECT_PROCESS.name());

            getContentPane().remove(mapPortsPanelWrapper);
            mapPortsPanel = null;
            mapPortsPanelWrapper = null;
        }

        // OK -> replace process
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {
            processReplacer.replaceProcess(graphView, mapPortsPanel, sourceCell, targetProcess);
        }

        super.handleDialogButton(buttonId);
    }

    private JLabel getMappingCardCaption() {
        if (null == this.mappingCardCaption) {
            // create caption
            String format = String.format(AppConstants.PROCESS_REPLACER_MAPPING_CAPTION);
            this.mappingCardCaption = new JLabel(format);
            this.mappingCardCaption.setBackground(null);
            this.mappingCardCaption.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
        }

        return this.mappingCardCaption;
    }

}
