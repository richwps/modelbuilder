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
import de.hsos.richwps.mb.appView.AppSplashScreen;
import de.hsos.richwps.mb.appView.toolbar.AppTreeToolbar;
import de.hsos.richwps.mb.graphView.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.PropertyChangeListener;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.DndProxyLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;

/**
 * ModelBuilder entry point. Creates and connects all components.
 *
 * @author dziegenh
 */
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

    private MainTreeView mainTreeView;
    private GraphView graphView;
    private PropertiesView propertiesView;
    private JLabel graphDndProxy;

    GraphDropTargetAdapter dropTargetAdapter;
    private InfoTabs infoTabs;
    private AppTreeToolbar treeViewToolbar;
    private JPanel mainTreeViewPanel;
    private JPanel subTreeViewPanel;
    private SubTreeView subTreeView;

    /**
     * ModelBuilder entry point. Creates and connects all components.
     *
     * @param args
     */
    public App(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        {
            //
            frame = new AppFrame(AppConstants.FRAME_TITLE);
            AppSplashScreen splash = new AppSplashScreen();
            splash.showProgess(0);

            // Interpret program arguments.
            if (Arrays.asList(args).contains("graph_editable")) {
                AppConstants.GRAPH_AUTOLAYOUT = false;
            }

            splash.showMessage("Loading resources");

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

            splash.showMessageAndProgress("Creating window", 20);

            // Create frame.
            frame.init(this);
            frame.setIconImage(((ImageIcon) UIManager.getIcon(AppConstants.ICON_MBLOGO_KEY)).getImage());

            // Delegate frame closing action
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    getActionProvider().fire(APP_ACTIONS.EXIT_APP);
                }
            });

            splash.showMessageAndProgress("Initialising tooltips", 40);

            // Setup ToolTip.
            ToolTipManager.sharedInstance().setInitialDelay(AppConstants.TOOLTIP_INITIAL_DELAY);
            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);

            splash.showMessageAndProgress("Initialising user interactions", 60);

            initDragAndDrop();

            updateModelPropertiesView();
            getPropertiesView().addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent event) {
                    switch (event.getSourceCard()) {
                        case NO_SELECTION:
                            break;
                        case MODEL:
                            // TODO move String to config or new properties model
                            if (event.getProperty().equals("name")) {
                                getGraphView().setGraphName((String) event.getNewValue());
                            }
                            break;
                        case PROCESS_SINGLE_SELECTION:
                            break;
                        case PROCESS_MULTI_SELECTION:
                            break;
                        default:
                        // nothing
                        }
                }
            });

            splash.showMessageAndProgress("Requesting processes", 80);

            // connect to SP and fill tree with services etc. received from SP
            fillMainTree();

            splash.showMessageAndProgress("ModelBuilder is ready!", 100);

            splash.setVisible(false);
            frame.setVisible(true);

            // TODO move to config provider !
            // Validate frame location and reset it if necessary.
            Dimension screenSize = UiHelper.getMultiMonitorScreenSize();
            if (frame.getX() > screenSize.width || frame.getY() > screenSize.height) {
                frame.setLocation(AppConstants.FRAME_DEFAULT_LOCATION);
            }
        }
    }

    /**
     * Deletes the current UndoManager and adds a new instance.
     */
    void reloadUndoManager() {
        undoManager = null;
        getUndoManager();
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
            processProvider = new ProcessProvider();
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
        getMainTreeView().initDnd();
        getSubTreeView().initDnd();
    }
