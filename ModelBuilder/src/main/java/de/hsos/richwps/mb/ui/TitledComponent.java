/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import java.awt.Color;
import java.awt.Component;
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
    public static int DEFAULT_TITLE_HEIGHT = 25;
    private boolean foldable;
    private boolean folded = false;
    private LabelIconClickAdapter icon;
    private LabelIconClickAdapter clickAdapter;

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

        if (null != title) {
            componentTitle = new ComponentTitle(title);
            setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {titleHeight, TableLayout.FILL}}));
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
            }

        } else {
            setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
            add(component, "0 0");
        }
    }

    public void toggleFolding() {
        if(!foldable)
            return;

        folded = !folded;

        if(folded) {
            clickAdapter.setIcon(UIManager.getIcon("Tree.collapsedIcon"));

        } else {
            clickAdapter.setIcon(UIManager.getIcon("Tree.expandedIcon"));
        }

//        getLayout().removeLayoutComponent(getComponent(1));
//        remove(getComponent(1));
//        TableLayout layout = (TableLayout) getLayout();
//        layout.invalidateLayout(this);
//        invalidate();
//        validate();
//        validateTree();

//        getParent().validate();
        getComponent(1).setVisible(!folded);

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
}
