package de.hsos.richwps.mb.app.view.complexDataType;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * Renderer for list cells representing a ComplexDataTypeFormat.
 *
 * @author dziegenh
 */
public class ComplexDataTypeFormatCellRenderer extends DefaultListCellRenderer {

    protected DataTypeDescriptionComplex datatypeDescription;

    protected Icon supportedFormat = UIManager.getIcon(AppConstants.ICON_CHECK_KEY);
    protected Icon defaultFormat = UIManager.getIcon(AppConstants.ICON_CHECK_FAVOURITE_KEY);

    public ComplexDataTypeFormatCellRenderer() {
    }

    public ComplexDataTypeFormatCellRenderer(DataTypeDescriptionComplex datatypeDescription) {
        this.datatypeDescription = datatypeDescription;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = null;

        ComplexDataTypeFormat cdtValue = null;

        // no value => empty cell
        if (null == value) {
            String text = "<html><br/>(none)<br/><br/></html>";
            label = new JLabelWithBackground(text);

            // default => show format details
        } else if (value instanceof ComplexDataTypeFormat) {
            cdtValue = (ComplexDataTypeFormat) value;
            String text = cdtValue.getToolTipText();
            label = new JLabelWithBackground(text);

        }

        if (null != label) {

            Icon labelIcon = null;

            // cell with selected list item
            boolean bothNull = (value == null && value == datatypeDescription);
            boolean oneIsNull = null == value || null == datatypeDescription;
            boolean notNullAndEqual
                    = !oneIsNull && datatypeDescription.getFormats().contains(cdtValue);

            // temporarily selected cell
            if (isSelected) {
                label.setForeground(AppConstants.SELECTION_FG_COLOR);
                label.setBackground(AppConstants.SELECTION_BG_COLOR);

            } else {
                label.setBackground(new Color(0, 0, 0, 0));

                // Highlight previously selected supported format.
                if (bothNull || notNullAndEqual) {
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    label.setForeground(AppConstants.SELECTION_BG_COLOR);
                    label.setToolTipText(AppConstants.COMPLEX_FORMAT_TOOLTIP_SUPPORTEDFORMAT);
                    labelIcon = supportedFormat;
                }

                // Highlight previously selected default format.
                if (notNullAndEqual && cdtValue.equals(datatypeDescription.getDefaultFormat())) {
                    labelIcon = defaultFormat;
                    label.setToolTipText(AppConstants.COMPLEX_FORMAT_TOOLTIP_DEFAULTFORMAT);
                }
            }

            Border outer = new ColorBorder(AppConstants.SELECTION_BG_COLOR, 0, 0, 1, 0);
            Border inner = new EmptyBorder(2, 2, 2, 2);
            label.setBorder(new CompoundBorder(outer, inner));

            label.setIcon(labelIcon);

            return label;
        }

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
