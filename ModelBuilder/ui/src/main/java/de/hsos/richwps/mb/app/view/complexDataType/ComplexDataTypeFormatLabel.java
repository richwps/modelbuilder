package de.hsos.richwps.mb.app.view.complexDataType;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;
import org.apache.commons.lang.Validate;

/**
 *
 * @author dziegenh
 */
public class ComplexDataTypeFormatLabel extends JPanel {

    public static Color fgColor = Color.BLACK;

    protected Icon supportedFormat = UIManager.getIcon(AppConstants.ICON_CHECK_KEY);
    protected Icon defaultFormat = UIManager.getIcon(AppConstants.ICON_FAVOURITE_KEY);
    protected Icon disabledDefaultFormat = UIManager.getIcon(AppConstants.ICON_FAVOURITE_DISABLED_KEY);
    protected Icon deselectedFormat = UIManager.getIcon(AppConstants.ICON_CHECK_DISABLED_KEY);

    protected Color supportedBgColor = new Color(180, 236, 175);
    protected Color defaultBgColor = new Color(255, 234, 170);
    
    protected Color iconLabelBgColor = new Color(1f, 1f, 1f, 0f);
    protected Color iconLabelHighlightBgColor = UiHelper.deriveColor(AppConstants.SELECTION_BG_COLOR, 30);

    protected ComplexDataTypeFormat format;

    protected boolean isSupportedFormat;
    protected boolean isDefaultFormat;

    protected JLabelWithBackground formatLabel;
    private final JLabelWithBackground chooseDefaultLabel;
    private final JLabelWithBackground setSupportedLabel;

    private final List<IDefaultStatusChangeListener> defaultChangeListeners;
    private final List<ISupportedStatusChangeListener> supportChangeListeners;

    public ComplexDataTypeFormatLabel(ComplexDataTypeFormat format, boolean isSupportedFormat, boolean isDefaultFormat) {
        Validate.notNull(format);

        this.format = format;
        this.isSupportedFormat = isSupportedFormat;
        this.isDefaultFormat = isDefaultFormat;

        this.defaultChangeListeners = new LinkedList<>();
        this.supportChangeListeners = new LinkedList<>();

        final EmptyBorder emptyBorder = new EmptyBorder(2, 2, 2, 2);

        // Toggle "supported" status on mouse click
        MouseAdapter defaultAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!isDefaultFormat()) {
                        setIsSupportedFormat(true);
                    }

                    setIsDefaultFormat(!isDefaultFormat());
                    update();

