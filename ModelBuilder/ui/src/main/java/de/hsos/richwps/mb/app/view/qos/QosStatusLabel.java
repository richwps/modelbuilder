package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.entity.QoSAnaylsis;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dziegenh
 */
public class QosStatusLabel extends MultilineLabel {

    public QosStatusLabel() {
        super();
//        setFont(getFont().deriveFont(Font.ITALIC));
        setBorder(new EmptyBorder(2, 2, 2, 2));
    }

    public void update(QoSAnaylsis qosData) {
        final QoSTarget target = qosData.getTarget();

        Color bgColor, fgColor = Color.BLACK;
        String text;

        if (target.getMax() < target.getMin()) {
            bgColor = UIManager.getColor("Panel.background");
            text = "Invalid target data.";

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
                    text = "The median value is inside the target range.\nSome values are outside the range.";
                }
            }
        }

        setBackground(bgColor);
        setForeground(fgColor);
        setText(text);
    }

}
