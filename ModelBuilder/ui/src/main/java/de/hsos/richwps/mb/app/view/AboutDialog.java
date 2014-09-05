package de.hsos.richwps.mb.app.view;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Color;
import java.awt.Container;
import java.awt.Window;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import layout.TableLayout;

/**
 * The "about" dialog.
 *
 * @author dziegenh
 */
public class AboutDialog extends MbDialog {

    public AboutDialog(Window parent) {
        super(parent, AppConstants.ABOUT_DIALOG_TITLE, MB_DIALOG_BUTTONS.CLOSE);

        if (parent == null) {
            throw new IllegalArgumentException("AboutDialog parent can not be null");
        }

        setSize(AppConstants.ABOUT_DIALOG_SIZE);
        setResizable(false);
        setModal(true);

        addComponents();
    }

    private void addComponents() {
        Container content = getContentPane();
        if (content instanceof JComponent) {
            ((JComponent) content).setBorder(null);
        }

        double F = TableLayout.FILL;
        double P = TableLayout.PREFERRED;

        ImageIcon aboutImage = new ImageIcon(AppConstants.ABOUT_IMAGE);

        JLabel aboutTextLabel = new JLabel(AppConstants.ABOUT_DIALOG_TEXT);
        aboutTextLabel.setFont(aboutTextLabel.getFont().deriveFont(10f));

        double borderWidth = 5;
        double[][] layoutSize = new double[][]{
            {borderWidth, F, borderWidth},
            {P, borderWidth, P, F, borderWidth}
        };

        content.setLayout(new TableLayout(layoutSize));
        content.add(new JLabel(aboutImage), "0 0 2 0");
        content.add(aboutTextLabel, "1 2 1 2");

        content.setBackground(Color.WHITE);
        setBackground(Color.WHITE);
    }

}
