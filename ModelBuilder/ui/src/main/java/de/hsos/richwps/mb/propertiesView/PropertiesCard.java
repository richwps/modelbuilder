package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.properties.propertyComponents.PropertyTextField;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 * Basic property card GUI with methods for creating/adding property components.
 *
 * @author dziegenh
 */
class PropertiesCard extends JScrollPane {

    protected JPanel contentPanel;

    protected final Window parentWindow;

    @Deprecated
    protected List<AbstractPropertyComponent> propertyFields;

    protected IObjectWithProperties objectWithProperties;

    public PropertiesCard(final Window parentWindow, IObjectWithProperties objectWithProperties, final JPanel contentPanel) {
        this(parentWindow, contentPanel);
        this.objectWithProperties = objectWithProperties;
        createContentPanel();
    }

    public IObjectWithProperties getObjectWithProperties() {
        return objectWithProperties;
    }

    @Deprecated
    public PropertiesCard(final Window parentWindow, final JPanel contentPanel) {
        super(contentPanel);

        this.parentWindow = parentWindow;
        this.contentPanel = contentPanel;

        // setup Scrollbars
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

    public void setObjectWithProperties(IObjectWithProperties objectWithProperties) {
        this.objectWithProperties = objectWithProperties;
        createContentPanel();
    }

    private void createContentPanel() {
        contentPanel.removeAll();

        Collection<PropertyGroup> propertyGroups = objectWithProperties.getPropertyGroups();
        int numGroups = propertyGroups.size();

        // setup layout
        double[][] layoutSize = new double[2][];
        layoutSize[0] = new double[]{TableLayout.FILL};     // columns
        layoutSize[1] = new double[numGroups];              // rows
        Arrays.fill(layoutSize[1], TableLayout.PREFERRED);  // set rows to preferred size

        contentPanel.setLayout(new TableLayout(layoutSize));

        int row = 0;
        for (PropertyGroup aGroup : propertyGroups) {
            Component aGroupPanel = createPropertyGroupPanel(aGroup);
            contentPanel.add(aGroupPanel, "0 " + row++);
        }
    }

    private TitledComponent createPropertyGroupPanel(PropertyGroup propertyGroup) {
        Collection<AbstractPropertyComponent> components = propertyGroup.getPropertyComponents().values();

        JPanel propertiesPanel = new JPanel();

        // double the numbers because each component gets a bottom border
        int layoutRows = 2 * components.size();

        double[][] layoutSize = new double[2][];
        layoutSize[0] = new double[]{PropertyCardsConfig.COLUMN_1_WIDTH, TableLayout.FILL};
        layoutSize[1] = new double[layoutRows];
        // setup layout rows
        for (int i = 0; i < layoutRows; i += 2) {
            layoutSize[1][i] = TableLayout.PREFERRED;
            layoutSize[1][i + 1] = PropertyCardsConfig.propertyBorderThickness;
        }

        propertiesPanel.setLayout(new TableLayout(layoutSize));

        int row = 0;
        for (AbstractPropertyComponent aComponent : components) {
            // label + component
            propertiesPanel.add(createHeadLabel(aComponent.getPropertyName()), "0 " + row);
            propertiesPanel.add(aComponent.getComponent(), "1 " + row++);
            // bottom border(s)
            propertiesPanel.add(createColumn1Border(), "0 " + row);
            propertiesPanel.add(createColumn2Border(), "1 " + row++);
        }

        TitledComponent groupPanel = createTitledComponent(propertyGroup.getName(), propertiesPanel);
        groupPanel.setBorder(new ColorBorder(PropertyCardsConfig.propertyTitleBgColor2, 0, 0, 1, 0));

        return groupPanel;
    }

    @Deprecated
    protected List<AbstractPropertyComponent> getPropertyFields() {
        if (null == propertyFields) {
            this.propertyFields = new LinkedList<>();
        }

        return propertyFields;
    }

    @Deprecated
    protected JTextField createEditablePropertyField(String property, String text) {

        PropertyTextField propertyField = new PropertyTextField(property, text);

        JTextField label = (JTextField) propertyField.getComponent();
        CompoundBorder border = new CompoundBorder(new ColorBorder(PropertyCardsConfig.headLabelBgColor, 2, 0, 0, 1), label.getBorder());
        label.setBorder(border);
        label.setFont(label.getFont().deriveFont(PropertyCardsConfig.propertyFieldFontSize));

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
