/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class TitledComponent extends JPanel {

    private ComponentTitle componentTitle;

    // TODO move magic number to config/constants
    public static int DEFAULT_TITLE_HEIGHT = 24;
    private boolean foldable;
    private boolean isFolded = false;
    private LabelIconClickAdapter icon;
    private LabelIconClickAdapter clickAdapter;
    private int titleHeight;

    /**
     * Stored for unfolding/uncollapsing the content.
     */
    private Dimension contentSize;

    /**
     * Creates a {@link ComponentTitle} and places it above the component. If
     * the title is null, no ComponentTitle will be created.
     */
    public TitledComponent(String title, Component component) {
        this(title, component, DEFAULT_TITLE_HEIGHT);
    }

    /**
     * Creates a {@link ComponentTitle} and places it above the component. If
     * the title is null, no ComponentTitle will be created.
     *
     * @param title
     * @param component
     * @param titleHeight
     */
    public TitledComponent(String title, Component component, int titleHeight) {
        this(title, component, titleHeight, false);
    }

    /**
     * Creates a {@link ComponentTitle} and places it above the component. If
     * the title is null, no ComponentTitle will be created.
     *
     * @param title
     * @param component
     * @param titleHeight
     */
    public TitledComponent(String title, Component component, int titleHeight, boolean foldable) {
        this.foldable = foldable;
        this.titleHeight = titleHeight;

        if (null != title) {
            componentTitle = new ComponentTitle(title);
            // height needs to be preferred to enable collapsing.
            double contentHeight = foldable ? TableLayout.MINIMUM : TableLayout.FILL;
            setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {titleHeight, contentHeight}}));
            add(componentTitle, "0 0");
            add(component, "0 1");

            // initialize folding icon
            if (foldable) {
                Icon icon = UIManager.getIcon("Tree.expandedIcon");
                clickAdapter = new LabelIconClickAdapter(componentTitle, icon);
                componentTitle.setIcon(clickAdapter);
                clickAdapter.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        toggleFolding();
                    }
                });

                // TODO add doubleclick-listener to ComponentTitle
                componentTitle.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (2 == e.getClickCount()) {
                            toggleFolding();
                        }
                    }

                });
            }

        } else {
            setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
            add(component, "0 0");
        }
    }

    public void fold() {
        if (!isFolded) {
            toggleFolding();
        }
    }

    public void unfold() {
        if (isFolded) {
            toggleFolding();
        }
    }

    public void toggleFolding() {
        if (!foldable) {
            return;
        }

        isFolded = !isFolded;

        if (isFolded) {
            clickAdapter.setIcon(UIManager.getIcon("Tree.collapsedIcon"));
            contentSize = getComponent(1).getPreferredSize();
            getComponent(1).setMinimumSize(new Dimension(0, 0));

        } else {
            clickAdapter.setIcon(UIManager.getIcon("Tree.expandedIcon"));
            getComponent(1).setMinimumSize(contentSize);
        }

        getComponent(1).setVisible(!isFolded);
    }

    public void setTitle(String title) {
        if (null == componentTitle) {
            componentTitle = new ComponentTitle(title);
        } else {
            componentTitle.setText(title);
        }
    }

    public void setTitleItalic() {
        componentTitle.setItalic();
    }

    public void setTitleBold() {
        componentTitle.setBold();
    }

    public void setTitleGradientColor1(Color color) {
        componentTitle.setGradientColor1(color);
    }

    public void setTitleGradientColor2(Color color) {
        componentTitle.setGradientColor2(color);
    }

    public void setTitleFontColor(Color color) {
        componentTitle.setForeground(color);
    }

    public void setTitleInsets(Insets insets) {
        componentTitle.setBorder(new EmptyBorder(insets));
    }

    public void setComponent(Component component) {
        // no title => component has index 0
        if (null == componentTitle) {
            remove(0);
            add(component, "0 0");

            // title exists => component index is "1"
        } else {
            // TODO ??? store and update component's size if folded
            if (isFolded) {
                contentSize = component.getPreferredSize();
                component.setMinimumSize(new Dimension(0, 0));
            }
            remove(1);
            add(component, "0 1");
        }

    }

    public boolean isFolded() {
        return isFolded;
    }

    public void setFolded(boolean folded) {
        if (folded) {
            fold();
        } else {
            unfold();
        }
    }
}
