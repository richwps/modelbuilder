package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.IFormatSelectionListener;
import de.hsos.richwps.mb.propertiesView.PropertyCardsConfig;
import de.hsos.richwps.mb.ui.MbDialog;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 * Frame containing a list for format selection.
 *
 * @author dziegenh
 */
class SelectFormatFrame extends MbDialog {

    private final JList formatsList;
    private List<IFormatSelectionListener> selectionListeners;

    SelectFormatFrame(Window parent, List<ComplexDataTypeFormat> formats, List<ComplexDataTypeFormat> selected) {
        super(parent, "Select formats", MbDialog.BTN_ID_OK | MbDialog.BTN_ID_CANCEL);

        formatsList = new JList(formats.toArray());
        formatsList.setCellRenderer(new ComplexDataTypeFormatCellRenderer(selected));

        // accept an entry on double click
        formatsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // double clicked an entry
                if (2 == e.getClickCount() && e.getButton() == MouseEvent.BUTTON1) {
                    // store selected format for export and close window
                    handleDialogButton(BTN_ID_OK);
                }
            }
        });

        // reload selection if available
        if (null != selected) {
            for (ComplexDataTypeFormat format : selected) {
                int indexOf = formats.indexOf(format);
                formatsList.getSelectionModel().addSelectionInterval(indexOf, indexOf);
            }
        }

        getContentPane().setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        getContentPane().add(new JScrollPane(formatsList), "0 0");

        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Empty if selection is clear, null if selection was cancelled.
     */
    private List<ComplexDataTypeFormat> exportFormats = null;

    @Override
    protected void handleDialogButton(int buttonId) {

        // "ok" -> store selected formats for export
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {
            this.exportFormats = formatsList.getSelectedValuesList();

            // else -> no export
        } else if (isTheDialogButton(buttonId, MbDialog.BTN_ID_CANCEL)) {
            this.exportFormats = null;
        }

        super.handleDialogButton(buttonId);
    }

    /**
     * Gets the formats which are selected at the moment the window is closed.
     *
     * @return
     */
    List<ComplexDataTypeFormat> getSelectedFormats() {
        return exportFormats;
    }

    /**
     * if visible: Adjust window size + location to its content.
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            Dimension cSize = formatsList.getPreferredSize();
            Dimension size = getSize();
            size.width = (int) (cSize.getWidth() + 50);
            size.height = 500;
            setSize(size);
            UiHelper.centerToWindow(this, parent);
        }

        super.setVisible(visible);
    }

    void addSelectionListener(IFormatSelectionListener listener) {
        getSelectionListeners().add(listener);
    }

    private List<IFormatSelectionListener> getSelectionListeners() {
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

    private final JButton editButton;
    private final MultilineLabel formatLabel;
    private List<ComplexDataTypeFormat> formats;
    private final Dimension editButtonSize = new Dimension(18, 18);
    private List<ComplexDataTypeFormat> selectedFormats;

    private List<IFormatSelectionListener> selectionListeners;

    public ComplexDataTypeFormatLabel(final Window parent, List<ComplexDataTypeFormat> formats) {
        super();

        this.formats = formats;

        formatLabel = new MultilineLabel("");
        formatLabel.setBorder(new EmptyBorder(PropertyCardsConfig.labelInsets));

        editButton = new JButton(UIManager.getIcon(AppConstants.ICON_EDIT_KEY));
        editButton.setPreferredSize(editButtonSize);
        editButton.setToolTipText(AppConstants.PROPERTIES_BTN_EDIT_FORMAT_TTT);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final SelectFormatFrame selectFormatFrame = new SelectFormatFrame(parent, getFormats(), getSelectedFormats());
                Point btnLocation = editButton.getLocationOnScreen();
                btnLocation.y += editButton.getHeight();
                btnLocation.x += editButton.getWidth();
                selectFormatFrame.setLocation(btnLocation);

                // get selected formats when selection window closes
                selectFormatFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        List<ComplexDataTypeFormat> selectedFormats = selectFormatFrame.getSelectedFormats();

                        // null indicates the selection was cancelled
                        // (and that changes should not be saved)
                        if (null != selectedFormats) {
                            setSelectedFormats(selectedFormats);

                            for (IFormatSelectionListener listener : getSelectionListeners()) {
                                listener.formatSelected(selectedFormats);
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

    private List<ComplexDataTypeFormat> getSelectedFormats() {
        return selectedFormats;
    }

    public void addSelectionListener(IFormatSelectionListener listener) {
        getSelectionListeners().add(listener);
    }

    public void removeSelectionListener(IFormatSelectionListener listener) {
        getSelectionListeners().remove(listener);
    }

    private List<IFormatSelectionListener> getSelectionListeners() {
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

    public void setSelectedFormats(List<ComplexDataTypeFormat> formats) {
        this.selectedFormats = formats;

        if (null != formats) {
            formatLabel.setText(formats.size() + " selected");
        } else {
            formatLabel.setText("-");
        }
    }

}
