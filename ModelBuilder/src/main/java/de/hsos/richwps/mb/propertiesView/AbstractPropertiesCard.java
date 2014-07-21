/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dziegenh
 */
public abstract class AbstractPropertiesCard extends JScrollPane {

    protected JPanel contentPanel;

    // TODO move magic numbers to config/create setter
    protected double propertyBorderThickness = 1; // property row: height of the bottom border
    protected final int COLUMN_1_WIDTH = 56;      // fixed width of first column ("header")
    protected int titleHeight = 20;               // height of titles ("process", "inputs" etc.)
    protected Insets labelInsets = new Insets(2, 5, 2, 5);

    protected Color headLabelBgColor = UIManager.getColor("Panel.background");
    protected Color bodyLabelBgColor = Color.WHITE;

    protected Color propertyTitleFgColor = Color.WHITE;
    protected Color propertyTitleBgColor1 = Color.LIGHT_GRAY;
    protected Color propertyTitleBgColor2 = Color.LIGHT_GRAY.darker();
    protected Color portBorderColor = Color.LIGHT_GRAY.darker();


    public AbstractPropertiesCard(final JPanel contentPanel) {
        super(contentPanel);

        this.contentPanel = contentPanel;
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        addComponentListener(new ComponentAdapter() {
            @Override
            // Scroll to top after component changed
            public void componentShown(ComponentEvent e) {
                getViewport().setViewPosition(new Point(0, 0));
            }

            @Override
            // Set content panel width to match the viewport.
            // Otherwise, when resizing the panel to a smaller width, the
            // panel width would not adjust and the horizontal scrollbar appears
            public void componentResized(ComponentEvent e) {
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
    }

    protected TitledComponent createTitledComponent(String title, Component component) {
        TitledComponent titledComponent = new TitledComponent(title, component, titleHeight, true);
        titledComponent.setTitleFontColor(propertyTitleFgColor);
        titledComponent.setTitleGradientColor1(propertyTitleBgColor1);
        titledComponent.setTitleGradientColor2(propertyTitleBgColor2);
        titledComponent.setTitleInsets(labelInsets);
        return titledComponent;
    }

    protected Component createColumn1Border() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(bodyLabelBgColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

    protected Component createColumn2Border() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(headLabelBgColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

    /**
     * Creates and returns a styled Label for the table head.
     *
     * @param text
     * @return
     */
    protected MultilineLabel createHeadLabel(String text) {
        return createMultilineLabel(text, headLabelBgColor);
    }

    /**
     * Creates and returns a styled label for the table body.
     *
     * @param text
     * @return
     */
    protected MultilineLabel createBodyLabel(String text) {
        MultilineLabel label = createMultilineLabel(text, bodyLabelBgColor);
        label.setFocusable(true);
        return label;
    }

    protected MultilineLabel createMultilineLabel(String text, Color background) {
        MultilineLabel label = new MultilineLabel(text);
        label.setBackground(background);
        Border emptyBorder = new EmptyBorder(labelInsets);
        label.setBorder(emptyBorder);

        return label;
    }

}
