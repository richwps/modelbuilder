package de.hsos.richwps.mb;

import de.hsos.richwps.mb.appActions.AppActionProvider;
import de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.appView.AppFrame;
import de.hsos.richwps.mb.graphView.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.treeView.ProcessTransferHandler;
import de.hsos.richwps.mb.treeView.TreeView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
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
import java.io.File;
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
            if (Arrays.asList(args).contains("graph_editable")) {
                AppConstants.GRAPH_AUTOLAYOUT = false;
            }

            // TODO move/create icon loader
            String iconDir = AppConstants.RES_ICONS_DIR + File.separator;
            // Logo
            UIManager.put(AppConstants.ICON_MBLOGO_KEY, new ImageIcon(iconDir + "mb_logo.png", "mb logo icon"));
            // File icons
            UIManager.put(AppConstants.ICON_NEW_KEY, new ImageIcon(iconDir + "document-new-6.png", "new icon"));
            UIManager.put(AppConstants.ICON_OPEN_KEY, new ImageIcon(iconDir + "document-open-2.png", "open icon"));
            UIManager.put(AppConstants.ICON_SAVE_KEY, new ImageIcon(iconDir + "document-save-5.png", "save icon"));
            UIManager.put(AppConstants.ICON_SAVEAS_KEY, new ImageIcon(iconDir + "document-save-as-4.png", "save as icon"));
            UIManager.put(AppConstants.ICON_PREFERENCES_KEY, new ImageIcon(iconDir + "system-settings.png", "prefs icon"));
            UIManager.put(AppConstants.ICON_EXIT_KEY, new ImageIcon(iconDir + "dialog-close-2.png", "exit icon"));
            // Edit Icons
            UIManager.put(AppConstants.ICON_UNDO_KEY, new ImageIcon(iconDir + "arrow-undo.png", "undo icon"));
            UIManager.put(AppConstants.ICON_REDO_KEY, new ImageIcon(iconDir + "arrow-redo.png", "redo icon"));
            UIManager.put(AppConstants.ICON_LAYOUT_KEY, new ImageIcon(iconDir + "zoom-fit-best-4.png", "layout icon"));

            frame = new AppFrame(this);
            frame.setIconImage(((ImageIcon) UIManager.getIcon(AppConstants.ICON_MBLOGO_KEY)).getImage());

            // Delegate windows closing action
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    getActionProvider().fire(APP_ACTIONS.EXIT_APP);
                }

            });

            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);

            initDragAndDrop();

            frame.setVisible(true);
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
            graphDndProxy = new JLabel(" ") {
                @Override
                protected void paintComponent(Graphics g) {
                    Color tmpColor = g.getColor();
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(tmpColor);
                    super.paintComponent(g);
                }

            };
            graphDndProxy.setBackground(new Color(0f, 1f, 0f, .1f));
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
     * The graph.
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
}
