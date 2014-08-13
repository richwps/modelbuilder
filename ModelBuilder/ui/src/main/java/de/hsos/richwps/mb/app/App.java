package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.undoManager.MbUndoManager;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoableEdit;
import de.hsos.richwps.mb.app.actions.AppAbstractAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.app.view.AppFrame;
import de.hsos.richwps.mb.app.view.AppSplashScreen;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.appEvents.IAppEventObserver;
import de.hsos.richwps.mb.dsl.Export;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.ModelElementsChangedListener;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.propertiesView.AbstractPortCard;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeListener;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.server.Mock;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.DndProxyLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    private MbUndoManager undoManager;

    // TODO move to model (which has to be created...)
    private String currentModelFilename = null;

    private MainTreeViewController mainTreeView;
    private GraphView graphView;
    private PropertiesView propertiesView;
    private JLabel graphDndProxy;

    GraphDropTargetAdapter dropTargetAdapter;
    private InfoTabs infoTabs;
    private AppTreeToolbar treeViewToolbar;
    private JPanel mainTreeViewPanel;
    private JPanel subTreeViewPanel;
    private SubTreeViewController subTreeView;
    private AppPreferencesDialog preferencesDialog;

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

            splash.showMessage("Loading config");
            {
                String hostKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_HOST.name();
                String portKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_PORT.name();

                String host = AppConfig.getConfig().get(hostKey, "");
                System.setProperty("http.proxyHost", host);

                String port = AppConfig.getConfig().get(portKey, "");
                System.setProperty("http.proxyPort", port);
            }
            splash.showProgess(7);

            splash.showMessage("Loading resources");

            // Load icons etc. into UIManager
            AppRessourcesLoader.loadAll();

            // Optional Debug Logging.
            if (Arrays.asList(args).contains("debug")) {
                AppConstants.DEBUG_MODE = true;
                AppEventService.getInstance().registerObserver(new IAppEventObserver() {
                    public void eventOccured(AppEvent e) {
                        de.hsos.richwps.mb.Logger.log(e);
                    }
                });
            }

            splash.showMessageAndProgress("Creating window", 15);

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

            splash.showMessageAndProgress("Initialising tooltips", 30);

            // Setup ToolTip.
            ToolTipManager.sharedInstance().setInitialDelay(AppConstants.TOOLTIP_INITIAL_DELAY);
            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);

            splash.showMessageAndProgress("Initialising user interactions", 45);

            initDragAndDrop();

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
                        case GLOBAL_PORT:
                            Object newValue = event.getNewValue();
                            if (null != newValue
                                    && newValue instanceof String
                                    && (null != event.getSourceObject()
                                    && event.getSourceObject() instanceof ProcessPort)) {

                                ProcessPort port = (ProcessPort) event.getSourceObject();
                                String value = (String) newValue;
                                switch (event.getProperty()) {
                                    case AbstractPortCard.PORT_TITLE:
                                        port.setOwsTitle(value);
                                        break;
                                    case AbstractPortCard.PORT_ABSTRACT:
                                        port.setOwsAbstract(value);
                                        break;
                                    case AbstractPortCard.PORT_IDENTIFIER:
                                        port.setOwsIdentifier(value);
                                        break;
                                }
                                de.hsos.richwps.mb.Logger.log(newValue);
                            }
                            break;

                        default:
                        // nothing
                        }
                }
            });

            getPreferencesDialog().init();

            splash.showMessageAndProgress("Requesting processes", 60);

            // connect to SP and fill tree with services etc. received from SP
            fillMainTree();
            // TODO mocked server class for app events -> replace when server client exists!!
            AppEventService.getInstance().addSourceCommand(Mock.getInstance(), AppConstants.INFOTAB_ID_SERVER);

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

    MbUndoManager getUndoManager() {
        if (null == undoManager) {
            undoManager = new MbUndoManager();
            undoManager.addChangeListener(new MbUndoManager.UndoManagerChangeListener() {
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
     *
     * @return
     */
    private SubTreeViewController getSubTreeView() {
        if (null == subTreeView) {
            subTreeView = new SubTreeViewController(this);
        }

        return subTreeView;
    }

    /**
     * The SubTree Swing component.
     *
     * @return
     */
    protected JTree getSubTreeViewComponent() {
        return getSubTreeView().getTreeView().getGui();
    }

    /**
     * The SubTree panel containg the SubTree swing component.
     *
     * @return
     */
    public JPanel getSubTreeViewGui() {
        if (null == subTreeViewPanel) {
            subTreeViewPanel = new TitledComponent(AppConstants.SUB_TREEVIEW_TITLE, getSubTreeViewComponent());
        }
        return subTreeViewPanel;
    }

    public void fillSubTree() {
        getSubTreeView().fillTree();
    }

    private MainTreeViewController getMainTreeView() {
        if (null == mainTreeView) {
            mainTreeView = new MainTreeViewController(this);
        }

        return mainTreeView;
    }

    /**
     * The MainTree Swing component.
     *
     * @return
     */
    protected JTree getMainTreeViewComponent() {
        return getMainTreeView().getTreeView().getGui();
    }

    /**
     * The MainTree panel containg the MainTree swing component.
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

    AppPreferencesDialog getPreferencesDialog() {
        if (null == preferencesDialog) {
            preferencesDialog = new AppPreferencesDialog(frame);
        }

        return preferencesDialog;
    }

    /**
     * The graph GUI component.
     *
     * @return
     */
    public Component getGraphViewGui() {
        return getGraphView().getGui();
    }

    /**
     * The graph. Lazy init, binds listeners to it on creation.
     *
     * @return
     */
    protected GraphView getGraphView() {
        if (null == graphView) {
            graphView = new AppGraphView(this);
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

        getGraphView().addModelElementsChangedListener(new ModelElementsChangedListener() {
            @Override
            public void modelElementsChanged(Object element, GraphView.ELEMENT_TYPE type, GraphView.ELEMENT_CHANGE_TYPE changeType) {
                updateGraphDependentActions();

                if (!type.equals(GraphView.ELEMENT_TYPE.PROCESS)) {
                    return;
                }

                switch (changeType) {
                    case ADDED:
                        getSubTreeView().addNode(element);
                        break;
                    case REMOVED:
                        getSubTreeView().removeNode(element);
                        break;
                }
            }
        });

        getSubTreeView().fillTree();
        updateGraphDependentActions();
        updateModelPropertiesView();
    }

    /**
     * Sets specific actions enabled depending on the graph content. Should be
     * called when graph model changes (eg by user edits or new/load actions).
     */
    private void updateGraphDependentActions() {
        boolean graphIsEmpty = getGraphView().isEmpty();
        getActionProvider().getAction(APP_ACTIONS.DEPLOY).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.DO_LAYOUT).setEnabled(!graphIsEmpty);
    }

    protected PropertiesView getPropertiesView() {
        if (null == propertiesView) {
            propertiesView = new PropertiesView(getFrame(), AppConstants.PROPERTIES_PANEL_TITLE);
            getGraphView().addSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (getGraphView().hasSelection()) {
                        GraphView.ELEMENT_TYPE type = getGraphView().getSelectedElementType();
                        if (null != type && type.equals(GraphView.ELEMENT_TYPE.GLOBAL_PORT)) {
                            propertiesView.setSelectedGlobalPorts(getGraphView().getSelectedGlobalPorts());
                        } else {
                            propertiesView.setSelectedProcesses(getGraphView().getSelectedProcesses());
                        }
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

    void deploy() {
        try {
            String dslFile = "generated.rola";
            new Export(getGraphView().getGraph().clone()).export(dslFile);

            String content = null;
            File file = new File(dslFile); //for ex foo.txt
            FileReader reader = new FileReader(file);
            try {
                char[] chars = new char[(int) file.length()];
                reader.read(chars);
                content = new String(chars);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
            JOptionPane.showMessageDialog(getFrame(), content, "Generated ROLA", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(200);
            sb.append(AppConstants.DEPLOYMENT_FAILED);
            sb.append('\n');
            sb.append(AppConstants.SEE_LOGGING_TABS);

            JOptionPane.showMessageDialog(getFrame(), sb.toString());

            String exMsg;
            if(ex.getMessage() == null || ex.getMessage().isEmpty()) {
                exMsg = ex.getClass().getSimpleName();
            } else {
                exMsg = ex.getMessage();
            }

            sb = new StringBuilder(200);
            sb.append(AppConstants.DEPLOYMENT_FAILED);
            sb.append('\n');
            sb.append(String.format(AppConstants.ERROR_MSG_IS_FORMAT, exMsg));

            AppEventService.getInstance().fireAppEvent(sb.toString(), Mock.getInstance());
            Logger
                    .getLogger(App.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
