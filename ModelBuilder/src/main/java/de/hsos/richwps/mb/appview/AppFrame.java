/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appview;

import de.hsos.richwps.mb.AppConfig;
import de.hsos.richwps.mb.App;
import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.graphview.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphview.GraphView;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class AppFrame extends JFrame {

    // Components
    private JTree treeView;

    // Comnponent container
    private JSplitPane leftPanel;
    private JSplitPane mainPanel;
    private JSplitPane centerAndRightPanel;
    private JSplitPane centerPanel;
    private GraphView graphView;
    private App app;

    /**
     * Frame setup.
     */
    public AppFrame(App app) {
        super();

        this.app = app;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(AppConstants.FRAME_TITLE);
        setLocation(getStartLocation());
        setSize(getStartSize());
        if(getStartMaximized())
            setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);

        // init gui
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
        addComponents();
        setVisible(true);

        // call after component is visible and has a size
        getLeftPanel().setDividerLocation(.5);
        // TODO restore divider locations from config

        initDragAndDrop();

        // save frame location, size etc. when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Boolean maximized = (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
                AppConfig.getConfig().putBoolean(AppConfig.CONFIG_KEYS.FRAME_B_MAXIMIZED.name(), maximized);

                if(maximized)
                    setExtendedState(getExtendedState() & ~Frame.MAXIMIZED_BOTH);

                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_WIDTH.name(), getSize().width);
                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_HEIGHT.name(), getSize().height);

                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONX.name(), getLocation().x);
                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONY.name(), getLocation().y);
            }
        });
    }

    private void initDragAndDrop() {
        GraphDropTargetAdapter dropTargetAdapter = new GraphDropTargetAdapter(app.getProcessProvider(), getGraphView(), getGraphView().getGui());
        dropTargetAdapter.getDropTarget().setActive(false);

        // activate graph droptarget when user starts dragging a treeView node
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(getTreeView(), DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent dge) {
                getGraphView().getGui().getDropTarget().setActive(true);
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
                getGraphView().getGui().getDropTarget().setActive(false);
            }
        });
    }

    /**
     * Creates and adds all frame components.
     */
    private void addComponents() {
        setJMenuBar(new AppMenuBar(this));
        add(getToolbar(), "0 0");
        add(getMainPanel(), "0 1");
    }

    /**
     * The server/process tree (north-west of the frame).
     *
     * @return
     */
    public JTree getTreeView() {
        if (null == treeView) {
            // TODO mock
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
            root.add(new DefaultMutableTreeNode("server 1"));
            root.add(new DefaultMutableTreeNode("server x"));
            treeView = new JTree(root);
            treeView.setDragEnabled(true);
            treeView.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        }

        return treeView;
    }

    /**
     * The summary component (south-west of the frame).
     *
     * @return
     */
    private Component getServiceSummaryView() {
        // TODO mock
        return new JLabel();
    }

    /**
     * The graph component.
     *
     * @return
     */
    public GraphView getGraphView() {
        if (null == graphView) {
            graphView = new GraphView();
        }
        return graphView;
    }

    /**
     * The properties view (east side of the frame).
     *
     * @return
     */
    private Component getPropertiesView() {
        // TODO mock
        JLabel mock = new JLabel("Properties");
        mock.setMinimumSize(AppConstants.PROPERTIES_PANEL_MIN_SIZE);
        return mock;
    }

    /**
     * The main toolbar.
     *
     * @return
     */
    private Component getToolbar() {
        // TODO mock
        return new JLabel("Toolbar?");
    }

    /**
     * The main panel contains all content panels and components below the
     * toolbar.
     *
     * @return
     */
    private Component getMainPanel() {
        if (null == mainPanel) {
            mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainPanel.add(getLeftPanel(), JSplitPane.LEFT);
            mainPanel.add(getCenterAndRightPanel(), JSplitPane.RIGHT);
        }

        return mainPanel;
    }

    /**
     * Vertical splitbar containing the tree (top component) and the summary
     * panel (bottom component).
     *
     * @return
     */
    private JSplitPane getLeftPanel() {
        if (null == leftPanel) {
            leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            leftPanel.add(getTreeView(), JSplitPane.TOP);
            leftPanel.add(getServiceSummaryView(), JSplitPane.BOTTOM);
            // expand both components on resize
            leftPanel.setResizeWeight(.5);
            leftPanel.setMinimumSize(AppConstants.LEFT_PANEL_MIN_SIZE);
        }

        return leftPanel;
    }

    /**
     * A horizontal splitpane containing the center panel (left) and the
     * properties view (right).
     *
     * @return
     */
    private Component getCenterAndRightPanel() {
        if (null == centerAndRightPanel) {
            centerAndRightPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerAndRightPanel.add(getCenterPanel(), JSplitPane.LEFT);
            centerAndRightPanel.add(getPropertiesView(), JSplitPane.RIGHT);
            // only expand the center panel on resize
            centerAndRightPanel.setResizeWeight(1);
        }

        return centerAndRightPanel;
    }

    /**
     * The center splitpane contains the graph (top component) and tabs (bottom
     * component).
     *
     * @return
     */
    private Component getCenterPanel() {
        if (null == centerPanel) {
            centerPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            centerPanel.add(getGraphView().getGui(), JSplitPane.TOP);
            centerPanel.add(getBottomView(), JSplitPane.BOTTOM);
            // only expand the graph panel on resize
            centerPanel.setResizeWeight(1);
        }

        return centerPanel;
    }

    private Component getBottomView() {
        // TODO mock
        JLabel mock = new JLabel("Tabs");
        mock.setMinimumSize(AppConstants.BOTTOM_TABS_MIN_SIZE);
        return mock;
    }

    private Point getStartLocation() {
        int x = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONX.name(), AppConstants.FRAME_DEFAULT_LOCATION.x);
        int y = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONY.name(), AppConstants.FRAME_DEFAULT_LOCATION.y);
        return new Point(x, y);
    }
    
    private boolean getStartMaximized() {
        return AppConfig.getConfig().getBoolean(AppConfig.CONFIG_KEYS.FRAME_B_MAXIMIZED.name(), AppConstants.FRAME_DEFAULT_MAXIMIZED);
    }

    private Dimension getStartSize() {
        int w = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_WIDTH.name(), AppConstants.FRAME_DEFAULT_SIZE.width);
        int h = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_HEIGHT.name(), AppConstants.FRAME_DEFAULT_SIZE.height);
        return new Dimension(w, h);
    }

}
