package de.hsos.richwps.mb.app.view;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Color;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import layout.TableLayout;

/**
 * The "about" dialog.
 *
 * @author dziegenh
 */
public class AboutDialog extends MbDialog {

    private final Window parent;

    public AboutDialog(Window parent) {
        super(parent, AppConstants.ABOUT_DIALOG_TITLE);

        if (parent == null) {
            throw new IllegalArgumentException("AboutDialog parent can not be null");
        }

        this.parent = parent;

        setSize(AppConstants.ABOUT_DIALOG_SIZE);
        setResizable(false);
        setModal(true);

        addComponents();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
    }

    // TODO add "about" content
    private void addComponents() {
        Container content = getContentPane();
        if (content instanceof JComponent) {
            ((JComponent) content).setBorder(null);
        }

        double F = TableLayout.FILL;
        double P = TableLayout.PREFERRED;

        JButton closeButton = new JButton();
        closeButton.setAction(new AbstractAction() {
            @Override
            public Object getValue(String key) {
                if (key.equals(NAME)) {
                    return AppConstants.ABOUT_DIALOG_BTN_CLOSE;
                }
                return super.getValue(key);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        ImageIcon aboutImage = new ImageIcon(AppConstants.ABOUT_IMAGE);

        JLabel aboutTextLabel = new JLabel(AppConstants.ABOUT_DIALOG_TEXT);
        aboutTextLabel.setFont(aboutTextLabel.getFont().deriveFont(10f));

        double borderWidth = 5;
        double[][] layoutSize = new double[][]{
            {borderWidth, F, P, F, borderWidth},
            {P, borderWidth, P, F, P, borderWidth}
        };

        content.setLayout(new TableLayout(layoutSize));
        content.add(new JLabel(aboutImage), "0 0 4 0");
        content.add(aboutTextLabel, "1 2 3 2");
        content.add(closeButton, "2 4");

        content.setBackground(Color.WHITE);
    }

}
