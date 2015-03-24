package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.entity.IOwsObject;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.properties.PropertyKeyTranslator;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
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

    protected IObjectWithProperties objectWithProperties;
    private final PropertiesView propertiesView;
    private final CompoundBorder propertyGroupPanelBorder;

    public PropertiesCard(final Window parentWindow, PropertiesView view) {
        super(new JPanel());

        this.parentWindow = parentWindow;
        this.contentPanel = (JPanel) getViewport().getView();
        this.propertiesView = view;

        // setup Scrollbars
        getHorizontalScrollBar().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                scrollToTop();

            }
        });

        // create PropertyGroup borders
        int outerWidth = 1;
        int innerWidth = 2;
        Border outerDark = new ColorBorder(PropertyCardsConfig.propertyTitleBgColor2, 0, outerWidth, outerWidth, outerWidth);
        Border innerBright = new ColorBorder(PropertyCardsConfig.headLabelBgColor, innerWidth, innerWidth, innerWidth, innerWidth);
        propertyGroupPanelBorder = new CompoundBorder(outerDark, innerBright);
    }

    protected String getCaptionFor(IObjectWithProperties object) {
        final String propertyKey = object.getPropertiesObjectName();

        if (object.isTranslatable()) {
            return this.propertiesView.getPropertyKeyTranslator().translate(propertyKey);
        }

        return propertyKey;
    }

    @Override
    public void updateUI() {
        super.updateUI();

        new Runnable() {
            @Override
            public void run() {
                scrollToTop();
            }
        }.run();
    }

    public IObjectWithProperties getObjectWithProperties() {
        return objectWithProperties;
    }

    public void setObjectWithProperties(IObjectWithProperties objectWithProperties) {
        this.objectWithProperties = objectWithProperties;
        createContentPanel();

        scrollToTop();
    }

    public void scrollToTop() {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        getViewport().setViewPosition(new Point(0, 0));

                    }
                });
    }

    protected void adjustContentPanelSize() {
        // TODO find a way to adjust the components' widths
    }

    private void createContentPanel() {
        contentPanel.removeAll();
        createPropertyPanel(objectWithProperties, contentPanel);
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
                Property aPropertyLeaf = (Property) aProperty;

                // FIXME don't create/add any component if the component type is "none"
                if (aPropertyLeaf.getComponentType().equals(Property.COMPONENT_TYPE_NONE)) {
                    aPropertyRow = new JPanel();

                } else {
                    aPropertyRow = createPropertyComponentPanel(propertiesView.getComponentFor((Property) aProperty));
                }

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
        String title = getCaptionFor(propertyGroup);
        TitledComponent groupPanel = createTitledComponent(title, propertiesPanel);
        propertiesView.setupPropertyGroupTitledComponent(propertyGroup, groupPanel);

        return groupPanel;
    }

    private JPanel createPropertyComponentPanel(AbstractPropertyComponent component) {
        JPanel componentPanel = new JPanel();

        double[][] layoutSize = new double[][]{
            {PropertyCardsConfig.COLUMN_1_WIDTH, TableLayout.FILL},
            {TableLayout.PREFERRED, PropertyCardsConfig.propertyBorderThickness}
        };

        componentPanel.setLayout(new TableLayout(layoutSize));

        String propertyCaption = getCaptionFor(component.getProperty());

        // label + component
        componentPanel.add(createHeadLabel(propertyCaption), "0 0");
        componentPanel.add(component.getComponent(), "1 0");
        
        // bottom border(s)
        componentPanel.add(createColumn1Border(), "0 1");
        componentPanel.add(createColumn2Border(), "1 1");

        return componentPanel;
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
        MultilineLabel label = new MultilineLabel(text);
        label.setBackground(background);
        Border emptyBorder = new EmptyBorder(PropertyCardsConfig.labelInsets);
        label.setBorder(emptyBorder);
        return label;
    }

}