                    fireDefaultStatusChange();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                highlightIconLabel(chooseDefaultLabel, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                highlightIconLabel(chooseDefaultLabel, false);
            }
        };

        // Toggle "supported" status on mouse click
        MouseAdapter supportedAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isSupportedFormat()) {
                        boolean fireChange = isDefaultFormat();
                        setIsDefaultFormat(false);

                        if (fireChange) {
                            fireDefaultStatusChange();
                        }
                    }

                    setIsSupportedFormat(!isSupportedFormat());

                    fireSupportedStatusChange();

                    update();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                highlightIconLabel(setSupportedLabel, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                highlightIconLabel(setSupportedLabel, false);
            }
        };

        chooseDefaultLabel = new JLabelWithBackground();
        chooseDefaultLabel.setBackground(iconLabelBgColor);
        chooseDefaultLabel.setBorder(emptyBorder);
        chooseDefaultLabel.addMouseListener(defaultAdapter);
        chooseDefaultLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setSupportedLabel = new JLabelWithBackground();
        setSupportedLabel.setBackground(new Color(1f, 1f, 1f, 0f));
        setSupportedLabel.setBorder(emptyBorder);
        setSupportedLabel.addMouseListener(supportedAdapter);
        setSupportedLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        formatLabel = new JLabelWithBackground();
        formatLabel.setBackground(new Color(1f, 1f, 1f, 0f));
        formatLabel.setBorder(emptyBorder);

        setForeground(fgColor);
        setBackground(new Color(1f, 1f, 1f, 1f));
        Border outer = new ColorBorder(AppConstants.SELECTION_BG_COLOR, 0, 0, 1, 0);
        setBorder(outer);

        setLayout(new TableLayout(new double[][]{{TableLayout.FILL, 1d, TableLayout.PREFERRED, 1d, TableLayout.PREFERRED}, {TableLayout.PREFERRED}}));
        add(formatLabel, "0 0");
        add(new JLabelWithBackground(AppConstants.SELECTION_BG_COLOR), "1 0");
        add(setSupportedLabel, "2 0");
        add(new JLabelWithBackground(AppConstants.SELECTION_BG_COLOR), "3 0");
        add(chooseDefaultLabel, "4 0");

        update();
    }

    protected void highlightIconLabel(JLabelWithBackground label, boolean highlight) {
        if (highlight) {
            label.setBackground(iconLabelHighlightBgColor);
        } else {
            label.setBackground(iconLabelBgColor);
        }
    }

    private void update() {
        // no value => empty cell
        if (null == format) {
            String text = "<html><br/>(none)<br/><br/></html>";
            formatLabel.setText(text);

            // has value => show format details
        } else {
            String text = format.getToolTipText();
            formatLabel.setText(text);
        }

        Color formatBgColor = this.iconLabelBgColor;
        
        // Highlight selected supported format.
        if (isSupportedFormat) {
            setSupportedLabel.setIcon(supportedFormat);
            setSupportedLabel.setToolTipText(AppConstants.COMPLEX_FORMAT_TOOLTIP_SUPPORTEDFORMAT);
            formatBgColor = this.supportedBgColor;

        } else {
            setSupportedLabel.setIcon(deselectedFormat);
            setSupportedLabel.setToolTipText(AppConstants.COMPLEX_FORMAT_TOOLTIP_SUPPORTEDFORMAT_DESELECTED);

        }

        // Highlight selected default format.
        if (isDefaultFormat) {
            chooseDefaultLabel.setIcon(defaultFormat);
            chooseDefaultLabel.setToolTipText(AppConstants.COMPLEX_FORMAT_TOOLTIP_DEFAULTFORMAT);
            formatBgColor = this.defaultBgColor;

        } else {
            chooseDefaultLabel.setIcon(disabledDefaultFormat);
            chooseDefaultLabel.setToolTipText(AppConstants.COMPLEX_FORMAT_TOOLTIP_DEFAULTFORMAT_DESELECTED);
        }

        this.formatLabel.setBackground(formatBgColor);
    }

    public void setIsSupportedFormat(boolean isSupportedFormat) {
        this.isSupportedFormat = isSupportedFormat;
    }

    public boolean isSupportedFormat() {
        return this.isSupportedFormat;
    }

    public void updateLabel() {
        this.update();
    }

    public ComplexDataTypeFormat getFormat() {
        return format;
    }

    public void setIsDefaultFormat(boolean isDefaultFormat) {
        this.isDefaultFormat = isDefaultFormat;
    }

    protected void fireDefaultStatusChange() {
        for (IDefaultStatusChangeListener listener : this.defaultChangeListeners) {
            listener.defaultStatusChanged(this, isDefaultFormat);
        }
    }

    protected void fireSupportedStatusChange() {
        for (ISupportedStatusChangeListener listener : this.supportChangeListeners) {
            listener.supportedStatusChanged(this, isSupportedFormat);
        }
    }

    public boolean isDefaultFormat() {
        return isDefaultFormat;
    }

    public void addDefaultStatusChangeListener(IDefaultStatusChangeListener listener) {
        if (!this.defaultChangeListeners.contains(listener)) {
            this.defaultChangeListeners.add(listener);
        }
    }

    public void removeDefaultStatusChangeListener(IDefaultStatusChangeListener listener) {
        this.defaultChangeListeners.remove(listener);
    }

    public void addSupporteStatusChangeListener(ISupportedStatusChangeListener listener) {
        if (!this.supportChangeListeners.contains(listener)) {
            this.supportChangeListeners.add(listener);
        }
    }

    public void removeSupportedStatusChangeListener(ISupportedStatusChangeListener listener) {
        this.supportChangeListeners.remove(listener);
    }

}
