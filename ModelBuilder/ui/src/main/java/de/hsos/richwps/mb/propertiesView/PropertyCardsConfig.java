/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.UIManager;

/**
 *
 * @author dziegenh
 */
public class PropertyCardsConfig {
    public final static Insets labelInsets = new Insets(2, 5, 2, 5);
    public final static  Color propertyTitleFgColor = Color.WHITE;
    public final static int COLUMN_1_WIDTH = 56; // fixed width of first column ("header")
    public final static int titleHeight = 20; // height of titles ("process", "inputs" etc.)
    public final static Color portBorderColor = Color.LIGHT_GRAY.darker();
    public final static Color propertyTitleBgColor2 = Color.LIGHT_GRAY.darker();
    public final static Color propertyTitleBgColor1 = Color.LIGHT_GRAY;
    public final static Color bodyLabelBgColor = Color.WHITE;
    public final static Color headLabelBgColor = UIManager.getColor("Panel.background");
    public final static double propertyBorderThickness = 1; // property row: height of the bottom border
    public final static float propertyFieldFontSize = 10.0F;

}
