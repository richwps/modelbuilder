package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.entity.QoSAnaylsis;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.properties.Property;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/**
 *
 * @author dziegenh
 */
public class QosAnalysisPanel extends JLabel {

    private QoSAnaylsis data;

    // Target background colors
    public final Color insideRangeColor = new Color(0x66c066);
    public final Color outsideRangeColor = new Color(0xc06666);

    // Monitor value markers
    private final Color monitorMarkColor = new Color(0xffff33);
    private final int monitorMarkRangeAlpha = 0x60000000;
    private final Color monitorMarkRangeColor = new Color(this.monitorMarkColor.getRGB() + monitorMarkRangeAlpha, true);
    private final Rectangle monitorMark = new Rectangle(0, 0, 6, 8);
    private final int monitorMarkPadding = 2;

    private QoSTarget target;

    private double maxValue;

    private Color overlayColor = new Color(0xffffffff, true);
    private Color overlayColor2 = new Color(0x0, true);
    private Color overlayColor3 = new Color(0x40000000, true);

    public QosAnalysisPanel(final Property<QoSAnaylsis> targetProperty) {
        super();

        data = targetProperty.getValue();
        this.target = data.getTarget();

        computeMax();
        updateBorder();
        updateToolTipText();
    }

    private void computeMax() {
        this.maxValue = 0d;
        maxValue = Math.max(maxValue, data.getBest());
        maxValue = Math.max(maxValue, data.getMedian());
        maxValue = Math.max(maxValue, data.getWorst());
        maxValue = Math.max(maxValue, target.getIdeal());
        maxValue = Math.max(maxValue, target.getMax());
        maxValue = Math.max(maxValue, target.getMin());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle b = getBounds();
        b.setLocation(0, 0);

        boolean valuesDrawn = false;

        if (0 < this.maxValue) {

            try {
                // background rectangles indicating the target value range
                drawTargetValues(g);

                // black marks representing the monitored values
                drawMonitorValues(g);

                valuesDrawn = true;

                drawOverlay(b, g2d, g);

            } catch (Exception ex) {

                // if any error occurs, show a message instead of the marks
                valuesDrawn = false;
            }
        }

        if (!valuesDrawn) {
            Font font = new Font("Sans Serif", Font.ITALIC, 11);
            AttributedString as1 = new AttributedString("Not enough data");
            as1.addAttribute(TextAttribute.FONT, font);
            g2d.drawString(as1.getIterator(), 10, b.height / 2);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);

    }

    /**
     * Adds a simple 3D-appearance using two gradient overlays.
     *
     * @param b
     * @param g2d
     * @param g
     */
    public void drawOverlay(Rectangle b, Graphics2D g2d, Graphics g) {
        final int overlaySize = b.height / 3;
        g2d.setPaint(new GradientPaint(0, 0, overlayColor, 0, overlaySize, overlayColor2));

        g.fillRect(b.x, b.y, b.width, overlaySize);

        final int bottomOverlayY = b.height - overlaySize;
        g2d.setPaint(new GradientPaint(0, bottomOverlayY, overlayColor2, 0, b.height, overlayColor3));
        g.fillRect(b.x, b.y + bottomOverlayY, b.width, overlaySize);
    }

    /**
     * Gets the corresponding Pixel-X-Value to a QoS value.
     *
     * @param value
     * @param bounds
     * @return
     */
    private int qosValueToPixel(double value, Rectangle bounds) {
        final int drawingSize = bounds.width - monitorMark.width - 2 * monitorMarkPadding;
        return (int) (value / this.maxValue * drawingSize);
    }

