package de.hsos.richwps.mb.app.view.treeView;

import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar;
import de.hsos.richwps.mb.ui.ColorBorder;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class MainTreeViewPanel extends JPanel {

    private boolean init = false;
    private AppTreeToolbar treeViewToolbar;

    public void init(JTree tree, AppActionProvider actionProvider) {
        if (init) {
            return;
        }

        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        setLayout(new TableLayout(new double[][]{{f}, {p, f}}));

        // add tree toolbar
        treeViewToolbar = new AppTreeToolbar(actionProvider);
        treeViewToolbar.setBorder(new ColorBorder(UIManager.getColor("activeCaptionBorder"), 0, 0, 1, 0));
        add(treeViewToolbar, "0 0");

        // add tree
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBorder(null);
        add(treeScrollPane, "0 1");
    }

}
