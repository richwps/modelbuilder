package de.hsos.richwps.mb;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoableEdit;
import de.hsos.richwps.mb.appActions.AppAbstractAction;
import de.hsos.richwps.mb.appActions.AppActionProvider;
import de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
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
import de.hsos.richwps.mb.treeView.TreeView;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
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

    private TreenodeTransferHandler processTransferHandler;

    private AppUndoManager undoManager;

    // TODO move to model (which has to be created...)
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

            // Optional Debug Logging.
            if (AppConstants.DEBUG_MODE) {
                AppEventService.getInstance().registerObserver(new IAppEventObserver() {
                    public void eventOccured(AppEvent e) {
                        de.hsos.richwps.mb.Logger.log(e);
                    }
                });
            }

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

            // Setup ToolTip.
            ToolTipManager.sharedInstance().setInitialDelay(AppConstants.TOOLTIP_INITIAL_DELAY);
            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);

            initDragAndDrop();
            
            // 1) connect to SemanticProxy
            getProcessProvider().connect();
            // 2) fill tree with services etc. received from SP
            fillTree();

            frame.setVisible(true);

            // TODO move to config provider !
            // Validate frame location and reset it if necessary.
            Dimension screenSize = UiHelper.getMultiMonitorScreenSize();
            if (frame.getX() > screenSize.width || frame.getY() > screenSize.height) {
                frame.setLocation(AppConstants.FRAME_DEFAULT_LOCATION);
            }
        }
    }

    AppUndoManager getUndoManager() {
        if (null == undoManager) {
            undoManager = new AppUndoManager();
            undoManager.addChangeListener(new AppUndoManager.UndoManagerChangeListener() {
                public void changed() {
                    AppAbstractAction undoAction = getActionProvider().getAction(APP_ACTIONS.UNDO);
                    undoAction.setName("Undo " + undoManager.getPresentationName());
                    undoAction.setEnabled(undoManager.canUndo());

                    AppAbstractAction redoAction = getActionProvider().getAction(APP_ACTIONS.REDO);
                    redoAction.setName("Redo " + undoManager.getPresentationName());
                    redoAction.setEnabled(undoManager.canRedo());
                }
            });
        }

        return undoManager;
    }

    public String getCurrentModelFilename() {
        return currentModelFilename;
    }

    public void setCurrentModelFilename(String name) {
        this.currentModelFilename = name;
    }

    ProcessProvider getProcessProvider() {
        if (null == processProvider) {
            String url = AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name(), AppConstants.SEMANTICPROXY_DEFAULT_URL);
            processProvider = new ProcessProvider(url);
            AppEventService.getInstance().addSourceCommand(processProvider, AppConstants.INFOTAB_ID_SEMANTICPROXY);
        }
        return processProvider;
    }

    protected TreenodeTransferHandler getProcessTransferHandler() {
        if (null == processTransferHandler) {
            processTransferHandler = new TreenodeTransferHandler();
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
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(AppConstants.TREE_ROOT_NAME);

            treeView = new TreeView(root);
//            treeView.expandAll();

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

    void fillTree() {
        IProcessProvider processProvider = getProcessProvider();

        // Remove existing child-nodes from root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getTreeView().getGui().getModel().getRoot();
        root.removeAllChildren();

        // Create and fill Process node
        DefaultMutableTreeNode processesNode = new DefaultMutableTreeNode(AppConstants.TREE_PROCESSES_NAME);
        for (String server : processProvider.getAllServer()) {
            DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);
            for (ProcessEntity process : processProvider.getServerProcesses(server)) {
                serverNode.add(new DefaultMutableTreeNode(process));
            }
            processesNode.add(serverNode);
        }

        // Create and fill download services node
        DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);
        downloadServices.add(new DefaultMutableTreeNode(""));

        // Create and fill local elements node
        DefaultMutableTreeNode local = new DefaultMutableTreeNode(AppConstants.TREE_LOCALS_NAME);
        // Outputs
        ProcessPort cOut = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lOut = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cOut.setGlobalOutput(true);
        lOut.setGlobalOutput(true);
        local.add(new DefaultMutableTreeNode(cOut));
        local.add(new DefaultMutableTreeNode(lOut));
        // inputs
        ProcessPort cIn = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lIn = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cIn.setGlobalOutput(false);
        lIn.setGlobalOutput(false);
        local.add(new DefaultMutableTreeNode(cIn));
        local.add(new DefaultMutableTreeNode(lIn));

        // add all child nodes to root
        root.add(processesNode);
        root.add(downloadServices);
        root.add(local);

        getTreeView().getGui().updateUI();
        getTreeView().expandAll();
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
     * The graph. Lazy init, binds listeners to it on creation.
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

            // Add undoable graph edits to the UndoManager.
            graphView.addUndoEventListener(new mxEventSource.mxIEventListener() {
                public void invoke(Object o, mxEventObject eo) {
                    Object editProperty = eo.getProperty("edit");
                    if (eo.getProperty("edit") instanceof mxUndoableEdit) {
                        mxUndoableEdit edit = (mxUndoableEdit) editProperty;
                        getUndoManager().addEdit(new AppUndoableEdit(graphView, edit, "Graph edit"));
                    }
                }

            });

            // register graph components for the event service.
            AppEventService.getInstance().addSourceCommand(graphView, AppConstants.INFOTAB_ID_EDITOR);
            AppEventService.getInstance().addSourceCommand(graphView.getGraph(), AppConstants.INFOTAB_ID_EDITOR);
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

            AppEventService.getInstance().registerObserver(new IAppEventObserver() {
                public void eventOccured(AppEvent e) {
                    String command = e.getCommand();
                    String message = e.getMessage();

                    for (String[] infoTab : AppConstants.INFOTABS) {
                        String tabId = infoTab[0];
                        if (tabId.equals(command)) {
                            infoTabs.output(tabId, message);
                        }
                    }
                }
            });

        }
        return infoTabs;
    }

    /**
     * Blackboxed component getter for InfoTabs.
     *
     * @return
     */
    public Component getInfoTabGui() {
        return getInfoTabs();
    }
}
