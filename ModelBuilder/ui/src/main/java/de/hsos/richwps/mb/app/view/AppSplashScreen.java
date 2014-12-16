package de.hsos.richwps.mb.app.view;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import layout.TableLayout;

/**
 * Splash screen which is shown before the ModelBuilder GUI is ready.
 *
 * @author dziegenh
 */
public class AppSplashScreen extends JWindow {

    private final JLabel msgLabel;
    private final JLabel progLabel;
    private final JLabel splashLabel;

    public AppSplashScreen() {
        // Message Label
        msgLabel = new JLabel();
        msgLabel.setFont(msgLabel.getFont().deriveFont(Font.ITALIC));
        msgLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        int msgLabelHeight = 20;

        // Progress bar
        progLabel = new JLabel(" ") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Rectangle bounds = getBounds();
                g.setColor(getBackground());
                g.fillRect(0, 0, bounds.width, bounds.height);
            }
        };
        int progLabelHeight = 2;

        // Splash image
        Icon splashImage = new ImageIcon(AppConstants.SPLASH_BG);
        splashLabel = new JLabelWithBackground(splashImage, "", SwingConstants.CENTER);
        splashLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        // Main Layout
        double P = TableLayout.PREFERRED;
        setLayout(new TableLayout(new double[][]{{P}, {P, progLabelHeight, msgLabelHeight}}));
        getContentPane().add(splashLabel, "0 0");
        getContentPane().add(progLabel, "0 1");
        getContentPane().add(msgLabel, "0 2");

        // Window position and size
        int w = splashImage.getIconWidth() + 2;
        int h = splashImage.getIconHeight();
        Point center = UiHelper.getFirstScreenCenter();
        int x = center.x - w / 2;
        int y = center.y - h / 2;
        setBounds(x, y, w, h + msgLabelHeight);

        // Show SplashScreen
        showProgess(0);
        setVisible(true);
    }

    /**
     * Outputs a text message on the splash screen.
     *
     * @param message text to output.
     */
    public void showMessage(String message) {
        msgLabel.setText(message);
    }

    /**
     * Updates the progress bar.
     *
     * @param percent must be between 0 and 100
     */
    public void showProgess(int percent) {
        // Ensure limits
        percent = Math.max(0, percent);
        percent = Math.min(100, percent);

        double p = percent / 100.;

        int r, g;
        if (p < .5) {
            r = 255;
            g = (int) (255 * 2 * p);
        } else {
            r = 255 - (int) (255 * 2 * (p - .5));
            g = 255;
        }
        Color color = new Color(r, g, 0);
        progLabel.setBackground(color);
        splashLabel.setBackground(new Color(r, g, 0));
        
        // TODO remove after christmas...
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * Outputs a text message on the splash screen and updates the progress bar.
     *
     * @param msg text to output
     * @param progress must be between 0 and 100
     */
    public void showMessageAndProgress(String msg, int progress) {
        showMessage(msg);
        showProgess(progress);
    }
}
