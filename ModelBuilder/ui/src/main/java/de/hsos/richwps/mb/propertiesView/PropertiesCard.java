package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
    private List<AbstractPropertyComponent> propertyFields;

    protected IObjectWithProperties objectWithProperties;
    private PropertiesView propertiesView;
    private final CompoundBorder propertyGroupPanelBorder;

    public PropertiesCard(final Window parentWindow, PropertiesView view) {
        super(new JPanel());

        this.parentWindow = parentWindow;
        this.contentPanel = (JPanel) getViewport().getView();
        this.propertiesView = view;

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
            // reset content panel as otherwise some visual glitches appear
            public void componentResized(ComponentEvent e) {
                adjustContentPanelSize();
            }
        });

        // create PropertyGroup borders
        int outerWidth = 1;
        int innerWidth = 2;
        Border outerDark = new ColorBorder(PropertyCardsConfig.propertyTitleBgColor2, 0, outerWidth, outerWidth, outerWidth);
        Border innerBright = new ColorBorder(PropertyCardsConfig.headLabelBgColor, innerWidth, innerWidth, innerWidth, innerWidth);
        propertyGroupPanelBorder = new CompoundBorder(outerDark, innerBright);
    }

    public IObjectWithProperties getObjectWithProperties() {
        return objectWithProperties;
    }

    public void setObjectWithProperties(IObjectWithProperties objectWithProperties) {
        this.objectWithProperties = objectWithProperties;
        createContentPanel();
    }

    protected void adjustContentPanelSize() {
        // FIXME find a better way...
        createContentPanel();
    }

    private void createContentPanel() {
        contentPanel.removeAll();
        createPropertyPanel(objectWithProperties, contentPanel);
        contentPanel.setPreferredSize(AppConstants.PROPERTIES_PANEL_PREFERRED_SIZE);
    }

    private JPanel createPropertyPanel(IObjectWithProperties objectWithProperties, JPanel propertyPanel) {
        if (null == propertyPanel) {
            propertyPanel = new JPanel();
        }

        if (null == objectWithProperties || null == objectWithProperties.getProperties()) {
            return propertyPanel;
        }

        Collection<? extends IObjectWithProperties> properties = objectWithProperties.getProperties();
        int numProperties = properties.size();

        // setup layout
        double[][] layoutSize = new double[2][];
        layoutSize[0] = new double[]{TableLayout.FILL};     // columns
        layoutSize[1] = new double[numProperties];          // rows
        Arrays.fill(layoutSize[1], TableLayout.PREFERRED);  // set all rows to preferred size

        propertyPanel.setLayout(new TableLayout(layoutSize));

        int row = 0;
        for (IObjectWithProperties aProperty : properties) {
            JPanel aPropertyRow = null;

            // Property Component
            if (aProperty instanceof AbstractPropertyComponent) {
                aPropertyRow = createPropertyComponentPanel((AbstractPropertyComponent) aProperty);

                // Property Group
            } else if (aProperty instanceof PropertyGroup
                    && null != aProperty.getPropertiesObjectName()
                    && !aProperty.getPropertiesObjectName().isEmpty()) {
                aPropertyRow = createPropertyGroupPanel((PropertyGroup) aProperty);

                // Property: get component
            } else if (aProperty instanceof Property) {
                aPropertyRow = createPropertyComponentPanel(propertiesView.getComponentFor((Property) aProperty));

            }

            // other: recursive handling
            if (null == aPropertyRow) {
                aPropertyRow = createPropertyPanel(aProperty, null);
                aPropertyRow.setBorder(new ColorBorder(PropertyCardsConfig.propertyTitleBgColor2.brighter(), 0, 0, 1, 0));
            }

            propertyPanel.add(aPropertyRow, "0 " + row++);
        }

        return propertyPanel;
    }

    private TitledComponent createPropertyGroupPanel(PropertyGroup propertyGroup) {
        JPanel propertiesPanel = new JPanel();

        // create and add properties to panel
        if (propertyGroup.getProperties().size() > 0) {
            createPropertyPanel(propertyGroup, propertiesPanel);
        } else {
            JLabel emptyLabel = new JLabel("(empty)");
            emptyLabel.setFont(emptyLabel.getFont().deriveFont(Font.ITALIC));
            propertiesPanel.add(emptyLabel);
        }

        // setup panel border
        propertiesPanel.setBorder(propertyGroupPanelBorder);

        // add panel to foldable titled component
        TitledComponent groupPanel = createTitledComponent(propertyGroup.getPropertiesObjectName(), propertiesPanel);

        return groupPanel;
    }

    private JPanel createPropertyComponentPanel(AbstractPropertyComponent component) {
        JPanel componentPanel = new JPanel();

        double[][] layoutSize = new double[][]{
            {PropertyCardsConfig.COLUMN_1_WIDTH, TableLayout.FILL},
            {TableLayout.PREFERRED, PropertyCardsConfig.propertyBorderThickness}
        };

        componentPanel.setLayout(new TableLayout(layoutSize));

        // label + component
        componentPanel.add(createHeadLabel(component.getProperty().getPropertiesObjectName()), "0 0");
        componentPanel.add(component.getComponent(), "1 0");
        // bottom border(s)
        componentPanel.add(createColumn1Border(), "0 1");
        componentPanel.add(createColumn2Border(), "1 1");

        return componentPanel;
    }

    @Deprecated
    protected List<AbstractPropertyComponent> getPropertyFields() {
        if (null == propertyFields) {
            this.propertyFields = new LinkedList<>();
        }

        return propertyFields;
    }

//    @Deprecated
//    protected JTextField createEditablePropertyField(String property, String text) {
//        PropertyTextField propertyField = new PropertyTextField(property, text);
//
//        JTextField label = (JTextField) propertyField.getComponent();
//        CompoundBorder border = new CompoundBorder(new ColorBorder(PropertyCardsConfig.headLabelBgColor, 2, 0, 0, 1), label.getBorder());
//        label.setBorder(border);
//        label.setFont(label.getFont().deriveFont(PropertyCardsConfig.propertyFieldFontSize));
//
//        label.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                parentWindow.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                parentWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//            }
//        });
//
//        getPropertyFields().add(propertyField);
//        return label;
//    }
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