    /**
     * Draw black marks representing the monitored values.
     *
     * @param g
     */
    private void drawMonitorValues(Graphics g) {
        Rectangle b = getBounds();

        this.monitorMark.y = (b.height - monitorMark.height) / 2;

        // draw connecting lines (range)
        int left = qosValueToPixel(data.getBest(), b);
        left += monitorMarkPadding;

        int right = qosValueToPixel(data.getWorst(), b);
        right += (monitorMark.width);

        int width = right - left;

        g.setColor(monitorMarkRangeColor);
        g.fillRect(left, monitorMark.y, width, monitorMark.height);
        g.setColor(this.monitorMarkColor);
        g.drawRect(left, monitorMark.y, width, monitorMark.height - 1);

        // draw rectangle value marks
        drawAMonitorValue(g, data.getMedian(), b);
        drawAMonitorValue(g, data.getBest(), b);
        drawAMonitorValue(g, data.getWorst(), b);
    }

    /**
     * Draws a rectangle representing the monitor value.
     *
     * @param g
     * @param value
     * @param bounds
     */
    private void drawAMonitorValue(Graphics g, double value, Rectangle bounds) {
        int midPx = qosValueToPixel(value, bounds);
        this.monitorMark.x = midPx + monitorMarkPadding;
        g.setColor(this.monitorMarkColor);
        g.fillRect(monitorMark.x, monitorMark.y, monitorMark.width, monitorMark.height);
        g.setColor(this.monitorMarkColor.darker());
        g.drawLine(monitorMark.x, monitorMark.y + 1, monitorMark.x, monitorMark.y + monitorMark.height - 2);
    }

    /**
     * Draw background rectangles indicating the target value range.
     *
     * @param g
     */
    private void drawTargetValues(Graphics g) {

        Rectangle b = getBounds();
        b.setLocation(0, 0);

        int tMinPx = 0, tMaxPx = 0;

        if (0 < target.getMin()) {
            tMinPx = qosValueToPixel(target.getMin(), b);
        }

        if (0 < target.getMax()) {
            tMaxPx = qosValueToPixel(target.getMax(), b);
        }

        // red area
        g.setColor(outsideRangeColor);
        g.fillRect(b.x, b.y, b.width, b.height);

        // green area
        g.setColor(insideRangeColor);
        int greenWidth = tMaxPx - tMinPx;
        g.fillRect(tMinPx, b.y, greenWidth, b.height);
    }

    public void setQosProperty(Property<QoSAnaylsis> targetProperty) {
        data = targetProperty.getValue();
        this.target = data.getTarget();

        computeMax();
        updateBorder();
        updateToolTipText();
    }

    private void updateToolTipText() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("<html><b>Analysis for QoS target <i>");
        sb.append(this.target.getTargetTitle());
        sb.append("</i></b><br/><br/>");

        sb.append("<u>Target values:</u><br/>");

        sb.append("<table>");
        sb.append("<tr><td>Minimum: </td><td>");
        sb.append(this.target.getMin());
        sb.append(" sec </td></tr>");

        sb.append("<tr><td>Ideal: </td><td>");
        sb.append(this.target.getIdeal());
        sb.append(" sec </td></tr>");

        sb.append("<tr><td>Maximum: </td><td>");
        sb.append(this.target.getMax());
        sb.append(" sec </td></tr>");
        sb.append("</table>");

        if (0 < this.maxValue) {
            sb.append("<br/><i>The<span style='background:#00ff00;'> green area </span>represents the range of the target values.</i><br/>");
        }

        sb.append("<br /><u>Monitored values:</u><br/>");

        sb.append("<table>");
        sb.append("<tr><td>Best: </td><td>");
        sb.append(this.data.getBest());
        sb.append(" sec </td></tr>");

        sb.append("<tr><td>Median: </td><td>");
        sb.append(this.data.getMedian());
        sb.append(" sec </td></tr>");

        sb.append("<tr><td>Worst: </td><td>");
        sb.append(this.data.getWorst());
        sb.append(" sec </td></tr>");
        sb.append("</table>");

        if (0 < this.maxValue) {
            sb.append("<br/><i>The<span style='background:#ffff00;'> yellow marks </span>indicate the monitored values.</i><br/>");
        }
        
        sb.append("</html>");

        setToolTipText(sb.toString());
    }

    private void updateBorder() {
        if (0 < this.maxValue) {
            setBorder(new LineBorder(Color.GRAY));
        } else {
            setBorder(null);
        }
    }

}
