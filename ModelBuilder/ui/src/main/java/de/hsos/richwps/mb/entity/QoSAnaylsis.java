package de.hsos.richwps.mb.entity;

/**
 *
 * @author dziegenh
 */
public class QoSAnaylsis {

    private QoSTarget target;

    private double worst;

    private double median;

    private double best;

    public QoSAnaylsis(QoSTarget target, double worst, double median, double best) {
        this.target = target;
        this.worst = worst;
        this.median = median;
        this.best = best;
    }

    public QoSTarget getTarget() {
        return target;
    }

    public void setTarget(QoSTarget target) {
        this.target = target;
    }

    public double getWorst() {
        return worst;
    }

    public void setWorst(double worst) {
        this.worst = worst;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getBest() {
        return best;
    }

    public void setBest(double best) {
        this.best = best;
    }

}
