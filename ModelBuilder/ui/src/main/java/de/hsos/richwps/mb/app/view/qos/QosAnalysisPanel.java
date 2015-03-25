package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.entity.QoSAnaylsis;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.properties.Property;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
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
    public final Color insideRangeColor = new Color(0x808080);
    public final Color outsideRangeColor = new Color(0x0);

    // Monitor value markers
    private final Color monitorMarkColor = new Color(0x003300);
    private final Color monitorMarkRangeColor = new Color(0xe000ff00, true);
    private final Rectangle monitorMark = new Rectangle(0, 0, 6, 8);
    private final int monitorMarkPadding = 2;

    private QoSTarget target;

    private double maxValue;

    private final Color overlayColor = new Color(0xffffffff, true);
    private final Color overlayColor2 = new Color(0x0, true);
    private final Color overlayColor3 = new Color(0x40000000, true);

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
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Rectangle b = getBounds();
        b.setLocation(0, 0);

        if (0 < this.maxValue && data.hasMonitorValues()) {

            try {
                // background rectangles indicating the target value range
                drawTargetValues(g);

                // green triangle representing the monitored values
                drawMonitorValues(g);

                drawOverlay(b, g2d);

            } catch (Exception ex) {

            }
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);

    }

    /**
     * Adds a simple 3D-appearance using two gradient overlays.
     *
     * @param b
     * @param g2d
     */
    public void drawOverlay(Rectangle b, Graphics2D g2d) {
        final int overlaySize = b.height / 3;
        g2d.setPaint(new GradientPaint(0, 0, overlayColor, 0, overlaySize, overlayColor2));

        g2d.fillRect(b.x, b.y, b.width, overlaySize);

        final int bottomOverlayY = b.height - overlaySize;
        g2d.setPaint(new GradientPaint(0, bottomOverlayY, overlayColor2, 0, b.height, overlayColor3));
        g2d.fillRect(b.x, b.y + bottomOverlayY, b.width, overlaySize);
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
     * Draws a triangle which represents the monitor values.
     *
     * @param g
     */
    private void drawMonitorValues(Graphics g) {
        Rectangle b = getBounds();

        int left = qosValueToPixel(data.getBest(), b);
        left += monitorMarkPadding;

        int center = qosValueToPixel(data.getMedian(), b);
        center += monitorMarkPadding;

        int right = qosValueToPixel(data.getWorst(), b);
        right += (monitorMark.width);

        int halfMark = this.monitorMark.height / 2;

        int[] xPoints = new int[]{left, center, right};
        int[] yPoints = new int[]{b.height, halfMark, b.height};

        // fill triangle
        g.setColor(monitorMarkRangeColor);
        g.fillPolygon(xPoints, yPoints, xPoints.length);

        // draw border
        g.setColor(this.monitorMarkColor);
        g.drawPolygon(xPoints, yPoints, xPoints.length);
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

        sb.append("<table cellpadding='0'>");
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
            sb.append("<br/><i>The<span style='background:#cccccc;'> grey area </span>represents the range between<br/>the minimum and maximum target values.</i><br/>");
        }

        sb.append("<br /><u>Monitored values:</u><br/>");

        sb.append("<table cellpadding='0'>");
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
            sb.append("<br/><i>The<span style='background:#00ff00;'> green area </span>indicates the monitored values.<br/>Its peak shows the measured median value.</i><br/>");
        }

        sb.append("</html>");

        setToolTipText(sb.toString());
    }

    private void updateBorder() {
        if (0 < this.maxValue && data.hasMonitorValues()) {
            setBorder(new LineBorder(Color.GRAY));
        } else {
            setBorder(null);
        }
    }

}
