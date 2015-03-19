package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.ui.UiHelper;
import java.util.Objects;

/**
 *
 * @author dziegenh
 */
public class QoSTarget {

    public static final String PROPERTY_KEY_QOS_TARGET_IDEAL = "Ideal";
    public static final String PROPERTY_KEY_QOS_TARGET_UOM = "Unit";
    public static final String PROPERTY_KEY_QOS_TARGET_VAR = "Variance";
    public static final String PROPERTY_KEY_QOS_TARGET_MIN = "Minimum";
    public static final String PROPERTY_KEY_QOS_TARGET_ABSTRACT = "Abstract";
    public static final String PROPERTY_KEY_QOS_TARGET_MAX = "Maximum";

    private String targetTitle;
    private String targetAbstract;
    private double min;
    private double max;
    private double ideal;
    private double variance;
    private String uomTranslated;

    public QoSTarget() {
    }
    
    public QoSTarget(String targetTitle, String targetAbstract, double min, double max, double ideal, double variance, String uomTranslated) {
        this.targetTitle = targetTitle;
        this.targetAbstract = targetAbstract;
        this.min = min;
        this.max = max;
        this.ideal = ideal;
        this.variance = variance;
        this.uomTranslated = uomTranslated;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public String getTargetAbstract() {
        return targetAbstract;
    }

    public void setTargetAbstract(String targetAbstract) {
        this.targetAbstract = targetAbstract;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getIdeal() {
        return ideal;
    }

    public void setIdeal(double ideal) {
        this.ideal = ideal;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public String getUomTranslated() {
        return uomTranslated;
    }

    public void setUomTranslated(String uomTranslated) {
        this.uomTranslated = uomTranslated;
    }

    @Override
    public String toString() {
        return UiHelper.avoidNull(this.targetTitle);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.targetTitle);
        hash = 23 * hash + Objects.hashCode(this.targetAbstract);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.ideal) ^ (Double.doubleToLongBits(this.ideal) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.variance) ^ (Double.doubleToLongBits(this.variance) >>> 32));
        hash = 23 * hash + Objects.hashCode(this.uomTranslated);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QoSTarget other = (QoSTarget) obj;
        if (!Objects.equals(this.targetTitle, other.targetTitle)) {
            return false;
        }
        if (!Objects.equals(this.targetAbstract, other.targetAbstract)) {
            return false;
        }
        if (Double.doubleToLongBits(this.min) != Double.doubleToLongBits(other.min)) {
            return false;
        }
        if (Double.doubleToLongBits(this.max) != Double.doubleToLongBits(other.max)) {
            return false;
        }
        if (Double.doubleToLongBits(this.ideal) != Double.doubleToLongBits(other.ideal)) {
            return false;
        }
        if (Double.doubleToLongBits(this.variance) != Double.doubleToLongBits(other.variance)) {
            return false;
        }
        if (!Objects.equals(this.uomTranslated, other.uomTranslated)) {
            return false;
        }
        return true;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new QoSTarget(targetTitle, targetAbstract, min, max, ideal, variance, uomTranslated);
    }

    
    
}
