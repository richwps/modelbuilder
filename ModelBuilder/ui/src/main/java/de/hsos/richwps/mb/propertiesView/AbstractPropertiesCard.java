package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyTextField;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * Basic property card GUI with methods for creating/adding property components.
 *
 * @author dziegenh
 */
abstract class AbstractPropertiesCard extends JScrollPane {

    protected JPanel contentPanel;

    protected List<AbstractPropertyComponent> propertyFields;
    protected final Window parentWindow;

    public AbstractPropertiesCard(final Window parentWindow, final JPanel contentPanel) {
        super(contentPanel);

        this.parentWindow = parentWindow;
        this.contentPanel = contentPanel;
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getHorizontalScrollBar().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                adjustContentPanelSize();
            }
        });

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
                adjustContentPanelSize();
            }
        });
    }

    protected void adjustContentPanelSize() {
        if (getHorizontalScrollBar().isVisible()) {
            Dimension prefSize = contentPanel.getPreferredSize();
            prefSize.width -= 2 * getVerticalScrollBar().getWidth();
            contentPanel.setPreferredSize(prefSize);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     *
     * @return
     */
    List<AbstractPropertyComponent> getPropertyFields() {
        if (null == propertyFields) {
            propertyFields = new LinkedList<>();
        }
        return propertyFields;
    }

    protected JTextField createEditablePropertyField(String property, String text) {

        PropertyTextField propertyField = new PropertyTextField(property, text);

        JTextField label = (JTextField) propertyField.getComponent();
        CompoundBorder border = new CompoundBorder(new ColorBorder(PropertyCardsConfig.headLabelBgColor, 2, 0, 0, 1), label.getBorder());
        label.setBorder(border);
        label.setFont(label.getFont().deriveFont(PropertyCardsConfig.propertyFieldFontSize));

        // TODO try to find a better way to change the cursor (label.setCursor doesn't work)
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                parentWindow.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                parentWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        getPropertyFields().add(propertyField);
        return label;
    }

    protected TitledComponent createTitledComponent(String title, Component component) {
        TitledComponent titledComponent = new TitledComponent(title, component, PropertyCardsConfig.titleHeight, true);
        titledComponent.setTitleFontColor(PropertyCardsConfig.propertyTitleFgColor);
        titledComponent.setTitleGradientColor1(PropertyCardsConfig.propertyTitleBgColor1);
        titledComponent.setTitleGradientColor2(PropertyCardsConfig.propertyTitleBgColor2);
        titledComponent.setTitleInsets(PropertyCardsConfig.labelInsets);
        return titledComponent;
    }

    protected Component createColumn1Border() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(PropertyCardsConfig.bodyLabelBgColor, 0, 0, (int) PropertyCardsConfig.propertyBorderThickness, 0));
        return border;
    }

    protected Component createColumn2Border() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(PropertyCardsConfig.headLabelBgColor, 0, 0, (int) PropertyCardsConfig.propertyBorderThickness, 0));
        return border;
    }

    /**
     * Creates and returns a styled Label for the table head.
     *
     * @param text
     * @return
     */
    protected MultilineLabel createHeadLabel(String text) {
        return createMultilineLabel(text, PropertyCardsConfig.headLabelBgColor);
    }

    /**
     * Creates and returns a styled label for the table body.
     *
     * @param text
     * @return
     */
    protected MultilineLabel createBodyLabel(String text) {
        MultilineLabel label = createMultilineLabel(text, PropertyCardsConfig.bodyLabelBgColor);
        label.setFocusable(true);
        return label;
    }

    protected MultilineLabel createMultilineLabel(String text, Color background) {
        return createMultilineLabel(text, background, false);
    }

    protected MultilineLabel createMultilineLabel(String text, Color background, boolean editable) {
        MultilineLabel label = new MultilineLabel(text);
        label.setBackground(background);
        Border emptyBorder = new EmptyBorder(PropertyCardsConfig.labelInsets);
        label.setBorder(emptyBorder);

        return label;
    }

}
