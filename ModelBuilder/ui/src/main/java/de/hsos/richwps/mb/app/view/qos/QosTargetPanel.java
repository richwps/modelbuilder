package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.properties.Property;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author dziegenh
 */
public class QosTargetPanel extends JPanel {

    private final Property<QoSTarget> targetProperty;
    private final QoSTarget target;
    
    public final Color insideRangeColor = new Color(0x66dd66);
    public final Color outsideRangeColor = new Color(0xdd6666);
    
    public QosTargetPanel(final Property<QoSTarget> targetProperty) {
        super();

        this.targetProperty = targetProperty;
        target = targetProperty.getValue();

//        add(new JLabel("[ " + value.getMin() + " - " + value.getMax() + "]"));
//        double p = TableLayout.PREFERRED;
//        double f = TableLayout.FILL;
//        setLayout(new TableLayout(new double[][]{{f}, {f}}));
//        add(new JLabel)
    }

    @Override
    protected void paintComponent(Graphics g) {
        Rectangle b = getBounds();

        int redWidth = (int) (b.width * .2);
        int greenWidth = b.width - 2 * redWidth;

        int red1X = 0;
        int greenX = red1X + redWidth;
        int red2X = greenX + greenWidth;

        g.setColor(outsideRangeColor);
        g.fillRect(red1X, b.y, redWidth, b.height);
        g.fillRect(red2X, b.y, redWidth, b.height);

        g.setColor(insideRangeColor);
        g.fillRect(greenX, b.y, greenWidth, b.height);
    }


    public void setQosProperty(Property<QoSTarget> targetProperty) {
        // TODO (necessary?!?)
        Logger.log("!SINGLE! QOS TARGET PROPERTY CHANGED!! NEED TO REACT?");
    }

}
