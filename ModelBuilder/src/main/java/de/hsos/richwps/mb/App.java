package de.hsos.richwps.mb;

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
import java.awt.Component;
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
import java.io.File;
import java.util.Arrays;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class App {

    private AppFrame frame;
    private ProcessProvider processProvider;

    private ProcessTransferHandler processTransferHandler;

    public static void main(String[] args) {
        App app = new App(args);
    }

    private TreeView treeView;
    private GraphView graphView;
    private PropertiesView propertiesView;
    private AppActionHandler actionHandler;

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

            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);

            if (AppConstants.GRAPH_AUTOLAYOUT) {
                initDragAndDrop();
            }

            frame.setVisible(true);
        }
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

    GraphDropTargetAdapter dropTargetAdapter;

    private void initDragAndDrop() {
//        final GraphDropTargetAdapter dropTargetAdapter = new GraphDropTargetAdapter(getProcessProvider(), getGraphView(), getGraphViewGui());
//        dropTargetAdapter.getDropTarget().setActive(false);
//        dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);

        // activate graph droptarget when user starts dragging a treeView node
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(getTreeView().getGui(), DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent dge) {
                try {

                    dropTargetAdapter = new GraphDropTargetAdapter(getProcessProvider(), getGraphView(), getGraphViewGui());
                    dropTargetAdapter.getDropTarget().addDropTargetListener(dropTargetAdapter);
//                getGraphViewGui().getDropTarget().setActive(true);
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
                dropTargetAdapter = null;

//                getGraphViewGui().getDropTarget().setActive(false);
            }
        });

        getTreeViewGui().setTransferHandler(getProcessTransferHandler());
    }

    /**
     * The server/process tree (north-west of the frame).
     *
     * @return
     */
    protected TreeView getTreeView() {
        if (null == treeView) {
            // TODO mock
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
            local.add(new DefaultMutableTreeNode(new ProcessPort(ProcessPortDatatype.COMPLEX)));
            local.add(new DefaultMutableTreeNode(new ProcessPort(ProcessPortDatatype.LITERAL)));

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
                                getGraphView().createNodeFromProcess((IProcessEntity) nodeObject);
                            } else if (nodeObject instanceof ProcessPort) {
                                getGraphView().createNodeFromPort((ProcessPort) nodeObject);
                            }
                        }
                    }
                }
            });
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

    // TODO make privat
    public AppActionHandler getActionHandler() {
        if (null == actionHandler) {
            actionHandler = new AppActionHandler(this);
        }

        return actionHandler;
    }

    public AppFrame getFrame() {
        return frame;
    }
}
