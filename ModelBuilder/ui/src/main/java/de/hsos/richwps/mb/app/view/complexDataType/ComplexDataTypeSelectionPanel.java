package de.hsos.richwps.mb.app.view.complexDataType;

import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.exception.IllegalDefaultFormatException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import layout.TableLayout;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author dziegenh
 */
public class ComplexDataTypeSelectionPanel extends JPanel {

    protected DataTypeDescriptionComplex datatype;
    protected List<ComplexDataTypeFormatLabel> allFormatLabels;

    protected ComplexDataTypeFormatLabel currentDefaultLabel = null;

    private final List<IDefaultStatusChangeListener> defaultChangeListeners;

    public ComplexDataTypeSelectionPanel(List<ComplexDataTypeFormat> allFormats) {
        Validate.notNull(allFormats);

        double[] yLayout = new double[allFormats.size()];
        Arrays.fill(yLayout, TableLayout.PREFERRED);
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, yLayout}));

        this.allFormatLabels = new LinkedList<>();
        this.defaultChangeListeners = new LinkedList<>();

        // update default format when user changes a label's status.
        IDefaultStatusChangeListener defaultStatusListener = new IDefaultStatusChangeListener() {
            @Override
            public void defaultStatusChanged(ComplexDataTypeFormatLabel label, boolean isDefault) {

                // de-select old format label
                if (null != currentDefaultLabel && !label.equals(currentDefaultLabel)) {
                    currentDefaultLabel.setIsDefaultFormat(false);
                    currentDefaultLabel.updateLabel();
                }

                if (isDefault) {
                    currentDefaultLabel = label;
                } else {
                    currentDefaultLabel = null;
                }

                for (IDefaultStatusChangeListener listener : defaultChangeListeners) {
                    listener.defaultStatusChanged(currentDefaultLabel, null != currentDefaultLabel);
                }
            }
        };

        // set a a format as default if it is the first supported format.
        ISupportedStatusChangeListener supportChangeListener = new ISupportedStatusChangeListener() {
            @Override
            public void supportedStatusChanged(ComplexDataTypeFormatLabel label, boolean isSupported) {
                if (null == currentDefaultLabel && isSupported) {
                    currentDefaultLabel = label;
                    label.setIsDefaultFormat(true);
                    label.updateLabel();

                    for (IDefaultStatusChangeListener listener : defaultChangeListeners) {
                        listener.defaultStatusChanged(currentDefaultLabel, true);
                    }
                }
            }
        };

        int y = 0;
        for (ComplexDataTypeFormat aFormat : allFormats) {
            ComplexDataTypeFormatLabel formatLabel = new ComplexDataTypeFormatLabel(aFormat, false, false);

            formatLabel.addDefaultStatusChangeListener(defaultStatusListener);
            formatLabel.addSupporteStatusChangeListener(supportChangeListener);

            allFormatLabels.add(formatLabel);
            add(formatLabel, "0 " + y++);
        }
    }

    public void setDatatype(DataTypeDescriptionComplex datatype) {
        this.datatype = datatype;

        ComplexDataTypeFormat defaultFormat = null;
        List<ComplexDataTypeFormat> supportedFormats = new LinkedList<>();

        if (null != datatype) {
            supportedFormats = datatype.getFormats();
            defaultFormat = datatype.getDefaultFormat();
        }

        for (ComplexDataTypeFormatLabel aLabel : allFormatLabels) {
            ComplexDataTypeFormat aFormat = aLabel.getFormat();

            boolean isSupported = supportedFormats.contains(aFormat);
            boolean isDefault = aFormat.equals(defaultFormat);

            if (isDefault) {
                this.currentDefaultLabel = aLabel;
            }

            aLabel.setIsSupportedFormat(isSupported);
            aLabel.setIsDefaultFormat(isDefault);
            aLabel.updateLabel();
        }
    }

    public DataTypeDescriptionComplex getDatatype() throws IllegalDefaultFormatException {
        updateDatatypeUsingLabelValues();
        return datatype;
    }

    protected void updateDatatypeUsingLabelValues() throws IllegalDefaultFormatException {
        List<ComplexDataTypeFormat> supportedFormats = new LinkedList<>();

        for (ComplexDataTypeFormatLabel aLabel : allFormatLabels) {
            if (aLabel.isSupportedFormat()) {
                supportedFormats.add(aLabel.getFormat());
            }
        }

        DataTypeDescriptionComplex datatype = new DataTypeDescriptionComplex(supportedFormats);
        if (null != currentDefaultLabel) {
            datatype.setDefaultFormat(currentDefaultLabel.getFormat());
        }

        this.datatype = datatype;
    }

    public void addDefaultStatusChangeListener(IDefaultStatusChangeListener listener) {
        if (!this.defaultChangeListeners.contains(listener)) {
            this.defaultChangeListeners.add(listener);
        }
    }

    public void removeDefaultStatusChangeListener(IDefaultStatusChangeListener listener) {
        this.defaultChangeListeners.remove(listener);
    }

}
