package de.hsos.richwps.mb.propertiesView;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.UIManager;

/**
 * GUI constants for property cards.
 *
 * @author dziegenh
 */
public class PropertyCardsConfig {

    /**
     * Insets for property titles, property names and property values.
     */
    public final static Insets labelInsets = new Insets(2, 5, 2, 5);

    /**
     * fixed width of first column ("header").
     */
    public final static int COLUMN_1_WIDTH = 56;

    /**
     * height of titles ("process", "inputs" etc).
     */
    public final static int titleHeight = 20;

    /**
     * Font color of a property title.
     */
    public final static Color propertyTitleFgColor = Color.WHITE;

    /**
     * Gradient color 1 of a property title.
     */
    public final static Color propertyTitleBgColor1 = Color.LIGHT_GRAY;

    /**
     * Gradient color 2 of a property title.
     */
    public final static Color propertyTitleBgColor2 = Color.LIGHT_GRAY.darker();
    /**
     * Color of property value fields.
     */
    public final static Color bodyLabelBgColor = Color.WHITE;

    /**
     * Color of property name fields.
     */
    public final static Color headLabelBgColor = UIManager.getColor("Panel.background");

    /**
     * property row: height of the bottom border.
     */
    public final static double propertyBorderThickness = 1;

    /**
     * Font size of property fields.
     */
    public final static float propertyFieldFontSize = 11.0F;
}
