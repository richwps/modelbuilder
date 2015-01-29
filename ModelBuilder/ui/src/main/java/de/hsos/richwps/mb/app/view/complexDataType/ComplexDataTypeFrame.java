package de.hsos.richwps.mb.app.view.complexDataType;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.exception.IllegalDefaultFormatException;
import de.hsos.richwps.mb.ui.MbDialog;
import static de.hsos.richwps.mb.ui.MbDialog.BTN_ID_CANCEL;
import static de.hsos.richwps.mb.ui.MbDialog.BTN_ID_OK;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import layout.TableLayout;

/**
 * Frame containing a list for format selection.
 *
 * @author dziegenh
 */
class ComplexDataTypeFrame extends MbDialog {

//    private JList formatsList;
    private ComplexDataTypeSelectionPanel formatsList;

    /**
     * Empty if nothing selected, null if selection was cancelled.
     */
    private DataTypeDescriptionComplex exportDatatype = null;

    ComplexDataTypeFrame(Window parent, List<ComplexDataTypeFormat> formats, DataTypeDescriptionComplex datatypeDescription) {
        super(parent, "Select formats for complex datatype", BTN_ID_OK | BTN_ID_CANCEL);

        formatsList = new ComplexDataTypeSelectionPanel(formats);
        formatsList.setDatatype(datatypeDescription);
        formatsList.addDefaultStatusChangeListener(new IDefaultStatusChangeListener() {

            @Override
            public void defaultStatusChanged(ComplexDataTypeFormatLabel label, boolean isDefault) {
                getDialogButton(BTN_ID_OK).setEnabled(isDefault);
            }
        });

        getContentPane().setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {430}}));
        getContentPane().add(new JScrollPane(formatsList), "0 0");

        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getDialogButton(BTN_ID_OK).setEnabled(null != datatypeDescription);
    }

    @Override
    protected void handleDialogButton(int buttonId) {

        // "ok" -> store selected formats for export
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {

            // one of multiple formats was selected as default format
            try {
                this.exportDatatype = formatsList.getDatatype();

            } // else -> no export
            catch (IllegalDefaultFormatException ex) {
                Logger.log(ex);
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "An error occured", JOptionPane.ERROR_MESSAGE);
            }

        } else if (isTheDialogButton(buttonId, MbDialog.BTN_ID_CANCEL)) {
            this.exportDatatype = null;
        }

        super.handleDialogButton(buttonId);
    }

    /**
     * Gets the configured datatype as it is the window is closed.
     *
     * @return
     */
    public DataTypeDescriptionComplex getExportDatatype() {
        return exportDatatype;
    }

    /**
     * if visible: Adjust window size + location to its content.
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
//            Dimension cSize = formatsList.getPreferredSize();
            Dimension size = getSize();
            size.width = 600;
            size.height = 500;
            setSize(size);
            UiHelper.centerToWindow(this, parent);

            // reset components
            this.exportDatatype = null;
        }

        super.setVisible(visible);
    }
}
