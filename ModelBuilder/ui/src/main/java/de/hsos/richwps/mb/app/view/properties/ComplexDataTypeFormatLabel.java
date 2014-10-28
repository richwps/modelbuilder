package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.IFormatSelectionListener;
import de.hsos.richwps.mb.propertiesView.PropertyCardsConfig;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.PopupMenuAdapter;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import layout.TableLayout;
import org.apache.commons.lang3.SystemUtils;

/**
 * Frame containing a combobox for format selection.
 *
 * @author dziegenh
 */
class SelectFormatFrame extends JDialog {

    private final JComboBox comboBox;
    private List<IFormatSelectionListener> selectionListeners;

    SelectFormatFrame(List<ComplexDataTypeFormat> formats, ComplexDataTypeFormat selected) {
        super((Frame) null, "", true);

        comboBox = new JComboBox(formats.toArray());
        comboBox.setRenderer(new ComplexDataTypeFormatCellRenderer(selected));
        comboBox.setSelectedItem(selected);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (IFormatSelectionListener listener : getSelectionListeners()) {
                    listener.formatSelected((ComplexDataTypeFormat) comboBox.getSelectedItem());
                }
            }
        });
        comboBox.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                dispose();
            }
        });

        add(comboBox);

        // close window when user clicks outside
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                dispose();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * if visible: Adjust window size + location to its content.
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            Dimension cSize = comboBox.getPreferredSize();
            Dimension size = getSize();
            size.width += cSize.width;
            size.height += cSize.height + 30;
            setSize(size);
            Point location = getLocation();
            location.x -= size.width;
            setLocation(location);
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
    private ComplexDataTypeFormat format;

    private List<IFormatSelectionListener> selectionListeners;

    public ComplexDataTypeFormatLabel(List<ComplexDataTypeFormat> formats) {
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
                final SelectFormatFrame selectFormatFrame = new SelectFormatFrame(getFormats(), format);
                Point btnLocation = editButton.getLocationOnScreen();
                btnLocation.y += editButton.getHeight();
                btnLocation.x += editButton.getWidth();
                selectFormatFrame.setLocation(btnLocation);

                selectFormatFrame.addSelectionListener(new IFormatSelectionListener() {
                    @Override
                    public void formatSelected(ComplexDataTypeFormat format) {
                        setComplexDataTypeFormat(format);
                        selectFormatFrame.dispose();
                        for (IFormatSelectionListener listener : getSelectionListeners()) {
                            listener.formatSelected(format);
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

    public void setComplexDataTypeFormat(ComplexDataTypeFormat format) {
        this.format = format;

        String mimeType = "-", encoding = "-", schema = "-";

        String toolTipText = "-";
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
