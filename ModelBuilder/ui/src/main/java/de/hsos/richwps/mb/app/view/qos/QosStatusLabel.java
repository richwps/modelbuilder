package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.entity.QoSAnaylsis;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author dziegenh
 */
public class QosStatusLabel extends MultilineLabel {

    public QosStatusLabel() {
        super();

        final EmptyBorder inner = new EmptyBorder(5, 2, 5, 2);
        final LineBorder outer = new LineBorder(Color.GRAY, 1);
        setBorder(new CompoundBorder(outer, inner));
    }

    public void update(QoSAnaylsis qosData) {
        final QoSTarget target = qosData.getTarget();

        Color bgColor, fgColor = Color.BLACK;
        String text;

        if (target.getMax() < target.getMin()) {
            bgColor = UIManager.getColor("Panel.background");
            text = "Invalid target data.";

        } else if (!qosData.hasMonitorValues()) {
            bgColor = UIManager.getColor("Panel.background");
            text = "No monitor data.";

        } else {
            // STATUS RED
            if (qosData.getMedian() < target.getMin() || qosData.getMedian() > target.getMax()) {
                bgColor = Color.RED;
                fgColor = Color.WHITE;
                text = "QoS target values not reached !";

            } else {
                // STATUS GREEN
                if (qosData.getBest() >= target.getMin() && qosData.getWorst() <= target.getMax()) {
                    bgColor = Color.GREEN;
                    text = "QoS target valuesd reached.";

                } else {

                    //STATUS YELLOW
                    bgColor = Color.YELLOW;
                    text = "The median value is inside the target range, but some measured values are outside the range.";
                }
            }
        }

        setBackground(bgColor);
        setForeground(fgColor);
        setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Rectangle bounds = getBounds();
        bounds.x = 0;
        bounds.y = 0;
        UiHelper.drawOverlay(bounds, (Graphics2D) g);
    }

}
