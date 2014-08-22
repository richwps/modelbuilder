/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import layout.TableLayout;
import org.apache.commons.lang3.SystemUtils;

interface ISelectionListener {

    void formatSelected(ComplexDataTypeFormat format);
}

class SelectFormatFrame extends JDialog {

    private final JComboBox comboBox;
    private List<ISelectionListener> selectionListeners;

    SelectFormatFrame(List<ComplexDataTypeFormat> formats) {
        super((Frame) null, "", true);
        comboBox = new JComboBox(formats.toArray());
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (null != value && value instanceof ComplexDataTypeFormat) {
                    ComplexDataTypeFormat cdtValue = (ComplexDataTypeFormat) value;
                    JLabel label = new JLabel();

                    String text = "<html>" + cdtValue.getMimeType() + "<br/>Schema: " + cdtValue.getSchema() + "<br/>Encoding: " + cdtValue.getEncoding();
                    if (index < (list.getModel().getSize() - 1)) {
                        text += "<hr>";
                    }
                    text += "</html>";
                    label.setText(text);

                    return label;
                }

                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

        });

        add(comboBox);
        setResizable(false);

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (ISelectionListener listener : getSelectionListeners()) {
                    listener.formatSelected((ComplexDataTypeFormat) comboBox.getSelectedItem());
                }
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        // adjust window size + location to its content
        Dimension cSize = comboBox.getPreferredSize();
        Dimension size = getSize();
        size.width += cSize.width;
        size.height += cSize.height + 30;
        setSize(size);
        Point location = getLocation();
        location.x -= size.width;
        setLocation(location);

        super.setVisible(b);
    }

    void addSelectionListener(ISelectionListener listener) {
        getSelectionListeners().add(listener);
    }

    private List<ISelectionListener> getSelectionListeners() {
        if (null == selectionListeners) {
            selectionListeners = new LinkedList<>();
        }
        return selectionListeners;

    }

}

/**
 *
 * @author dziegenh
 */
public class ComplexDataTypeFormatLabel extends JPanel {

    private final JButton editButton = new JButton("Edit");
    private final MultilineLabel formatLabel = new MultilineLabel("");
    private List<ComplexDataTypeFormat> formats;

    public ComplexDataTypeFormatLabel(List<ComplexDataTypeFormat> formats) {
        super();

        this.formats = formats;

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Logger.log("choose from " + getFormats().size());
                final SelectFormatFrame selectFormatFrame = new SelectFormatFrame(getFormats());
                Point btnLocation = editButton.getLocationOnScreen();
                btnLocation.y += editButton.getHeight();
                btnLocation.x += editButton.getWidth();
                selectFormatFrame.setLocation(btnLocation);

                selectFormatFrame.addSelectionListener(new ISelectionListener() {
                    @Override
                    public void formatSelected(ComplexDataTypeFormat format) {
                        setComplexDataTypeFormat(format);
                        selectFormatFrame.dispose();
                    }
                });

                selectFormatFrame.setVisible(true);
            }
        });

        double[][] layout = new double[][]{
            {TableLayout.FILL},
            {TableLayout.PREFERRED, TableLayout.PREFERRED}
        };
        setLayout(new TableLayout(layout));

        add(editButton, "0 0");
        add(formatLabel, "0 1");

        editButton.setMaximumSize(editButton.getPreferredSize());
    }

    private List<ComplexDataTypeFormat> getFormats() {
        return formats;
    }

    public void setEditable(boolean editable) {
        editButton.setEnabled(editable);
        editButton.setVisible(editable);

        if (editable) {
            editButton.setPreferredSize(editButton.getMaximumSize());
        } else {
            editButton.setPreferredSize(new Dimension(0, 0));
        }
//        doLayout();
//        invalidate();
    }

    public void setComplexDataTypeFormat(ComplexDataTypeFormat format) {
        String mimeType = "-", encoding = "-", schema = "-";

        String toolTipText = "";
        if (null != format) {
            mimeType = format.getMimeType();
            schema = format.getSchema();
            encoding = format.getEncoding();
            toolTipText = format.getToolTipText();
        }
        formatLabel.setToolTipText(toolTipText);

        String NL = SystemUtils.LINE_SEPARATOR;
//        String text = mimeType + NL + schema + NL + encoding;

        formatLabel.setText(mimeType);
    }

}
