package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * The title of a titled component.
 *
 * @author dziegenh
 */
public class ComponentTitle extends JLabel {

    private Color gradientColor2;
    private Color gradientColor1;

    public ComponentTitle() {
        this("", null, SwingConstants.LEFT);
    }

    public ComponentTitle(String title) {
        this(title, null, SwingConstants.LEFT);
    }

    public ComponentTitle(String title, int horizontalAlignment) {
        this(title, null, horizontalAlignment);
    }

    public ComponentTitle(Icon icon) {
        this("", icon, SwingConstants.LEFT);
    }

    public ComponentTitle(Icon image, int horizontalAlignment) {
        this("", image, horizontalAlignment);
    }

    public ComponentTitle(String title, Icon icon, int horizontalAlignment) {
        super(title, icon, horizontalAlignment);
        init();
    }

    private void init() {
        // TODO move magic numbers to config/constants
        setBorder(new EmptyBorder(5, 5, 5, 5));
        gradientColor1 = Color.WHITE;
        gradientColor2 = AppConstants.SELECTION_BG_COLOR; //new Color(193, 230, 238);  // TODO get color from UIManager
    }

    public void setGradientColor1(Color color) {
        gradientColor1 = color;
    }

    public void setGradientColor2(Color color) {
        gradientColor2 = color;
    }

    public void setFontStyle(int fontStyle, boolean keepOtherStyles) {
        Font font = getFont();
        int newStyle = keepOtherStyles ? font.getStyle() & fontStyle : fontStyle;
        setFont(font.deriveFont(newStyle));
    }

    public void setBold() {
        setFontStyle(Font.BOLD, false);
    }

    public void setItalic() {
        setFontStyle(Font.ITALIC, false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, gradientColor1, 0, h, gradientColor2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        // TODO let developer decide if there should be any top/bottom border.
        // top border
//        g2d.setColor(gradientColor1);
//        g2d.drawLine(0, 0, w, 0);
        // bottom border

        g2d.setColor(gradientColor2.darker());
        g2d.drawLine(0, h - 1, w, h - 1);

        Color bgColor = UIManager.getColor("Panel.background"); //gradientColor2.darker();
        if (null == bgColor) {
            bgColor = gradientColor1;
        }

        // rounded left corner
        g2d.setColor(bgColor);
        g2d.drawLine(0, 0, 0, 5);
        g2d.drawLine(1, 0, 1, 3);
        g2d.drawLine(2, 0, 2, 1);
        g2d.drawLine(3, 0, 3, 1);
        g2d.drawLine(4, 0, 4, 0);
        g2d.drawLine(5, 0, 5, 0);

        // rounded right corner
        g2d.drawLine(w - 1, 0, w - 1, 5);
        g2d.drawLine(w - 2, 0, w - 2, 3);
        g2d.drawLine(w - 3, 0, w - 3, 1);
        g2d.drawLine(w - 4, 0, w - 4, 1);
        g2d.drawLine(w - 5, 0, w - 5, 0);
        g2d.drawLine(w - 6, 0, w - 6, 0);

        // left corner pseudo anti aliasing
        g2d.setColor(UiHelper.mixColors(gradientColor2, bgColor, .3f));
        g2d.drawLine(0, 5, 0, 6);
        g2d.drawLine(1, 3, 1, 4);
        g2d.drawLine(2, 2, 2, 2);
        g2d.drawLine(3, 1, 4, 1);
        g2d.drawLine(5, 0, 6, 0);

        // right corner pseudo anti aliasing
        g2d.drawLine(w - 1, 5, w - 1, 6);
        g2d.drawLine(w - 2, 3, w - 2, 4);
        g2d.drawLine(w - 3, 2, w - 3, 2);
        g2d.drawLine(w - 4, 1, w - 5, 1);
        g2d.drawLine(w - 6, 0, w - 7, 0);

        super.paintComponent(g);
    }

}
