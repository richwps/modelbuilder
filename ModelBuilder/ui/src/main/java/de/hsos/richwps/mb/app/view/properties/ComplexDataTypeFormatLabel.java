package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescriptionChangeListener;
import de.hsos.richwps.mb.exception.IllegalDefaultFormatException;
import de.hsos.richwps.mb.propertiesView.PropertyCardsConfig;
import de.hsos.richwps.mb.ui.MbDialog;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.CardLayout;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;

/**
 * Frame containing a list for format selection.
 *
 * @author dziegenh
 */
class SelectFormatFrame extends MbDialog {

    private JList formatsList;
    private final JPanel card2;
    private int currentCard;
    private final CardLayout cardLayout;
    private JComboBox<ComplexDataTypeFormat> selectDefaultComboBox;

    /**
     * Empty if nothing selected, null if selection was cancelled.
     */
    private DataTypeDescriptionComplex exportDatatype = null;

    SelectFormatFrame(Window parent, List<ComplexDataTypeFormat> formats, DataTypeDescriptionComplex datatypeDescription) {
        super(parent, "Select formats for complex datatype", BTN_ID_OK | BTN_ID_CANCEL | BTN_ID_NEXT | BTN_ID_BACK);

        cardLayout = new CardLayout();
        getContentPane().setLayout(cardLayout);

        createFormatsList(formats, datatypeDescription);

        JPanel card1 = new JPanel();
        card1.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        card1.add(new JScrollPane(formatsList), "0 0");

        card2 = new JPanel();
        card2.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL}}));

        getContentPane().add(card1, "0");
        getContentPane().add(card2, "1");

        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (null == datatypeDescription) {
            updateButtonStatus();
        } else {
            updateButtonStatus(datatypeDescription.getDefaultFormat());
        }

    }

    private void updateButtonStatus() {
        updateButtonStatus(null);
    }

    private void updateButtonStatus(ComplexDataTypeFormat defaultFormat) {

        // remove individual tool tip texts.
        getDialogButton(BTN_ID_OK).setToolTipText(null);

        switch (currentCard) {

            // first step: select supported formats 
            case 0:
                int numSelected = formatsList.getSelectedIndices().length;

                // is first step -> no backwards navigation
                getDialogButton(BTN_ID_BACK).setEnabled(false);

                // enable next step (=chose default format) if multiple formats are selected
                getDialogButton(BTN_ID_NEXT).setEnabled(numSelected > 1);

                // enable ok button if just one format is selected (which then is the default)
                boolean enableOk = (numSelected == 1);  //|| (null != defaultFormat); // or a default type is selected
                getDialogButton(BTN_ID_OK).setEnabled(enableOk);

                break;

            // second step: select default format from supported formats (if necessary)
            case 1:
                // previous step can be accessed with back button
                getDialogButton(BTN_ID_BACK).setEnabled(true);

                // no further steps -> disable next button
                getDialogButton(BTN_ID_NEXT).setEnabled(false);

                // enable ok button as one ComboBox entry is always selected
                getDialogButton(BTN_ID_OK).setEnabled(true);
                break;
        }

    }

    private void showNextCard() {
        currentCard++;
        cardLayout.show(getContentPane(), "" + currentCard);
        updateButtonStatus();
    }

    private void showPreviousCard() {
        if (currentCard > 0) {
            currentCard--;
            cardLayout.show(getContentPane(), "" + currentCard);
            updateButtonStatus();
        }
    }

    @Override
    protected void handleDialogButton(int buttonId) {

        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_BACK)) {
            if (1 == currentCard) {
                showPreviousCard();
            }
        }

        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_NEXT)) {
            if (0 == currentCard) {

                List<ComplexDataTypeFormat> formats = formatsList.getSelectedValuesList();
                this.exportDatatype = new DataTypeDescriptionComplex(formats);

                ComplexDataTypeFormat[] formatsArray = new ComplexDataTypeFormat[0];
                formatsArray = formats.toArray(formatsArray);

                selectDefaultComboBox = new JComboBox<>(formatsArray);
                selectDefaultComboBox.setRenderer(new ComplexDataTypeFormatCellRenderer());
                card2.removeAll();
                card2.add(new JLabel("Please choose the default format:"), "0 0");
                card2.add(selectDefaultComboBox, "0 1");

                showNextCard();
            }
        }

        // "ok" -> store selected formats for export
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {

            // one of multiple formats was selected as default format
            if (1 == currentCard) {

                try {
                    Object defaultFormat = selectDefaultComboBox.getSelectedItem();
                    this.exportDatatype.setDefaultFormat((ComplexDataTypeFormat) defaultFormat);

                } // else -> no export
                catch (IllegalDefaultFormatException ex) {
                    Logger.log(ex);
                    JOptionPane.showMessageDialog(parent, ex.getMessage(), "An error occured", JOptionPane.ERROR_MESSAGE);
                }

            } else if (0 == currentCard) {
                try {
                    // only one supported format -> is automatically default
                    List<ComplexDataTypeFormat> formats = formatsList.getSelectedValuesList();
                    this.exportDatatype = new DataTypeDescriptionComplex(formats);
                    this.exportDatatype.setDefaultFormat(formats.get(0));
                } catch (IllegalDefaultFormatException ex) {
                    Logger.log("the case which could not be..." + ex);
                }
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
            Dimension cSize = formatsList.getPreferredSize();
            Dimension size = getSize();
            size.width = (int) (cSize.getWidth() + 50);
            size.height = 500;
            setSize(size);
            UiHelper.centerToWindow(this, parent);

            // reset components
            this.currentCard = 0;
            this.card2.removeAll();
            this.exportDatatype = null;
        }

        super.setVisible(visible);
    }

    private void createFormatsList(List<ComplexDataTypeFormat> formats, DataTypeDescriptionComplex datatypeDescription) {
        formatsList = new JList(formats.toArray());
        formatsList.setCellRenderer(new ComplexDataTypeFormatCellRenderer(datatypeDescription));

        // accept an entry on double click
        formatsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // double clicked an entry with left mouse button
                if (2 == e.getClickCount() && e.getButton() == MouseEvent.BUTTON1) {
                    // store selected format for export and close window
                    handleDialogButton(BTN_ID_OK);
                }
            }
        });

        // reload selection if available
        if (null != datatypeDescription) {
            for (ComplexDataTypeFormat format : datatypeDescription.getFormats()) {
                int indexOf = formats.indexOf(format);
                formatsList.getSelectionModel().addSelectionInterval(indexOf, indexOf);
            }
        }

        // update dialog button status when selection changes
        formatsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateButtonStatus();
            }
        });

    }

}

