/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;

/**
 * Renderer for list cells representing a ComplexDataTypeFormat.
 * @author dziegenh
 */
public class ComplexDataTypeFormatCellRenderer extends DefaultListCellRenderer {

    protected ComplexDataTypeFormat selectedFormat;

    public ComplexDataTypeFormatCellRenderer() {
    }

    public ComplexDataTypeFormatCellRenderer(ComplexDataTypeFormat selectedFormat) {
        this.selectedFormat = selectedFormat;
    }

    public ComplexDataTypeFormat getSelectedFormat() {
        return selectedFormat;
    }

    public void setSelectedFormat(ComplexDataTypeFormat selectedFormat) {
        this.selectedFormat = selectedFormat;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = null;

        // no value => empty cell
        if (null == value) {
            String text = "<html><br/>(none)<br/><br/>";
            if (index < (list.getModel().getSize() - 1)) {
                text += "<hr>";
            }
            text += "</html>";

            label = new JLabelWithBackground(text);

        // default => show format details
        } else if (null != value && value instanceof ComplexDataTypeFormat) {
            ComplexDataTypeFormat cdtValue = (ComplexDataTypeFormat) value;

            String text = "<html>" + cdtValue.getMimeType() + "<br/>Schema: " + cdtValue.getSchema() + "<br/>Encoding: " + cdtValue.getEncoding();
            if (index < (list.getModel().getSize() - 1)) {
                text += "<hr>";
            }
            text += "</html>";

            label = new JLabelWithBackground(text);
        }

        // selected cell
        if (null != label) {

            // temporarily selected cell
            if(isSelected) {
                label.setForeground(AppConstants.SELECTION_FG_COLOR);
                label.setBackground(AppConstants.SELECTION_BG_COLOR);
            } else {
                label.setBackground(new Color(0,0,0,0));
            }
            
            // cell with selected list item
            if(null != selectedFormat && selectedFormat.equals(value)) {
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            }
            
            label.setBorder(new EmptyBorder(2, 2, 2, 2));
            return label;
        }

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
