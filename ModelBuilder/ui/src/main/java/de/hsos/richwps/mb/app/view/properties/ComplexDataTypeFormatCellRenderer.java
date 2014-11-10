package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * Renderer for list cells representing a ComplexDataTypeFormat.
 *
 * @author dziegenh
 */
public class ComplexDataTypeFormatCellRenderer extends DefaultListCellRenderer {

    protected List<ComplexDataTypeFormat> selectedFormats;

    public ComplexDataTypeFormatCellRenderer() {
    }

    public ComplexDataTypeFormatCellRenderer(List<ComplexDataTypeFormat> selectedFormats) {
        this.selectedFormats = selectedFormats;
    }

    public List<ComplexDataTypeFormat> getSelectedFormat() {
        return selectedFormats;
    }

    public void setSelectedFormats(List<ComplexDataTypeFormat> selectedFormats) {
        this.selectedFormats = selectedFormats;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = null;

        // no value => empty cell
        if (null == value) {
            String text = "<html><br/>(none)<br/><br/></html>";
            label = new JLabelWithBackground(text);

            // default => show format details
        } else if (null != value && value instanceof ComplexDataTypeFormat) {
            ComplexDataTypeFormat cdtValue = (ComplexDataTypeFormat) value;
            String text = "<html><b>"
                    + ComplexDataTypeFormat.getValueForViews(cdtValue.getMimeType())
                    + "</b><br/>&nbsp;&nbsp;Schema: "
                    + ComplexDataTypeFormat.getValueForViews(cdtValue.getSchema())
                    + "<br/>&nbsp;&nbsp;Encoding: "
                    + ComplexDataTypeFormat.getValueForViews(cdtValue.getEncoding())
                    + "</html>";
            label = new JLabelWithBackground(text);

        }

        if (null != label) {

            // temporarily selected cell
            if (isSelected) {
                label.setForeground(AppConstants.SELECTION_FG_COLOR);
                label.setBackground(AppConstants.SELECTION_BG_COLOR);

            } else {
                label.setBackground(new Color(0, 0, 0, 0));

                // cell with selected list item
                boolean bothNull = (value == null && value == selectedFormats);
                boolean notNullAndEqual = (null != selectedFormats && selectedFormats.equals(value));
                if (bothNull || notNullAndEqual) {
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    label.setForeground(AppConstants.SELECTION_BG_COLOR);
                }
            }

            Border outer = new ColorBorder(AppConstants.SELECTION_BG_COLOR, 0, 0, 1, 0);
            Border inner = new EmptyBorder(2, 2, 2, 2);
            label.setBorder(new CompoundBorder(outer, inner));
            return label;
        }

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