/**
 *
 * @author dziegenh
 */
public class ComplexDataTypeFormatLabel extends JPanel {

    private final JButton editButton;
    private final JLabel formatLabel;
    private List<ComplexDataTypeFormat> formats;
    private final Dimension editButtonSize = new Dimension(18, 18);

    private List<IDataTypeDescriptionChangeListener> selectionListeners;
    private DataTypeDescriptionComplex dataTypeDescription;

    public ComplexDataTypeFormatLabel(final Window parent, List<ComplexDataTypeFormat> formats) {
        super();

        this.formats = formats;

        formatLabel = new JLabel(""); //new MultilineLabel("");
        formatLabel.setBorder(new EmptyBorder(PropertyCardsConfig.labelInsets));

        editButton = new JButton(UIManager.getIcon(AppConstants.ICON_EDIT_KEY));
        editButton.setPreferredSize(editButtonSize);
        editButton.setToolTipText(AppConstants.PROPERTIES_BTN_EDIT_FORMAT_TTT);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final SelectFormatFrame selectFormatFrame = new SelectFormatFrame(parent, getFormats(), getDataTypeDescription());
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
                    sb.append("<i>").append(formats1.size()).append(" formats supported. Default:</i><br>");
                }

                // add detailed default format data
                sb.append(dataTypeDescription.getDefaultFormat().getToolTipText().replaceAll("<html>", "").replaceAll("</html>", "<br>"));
                sb.append("</html>");

                formatLabel.setText(sb.toString());
            }
        }
    }

}
