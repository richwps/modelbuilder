package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import java.math.BigInteger;
import java.util.Objects;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import org.n52.wps.client.richwps.InputDescriptionTypeBuilder;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataSpecifier implements IInputSpecifier {

    private String identifier;
    private String typeReference;
    private String title;
    private String theabstract;
    private String defaultvalue;
    private int minOccur = 0;
    private int maxOccur = 0;

    /**
     * Constructs an empty InputSpecifier.
     */
    public InputLiteralDataSpecifier() {
        this.identifier = "";
        this.typeReference = "";
        this.title = "";
        this.theabstract = "";
        this.defaultvalue = "";
        this.minOccur = 0;
        this.maxOccur = 0;
    }

    /**
     *
     * @param description
     */
    public InputLiteralDataSpecifier(final InputDescriptionType description) {
        this.identifier = description.getIdentifier().getStringValue();

        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }

        this.title = description.getTitle().getStringValue();
        LiteralInputType thetype = description.getLiteralData();
        this.typeReference = thetype.getDataType().getReference();
        this.minOccur = description.getMinOccurs().intValue();
        this.maxOccur = description.getMaxOccurs().intValue();
        this.defaultvalue = thetype.getDefaultValue();
    }

    /**
     *
     * @return
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     *
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.typeReference = type;
    }

    /**
     *
     * @param minOccur
     */
    public void setMinOccur(int minOccur) {
        this.minOccur = minOccur;
    }

    /**
     *
     * @param maxOccur
     */
    public void setMaxOccur(int maxOccur) {
        this.maxOccur = maxOccur;
    }
    
    /**
     *
     * @return
     */
    public String getType() {
        return this.typeReference;
    }

    /**
     *
     * @param type
     */
    public void setSubtype(String type) {
        this.typeReference = type;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAbstract() {
        return theabstract;
    }

    /**
     *
     * @param theabstract
     */
    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

    /**
     *
     * @return
     */
    @Override
    public int getMinOccur() {
        return minOccur;
    }

    /**
     *
     * @return
     */
    @Override
    public int getMaxOccur() {
        return maxOccur;
    }

    /**
     *
     * @return
     */
    public String getDefaultvalue() {
        return defaultvalue;
    }

    /**
     *
     * @param defaultvalue
     */
    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }

    /**
     *
     * @return
     */
    @Override
    public InputDescriptionType toInputDescription() {
        InputDescriptionTypeBuilder desc;
        desc = new InputDescriptionTypeBuilder(this.identifier, this.title, BigInteger.valueOf(this.minOccur), BigInteger.valueOf(this.maxOccur));
        desc.setAbstract(this.theabstract);
        
        desc.addNewLiteralData(this.typeReference, this.defaultvalue);
        return desc.getIdt();
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.identifier);
        hash = 97 * hash + Objects.hashCode(this.typeReference);
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + Objects.hashCode(this.theabstract);
        hash = 97 * hash + Objects.hashCode(this.defaultvalue);
        hash = 97 * hash + this.minOccur;
        hash = 97 * hash + this.maxOccur;
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InputLiteralDataSpecifier other = (InputLiteralDataSpecifier) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.typeReference, other.typeReference)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.theabstract, other.theabstract)) {
            return false;
        }
        if (!Objects.equals(this.defaultvalue, other.defaultvalue)) {
            return false;
        }
        if (this.minOccur != other.minOccur) {
            return false;
        }
        if (this.maxOccur != other.maxOccur) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "InputLiteralDataSpecifier{" + "identifier=" + identifier + ", typeReference=" + typeReference + ", title=" + title + ", theabstract=" + theabstract + ", defaultvalue=" + defaultvalue + ", minOccur=" + minOccur + ", maxOccur=" + maxOccur + '}';
    }
    
    
    
    

}
