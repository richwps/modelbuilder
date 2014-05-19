package de.hsos.richwps.mb;

import de.hsos.richwps.mb.appActions.AppActionProvider;
import de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventController;
import de.hsos.richwps.mb.appEvents.IAppEventObserver;
import de.hsos.richwps.mb.appView.AppFrame;
import de.hsos.richwps.mb.graphView.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.treeView.ProcessTransferHandler;
import de.hsos.richwps.mb.treeView.TreeView;
import de.hsos.richwps.mb.ui.DndProxyLabel;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class App {

    private AppActionProvider actionProvider;

    private AppFrame frame;
    private ProcessProvider processProvider;

    private ProcessTransferHandler processTransferHandler;

    private String currentModelFilename = null;

    public static void main(String[] args) {
        App app = new App(args);
    }

    private TreeView treeView;
    private GraphView graphView;
    private PropertiesView propertiesView;
    private JLabel graphDndProxy;

    GraphDropTargetAdapter dropTargetAdapter;
    private InfoTabs infoTabs;

    public App(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        {
            // TODO create splashScreen class and move code
//            final SplashScreen splash = SplashScreen.getSplashScreen();
//            Graphics2D g = splash.createGraphics();
//            renderSplashScreen(g); 

            // Interpret program arguments.
            if (Arrays.asList(args).contains("graph_editable")) {
                AppConstants.GRAPH_AUTOLAYOUT = false;
            }

            // Load icons etc. into UIManager
            AppRessourcesLoader.loadAll();

            // Create frame.
            frame = new AppFrame(this);
            frame.setIconImage(((ImageIcon) UIManager.getIcon(AppConstants.ICON_MBLOGO_KEY)).getImage());

            // Delegate frame closing action
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    getActionProvider().fire(APP_ACTIONS.EXIT_APP);
                }

            });

            // Set ToolTip dismiss delay.
            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);

            initDragAndDrop();

            frame.setVisible(true);

            // TODO move to config provider !
            // Validate frame location and reset it if necessary.
            Dimension screenSize = UiHelper.getMultiMonitorScreenSize();
            if (frame.getX() > screenSize.width || frame.getY() > screenSize.height) {
                frame.setLocation(AppConstants.FRAME_DEFAULT_LOCATION);
            }
        }
    }

    public String getCurrentModelFilename() {
        return currentModelFilename;
    }

    public void setCurrentModelFilename(String name) {
        this.currentModelFilename = name;
    }

    public IProcessProvider getProcessProvider() {
        if (null == processProvider) {
            processProvider = new ProcessProvider();
        }
        return processProvider;
    }

    protected ProcessTransferHandler getProcessTransferHandler() {
        if (null == processTransferHandler) {
            processTransferHandler = new ProcessTransferHandler();
        }

        return processTransferHandler;
    }

    private void initDragAndDrop() {
        // activate graph droptarget when user starts dragging a treeView node
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(getTreeView().getGui(), DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent dge) {
                try {
                    if (null != dropTargetAdapter) {
                        return;
                    }
                    dropTargetAdapter = new GraphDropTargetAdapter(getProcessProvider(), getGraphView(), getGraphDndProxy());
                    dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);
                    dropTargetAdapter.getDropTarget().addDropTargetListener(dropTargetAdapter);
                    getGraphDndProxy().setVisible(true);
                } catch (TooManyListenersException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // deactivate graph drop target when dragging ends
        DragSource.getDefaultDragSource().addDragSourceListener(new DragSourceListener() {
            public void dragEnter(DragSourceDragEvent dsde) {
            }

            public void dragOver(DragSourceDragEvent dsde) {
            }

            public void dropActionChanged(DragSourceDragEvent dsde) {
            }

            public void dragExit(DragSourceEvent dse) {
            }

            public void dragDropEnd(DragSourceDropEvent dsde) {
                dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);
                getGraphDndProxy().setVisible(false);
                dropTargetAdapter = null;
            }
        });

        getTreeViewGui().setTransferHandler(getProcessTransferHandler());
    }

    /**
     * Returns an overlay panel for the graph.
     *
     * @return
     */
    public Component getGraphDndProxy() {
        if (null == graphDndProxy) {
            graphDndProxy = new DndProxyLabel();
            graphDndProxy.setVisible(false);
        }
        return graphDndProxy;
    }

    /**
     * The server/process tree (north-west of the frame).
     *
     * @return
     */
    protected TreeView getTreeView() {
        if (null == treeView) {
            // TODO mock; to be replaced when semantic proxy exists
            IProcessProvider processProvider = getProcessProvider();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(AppConstants.TREE_ROOT_NAME);
            DefaultMutableTreeNode processesNode = new DefaultMutableTreeNode(AppConstants.TREE_PROCESSES_NAME);

            for (String server : processProvider.getAllServer()) {
                DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);
                for (ProcessEntity process : processProvider.getServerProcesses(server)) {
                    serverNode.add(new DefaultMutableTreeNode(process));
                }
                processesNode.add(serverNode);
            }

            DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);
            downloadServices.add(new DefaultMutableTreeNode(""));

            DefaultMutableTreeNode local = new DefaultMutableTreeNode(AppConstants.TREE_LOCALS_NAME);
            // Outputs
            ProcessPort cOut = new ProcessPort(ProcessPortDatatype.COMPLEX);
            ProcessPort lOut = new ProcessPort(ProcessPortDatatype.LITERAL);
            cOut.setOutput(true);
            lOut.setOutput(true);
            local.add(new DefaultMutableTreeNode(cOut));
            local.add(new DefaultMutableTreeNode(lOut));
            // inputs
            ProcessPort cIn = new ProcessPort(ProcessPortDatatype.COMPLEX);
            ProcessPort lIn = new ProcessPort(ProcessPortDatatype.LITERAL);
            cIn.setOutput(false);
            lIn.setOutput(false);
            local.add(new DefaultMutableTreeNode(cIn));
            local.add(new DefaultMutableTreeNode(lIn));

            root.add(processesNode);
            root.add(downloadServices);
            root.add(local);

            treeView = new TreeView(getProcessProvider(), root);
            treeView.expandAll();

            treeView.getGui().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (2 == e.getClickCount()) {
                        DefaultMutableTreeNode node = treeView.getSelectedNode();
                        if (null != node) {

                            Object nodeObject = node.getUserObject();
                            if (nodeObject instanceof ProcessEntity) {
                                getGraphView().createNodeFromProcess((IProcessEntity) nodeObject, new Point(0, 0));
                            } else if (nodeObject instanceof ProcessPort) {
                                getGraphView().createNodeFromPort((ProcessPort) nodeObject, new Point(0, 0));
                            }
                        }
                    }
                }
            });

            treeView.getGui().setBorder(new EmptyBorder(2, 2, 2, 2));
            DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();
            cellRenderer.setBackgroundSelectionColor(AppConstants.SELECTION_BG_COLOR);

            treeView.getGui().setCellRenderer(cellRenderer);
        }

        return treeView;
    }

    /**
     * The tree GUI component
     *
     * @return
     */
    public JTree getTreeViewGui() {
        return getTreeView().getGui();
    }

    /**
     * The graph. Binds a key listener to it on creation.
     *
     * @return
     */
    protected GraphView getGraphView() {
        if (null == graphView) {
            graphView = new GraphView(getProcessProvider());
            graphView.getGui().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    // delete key
                    switch (e.getKeyCode()) {
                        // Delete
                        case 127:
                            Object[] selection = graphView.getSelection();
                            if (null != graphView.getSelection() && selection.length > 0) {
                                int choice = JOptionPane.showConfirmDialog(frame, AppConstants.CONFIRM_DELETE_CELLS, AppConstants.CONFIRM_DELETE_CELLS_TITLE, JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                    getGraphView().deleteSelectedCells();
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
            });
        }
        return graphView;
    }

    /**
     * The graph GUI component.
     *
     * @return
     */
    public Component getGraphViewGui() {
        return getGraphView().getGui();
    }

    protected PropertiesView getPropertiesView() {
        if (null == propertiesView) {
            propertiesView = new PropertiesView(AppConstants.PROPERTIES_PANEL_TITLE);
            getGraphView().addSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    propertiesView.setSelectedProcesses(getGraphView().getSelectedProcesses());

                }
            });
        }

        return propertiesView;
    }

    public Component getPropertiesViewGui() {
        return getPropertiesView();
    }

    public AppFrame getFrame() {
        return frame;
    }

    public AppActionProvider getActionProvider() {
        if (null == actionProvider) {
            AppActionHandler actionHandler = new AppActionHandler(this);
            actionProvider = new AppActionProvider(actionHandler);
        }

        return actionProvider;
    }

    /**
     * Return the visible caption String for the given item.
     *
     * @param item
     * @return
     */
    public static String getMenuItemCaption(APP_ACTIONS item) {
        String itemString = item.name();
        for (String[] itemCaption : AppConstants.MENU_ITEM_CAPTIONS) {
            if (itemCaption[0].equals(itemString)) {
                return itemCaption[1];
            }
        }
        return "";
    }

    boolean isAppAction(Object source) {
        return getActionProvider().isAppAction(source);
    }

    private InfoTabs getInfoTabs() {
        if (null == infoTabs) {
            infoTabs = new InfoTabs();
            infoTabs.setTextColor(AppConstants.INFOTABS_TEXTCOLOR);
            infoTabs.setMinimumSize(AppConstants.BOTTOM_TABS_MIN_SIZE);

            // Create/add tabs
            for (String[] tabData : AppConstants.INFOTABS) {
                infoTabs.addTab(tabData[0], tabData[1]);
            }

            AppEventController.getInstance().registerObserver(new IAppEventObserver() {
                public void eventOccured(AppEvent e) {
                    // TODO check if command equals an entry of AppConstants.INFOTABS
                    // TODO if so, output message to that tab.
                }
            });

            // TODO just mocked test events !
            AppEventController.getInstance().fireAppEvent("** connecting RichWPS-server...", this, AppConstants.INFOTABS[0][0]);
            AppEventController.getInstance().fireAppEvent("** server connection established.", this, AppConstants.INFOTABS[0][0]);
            AppEventController.getInstance().fireAppEvent("** requesting processes...", this, AppConstants.INFOTABS[0][0]);
            AppEventController.getInstance().fireAppEvent("** processes received.", this, AppConstants.INFOTABS[0][0]);
            infoTabs.output("server", "** connecting RichWPS-server...");
            infoTabs.output("server", "** server connection established.");
            infoTabs.output("server", "** requesting processes...");
            infoTabs.output("server", "** processes received.");

        }
        return infoTabs;
    }

    public Component getInfoTabGui() {
        return getInfoTabs();
    }
}