//        // activate graph droptarget when user starts dragging a treeView node
//        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
//            public void dragGestureRecognized(DragGestureEvent dge) {
//                try {
//                    if (null != dropTargetAdapter) {
//                        return;
//                    }
//                    dropTargetAdapter = new GraphDropTargetAdapter(getProcessProvider(), getGraphView(), getGraphDndProxy());
//                    dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);
//                    dropTargetAdapter.getDropTarget().addDropTargetListener(dropTargetAdapter);
//                    getGraphDndProxy().setVisible(true);
//
//                } catch (TooManyListenersException ex) {
//                    Logger.getLogger(App.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//
//        // deactivate graph drop target when dragging ends
//        DragSource.getDefaultDragSource().addDragSourceListener(new DragSourceAdapter() {
//            @Override
//            public void dragDropEnd(DragSourceDropEvent dsde) {
//                dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);
//                getGraphDndProxy().setVisible(false);
//                dropTargetAdapter = null;
//            }
//        });
//
//        tree.setTransferHandler(getProcessTransferHandler());
//    }

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

    private SubTreeView getSubTreeView() {
        if (null == subTreeView) {
            subTreeView = new SubTreeView(this);
        }

        return subTreeView;
    }

    protected JTree getSubTreeViewComponent() {
        return getSubTreeView().getTreeView().getGui();
    }

    /**
     * The tree GUI component.
     *
     * @return
     */
    public JPanel getSubTreeViewGui() {
        if (null == subTreeViewPanel) {
            subTreeViewPanel = new TitledComponent(AppConstants.SUB_TREEVIEW_TITLE, getSubTreeViewComponent());
//            treeViewPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
//            subTreeViewPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
//            treeViewToolbar = new AppTreeToolbar(getActionProvider());
//            treeViewToolbar.setBorder(new ColorBorder(UIManager.getColor("activeCaptionBorder"), 0, 0, 1, 0));
//            treeViewPanel.add(treeViewToolbar, "0 0");
//            subTreeViewPanel.add(getSubTreeViewComponent(), "0 0");

        }
        return subTreeViewPanel;
    }

    public void fillSubTree() {
        getSubTreeView().fillTree();
    }

    private MainTreeView getMainTreeView() {
        if (null == mainTreeView) {
            mainTreeView = new MainTreeView(this);
        }

        return mainTreeView;
    }

    /**
     * The server/process tree (north-west of the frame).
     *
     * @return
     */
    protected JTree getMainTreeViewComponent() {
        return getMainTreeView().getTreeView().getGui();
    }

    /**
     * The tree GUI component.
     *
     * @return
     */
    public JPanel getMainTreeViewGui() {
        if (null == mainTreeViewPanel) {
            mainTreeViewPanel = new JPanel();
            mainTreeViewPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
            treeViewToolbar = new AppTreeToolbar(getActionProvider());
            treeViewToolbar.setBorder(new ColorBorder(UIManager.getColor("activeCaptionBorder"), 0, 0, 1, 0));
            mainTreeViewPanel.add(treeViewToolbar, "0 0");
            mainTreeViewPanel.add(getMainTreeViewComponent(), "0 1");

        }
        return mainTreeViewPanel;
    }

    void fillMainTree() {
        getMainTreeView().fillTree();
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

                        // Select All
                        case 65:
                            if (0 < (e.getModifiers() & KeyEvent.CTRL_MASK)) {
                                // TODO move select-method to graphView (boundary!!)
                                graphView.selectAll();
                            }
                            break;

                        default:
                            break;
                    }
                }
            });

            // register graph components for the event service.
            AppEventService.getInstance().addSourceCommand(graphView, AppConstants.INFOTAB_ID_EDITOR);
            AppEventService.getInstance().addSourceCommand(graphView.getGraph(), AppConstants.INFOTAB_ID_EDITOR);

            modelLoaded();
        }
        return graphView;
    }

    /**
     * Add the model's undoable graph edits to the UndoManager. Needs to be
     * called after a new model has been created or loaded.
     */
    void modelLoaded() {
        getGraphView().addUndoEventListener(new mxEventSource.mxIEventListener() {
            public void invoke(Object o, mxEventObject eo) {
                Object editProperty = eo.getProperty("edit");
                if (eo.getProperty("edit") instanceof mxUndoableEdit) {
                    mxUndoableEdit edit = (mxUndoableEdit) editProperty;
                    getUndoManager().addEdit(new AppUndoableEdit(graphView, edit, "Graph edit"));
                }
            }
        });
        
        getSubTreeView().fillTree();
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
            propertiesView = new PropertiesView(getFrame(), AppConstants.PROPERTIES_PANEL_TITLE);
            getGraphView().addSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (getGraphView().hasSelection()) {
                        propertiesView.setSelectedProcesses(getGraphView().getSelectedProcesses());
                    } else {
                        propertiesView.showCard(PropertiesView.CARD.MODEL);
                    }
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

    public void updateModelPropertiesView() {
        getPropertiesView().setModel(getGraphView().getGraph().getGraphModel());
    }

}
