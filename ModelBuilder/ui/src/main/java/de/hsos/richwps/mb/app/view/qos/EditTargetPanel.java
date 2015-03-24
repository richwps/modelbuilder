package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.QoSTarget;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class EditTargetPanel extends JPanel {

    private final JTextField targetTitle;
    private final JTextField targetAbstract;
    private final JTextField minField;
    private final JTextField maxField;
    private final JTextField idealField;
    private final JTextField varField;

    public EditTargetPanel() {
        this(new QoSTarget());
    }

    public EditTargetPanel(QoSTarget target) {
        super();

        if (null == target) {
            target = new QoSTarget();
        }

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        setLayout(new TableLayout(new double[][]{{p, f}, {p, p, p, p, p, p, p}}));
        int y = 0;

        targetTitle = new JTextField(target.getTargetTitle());
        final String title = "Title";
        targetTitle.setName(title);
        add(new JLabel(title), "0 " + y);
        add(targetTitle, "1 " + y++);

        targetAbstract = new JTextField(target.getTargetAbstract());
        final String aAbstract = "Abstract";
        targetAbstract.setName(aAbstract);
        add(new JLabel(aAbstract), "0 " + y);
        add(targetAbstract, "1 " + y++);

        Double min = target.getMin();
        final String minimum = "Minimum";
        minField = new JTextField(min.toString());
        minField.setName(minimum);
        add(new JLabel(minimum), "0 " + y);
        add(minField, "1 " + y++);

        Double max = target.getMax();
        maxField = new JTextField(max.toString());
        final String maximum = "Maximum";
        maxField.setName(maximum);
        add(new JLabel(maximum), "0 " + y);
        add(maxField, "1 " + y++);

        Double ideal = target.getIdeal();
        final String ideal1 = "Ideal";
        idealField = new JTextField(ideal.toString());
        idealField.setName(ideal1);
        add(new JLabel(ideal1), "0 " + y);
        add(idealField, "1 " + y++);

        Double var = target.getVariance();
        final String variance = "Variance";
        varField = new JTextField(var.toString());
        varField.setName(variance);
        add(new JLabel(variance), "0 " + y);
        add(varField, "1 " + y++);
    }

    public QoSTarget getQosTarget() throws InvalidValueException {
        QoSTarget qoSTarget = new QoSTarget();

        qoSTarget.setMax(parseDoubleValue(maxField));
        qoSTarget.setMin(parseDoubleValue(minField));
        qoSTarget.setVariance(parseDoubleValue(varField));
        qoSTarget.setIdeal(parseDoubleValue(idealField));
        qoSTarget.setTargetAbstract(targetAbstract.getText());
        qoSTarget.setTargetTitle(targetTitle.getText());

        // TODO UOM ???
//        qoSTarget.setUomTranslated(TOOL_TIP_TEXT_KEY);
//        (targetTitle.getText());
        return qoSTarget;
    }

    private Double parseDoubleValue(JTextField field) throws InvalidValueException {
        String currentValue = "?";
        try {
            currentValue = field.getText().trim();
            if (currentValue.isEmpty()) {
                currentValue = "0";
            }
            return Double.parseDouble(currentValue);

        } catch (NumberFormatException ex) {
            throw new InvalidValueException(field.getName(), currentValue);
        }
    }

}
