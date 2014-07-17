/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Color;
import java.awt.Point;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 *
 * @author dziegenh
 */
public class AppSplashScreen extends JWindow {

    public AppSplashScreen() {
        Icon splashImage = new ImageIcon(AppConstants.SPLASH_BG);
        JLabel splashLabel = new JLabel("", splashImage, SwingConstants.CENTER);
        splashLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        getContentPane().add(splashLabel);

        int w = splashImage.getIconWidth();
        int h = splashImage.getIconHeight();

        Point center = UiHelper.getFirstScreenCenter();
        int x = center.x - w/2;
        int y = center.y - h/2;

        setBounds(x, y, w, h);
        setVisible(true);
    }

    private int frame = 0;

//    private void renderSplashScreen(Graphics2D g) {
//        final String[] comps = {"foo", "bar", "baz"};
//        g.setComposite(AlphaComposite.Clear);
//        g.fillRect(120, 140, 200, 40);
//        g.setPaintMode();
//        g.setColor(Color.BLACK);
//        g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 120, 150);
//    }
//
//    public void close() {
//        if (null != splash) {
//            splash.close();
//        }
//    }
}
