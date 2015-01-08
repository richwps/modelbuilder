package de.hsos.richwps.mb.app.view;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class LoadingScreen extends JWindow {

    public LoadingScreen(Window parent) {
        super(parent);
        setType(Type.POPUP);
//        setSize(new Dimension(300, 200));
        setLocation(parent.getLocation());
        setSize(parent.getSize());
        setOpacity(.8f);
        
        Color bgCol = AppConstants.SELECTION_BG_COLOR;

        JPanel content = new JPanel();
        content.setBackground(new Color(0xffffff));
        LineBorder outer = new LineBorder(bgCol.darker(), 1);
        EmptyBorder inner = new EmptyBorder(2, 2, 2, 2);
        content.setBorder(new CompoundBorder(outer, inner));

        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        content.setLayout(new TableLayout(new double[][]{{f, p, f}, {f, p, f}}));
        
        JLabel label = new JLabel(AppConstants.LOADING_SCREEN_TITLE);
        Font font = label.getFont().deriveFont(20f);
        font = font.deriveFont(Font.ITALIC);
        label.setFont(font);
        content.add(label, "1 1");

        setLayout(new TableLayout(new double[][]{{f}, {f}}));
        add(content, "0 0");

        this.invalidate();
        this.repaint();
    }

}
