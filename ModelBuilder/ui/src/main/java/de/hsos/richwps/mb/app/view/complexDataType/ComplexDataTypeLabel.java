package de.hsos.richwps.mb.app.view.complexDataType;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescriptionChangeListener;
import de.hsos.richwps.mb.propertiesView.PropertyCardsConfig;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 * This label represents sums up the supported formats and the default format of
 * a DataTypeDescriptionComplex. If it is set to be editable, an edit button is
 * added, which opens a dialog for selecting the supported and default formats.
 *
 *
 * @author dziegenh
 */
public class ComplexDataTypeLabel extends JPanel implements Serializable {

    private final JButton editButton;
    private final JLabel formatLabel;
    private List<ComplexDataTypeFormat> formats;
    private final Dimension editButtonSize = new Dimension(18, 18);

    private List<IDataTypeDescriptionChangeListener> selectionListeners;
    private DataTypeDescriptionComplex dataTypeDescription;

    public ComplexDataTypeLabel() {
        this.editButton = null;
        this.formatLabel = null;
    }

    public ComplexDataTypeLabel(final Window parent, List<ComplexDataTypeFormat> formats) {
        super();

        this.formats = formats;

        formatLabel = new JLabel("");
        formatLabel.setBorder(new EmptyBorder(PropertyCardsConfig.labelInsets));

        editButton = new JButton(UIManager.getIcon(AppConstants.ICON_EDIT_KEY));
        editButton.setPreferredSize(editButtonSize);
        editButton.setToolTipText(AppConstants.PROPERTIES_BTN_EDIT_FORMAT_TTT);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ComplexDataTypeFrame selectFormatFrame = new ComplexDataTypeFrame(parent, getFormats(), getDataTypeDescription());
                Point btnLocation = editButton.getLocationOnScreen();
                btnLocation.y += editButton.getHeight();
                btnLocation.x += editButton.getWidth();
                selectFormatFrame.setLocation(btnLocation);

                // get selected formats when selection window closes
                selectFormatFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        DataTypeDescriptionComplex exportDatatype = selectFormatFrame.getExportDatatype();

                        // null indicates the selection was cancelled
                        // (and that changes should not be saved)
                        if (null != exportDatatype) {
                            setDatatypeDescription(exportDatatype);

                            for (IDataTypeDescriptionChangeListener listener : getSelectionListeners()) {
                                listener.dataTypeDescriptionChanged(exportDatatype);
                            }
                        }
                    }

                });

                selectFormatFrame.setVisible(true);
            }
        });

        double[][] layout = new double[][]{
            {TableLayout.FILL, TableLayout.PREFERRED},
            {TableLayout.PREFERRED},};
        setLayout(new TableLayout(layout));

        // use tooblar to get it's look and feel for the button
        JToolBar bar = new JToolBar();
        bar.add(editButton);
        bar.setFloatable(false);
        bar.setBorder(null);

        add(formatLabel, "0 0");
        add(bar, "1 0");
    }

    public void addSelectionListener(IDataTypeDescriptionChangeListener listener) {
        getSelectionListeners().add(listener);
    }

    public void removeSelectionListener(IDataTypeDescriptionChangeListener listener) {
        getSelectionListeners().remove(listener);
    }

    private List<IDataTypeDescriptionChangeListener> getSelectionListeners() {
        if (null == selectionListeners) {
            selectionListeners = new LinkedList<>();
        }
        return selectionListeners;

    }

    private List<ComplexDataTypeFormat> getFormats() {
        return formats;
    }

    public void setEditable(boolean editable) {
        editButton.setEnabled(editable);
        editButton.setVisible(editable);

        if (editable) {
            editButton.setPreferredSize(editButtonSize);
        } else {
            editButton.setPreferredSize(new Dimension(0, 0));
        }
    }

    public DataTypeDescriptionComplex getDataTypeDescription() {
        return dataTypeDescription;
    }

    public void setDatatypeDescription(DataTypeDescriptionComplex dataTypeDescription) {
        this.dataTypeDescription = dataTypeDescription;

        formatLabel.setText("-");

        if (null != dataTypeDescription) {
            List<ComplexDataTypeFormat> formats1 = dataTypeDescription.getFormats();
            if (null != formats1 && formats1.size() > 0) {

                StringBuilder sb = new StringBuilder(200);
                sb.append("<html>");

                if (formats1.size() > 1) {
                    sb.append("<i>").append(formats1.size()).append(" formats supported. Default:</i>");
                }

                // add detailed default format data
                if (dataTypeDescription.getDefaultFormat() != null) {
                    if (formats1.size() > 1) {
                        sb.append("<br>");
                    }
                    sb.append(dataTypeDescription.getDefaultFormat().getToolTipText().replaceAll("<html>", "").replaceAll("</html>", "<br>"));
                } else {
                    sb.append("<b style='color:red;'> none</b>");
                }

                sb.append("</html>");

                formatLabel.setText(sb.toString());
            }
        }
    }

}
