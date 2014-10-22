package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.CRSsType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.SupportedCRSsType;
import org.n52.wps.client.richwps.InputDescriptionTypeBuilder;

/**
 * InputSpecifier for BoundingBoxData.
 *
 * @author caduevel
 */
public class InputBoundingBoxDataSpecifier implements IInputSpecifier {
    /**
     * Unambiguous identifier or name of a process, input, or
     * output, unique for this server.
     */
    private String identifier;

    /**
     * Title of a process, input, or output, normally available for
     * display to a human.
     */
    private String title;

    /**
     * Text of the abstract.abstractText Brief narrative
     * description of a process, input, or output, normally available for
     * display to a human.
     */
    private String theabstract;

    /**
     * Default Reference to one coordinate reference system.
     */
    private String defaultCRS;

     /**
     * Minimum number of times that values for this parameter
     * are required.
     */
    private int minOccur = 0;

    /**
     * maxOccurs Maximum number of times that this parameter may be
     * present.
     */
    private int maxOccur = 0;

    /**
     * Reference to supported coordinate references.
     */
    private List<String> supportedCRS;

    /**
     * Constructs an empty InputSpecifier.
     */
    public InputBoundingBoxDataSpecifier() {
        this.identifier = "";
        this.title = "";
        this.theabstract = "";
        this.defaultCRS = "";
        this.minOccur = 0;
        this.maxOccur = 0;
        this.supportedCRS = new ArrayList();
    }

    /**
     * Initialize this specifier with a description containing BoundingBoxData.
     *
     * @param desc Inputdescription containing BoundingBoxData.
     */
    public InputBoundingBoxDataSpecifier(final InputDescriptionType desc) {
        this.identifier = desc.getIdentifier().getStringValue();

        if (desc.getAbstract() != null) {
            this.theabstract = desc.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }

        this.title = desc.getTitle().getStringValue();
        this.minOccur = desc.getMinOccurs().intValue();
        this.maxOccur = desc.getMaxOccurs().intValue();

        SupportedCRSsType bbdat = desc.getBoundingBoxData();
        this.defaultCRS = bbdat.getDefault().getCRS();
        CRSsType supported = desc.getBoundingBoxData().getSupported();
        String[] crsArray = supported.getCRSArray();

        this.supportedCRS = new ArrayList();
        this.supportedCRS.addAll(Arrays.asList(crsArray));
    }

    @Override
    final public String getIdentifier() {
        return identifier;
    }

    @Override
    final public String getTitle() {
        return title;
    }

    @Override
    final public String getAbstract() {
        return theabstract;
    }

    @Override
    final public int getMinOccur() {
        return minOccur;
    }

    @Override
    final public int getMaxOccur() {
        return maxOccur;
    }

    /**
     *
     * @param minOccur Minimum number of times that values for this parameter
     * are required.
     */
    final public void setMinOccur(int minOccur) {
        this.minOccur = minOccur;
    }

    /**
     *
     * @param maxOccur Maximum number of times that this parameter may be
     * present.
     */
    final public void setMaxOccur(int maxOccur) {
        this.maxOccur = maxOccur;
    }

    /**
     *
     * @param identifier Unambiguous identifier or name of a process, input, or
     * output, unique for this server.
     */
    final public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @param title Title of a process, input, or output, normally available for
     * display to a human.
     */
    final public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param theabstract Text of the abstract.abstractText Brief narrative
     * description of a process, input, or output, normally available for
     * display to a human.
     */
    final public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

    /**
     *
     * @return Default Reference to one coordinate reference system.
     */
    public String getDefaultCRS() {
        return defaultCRS;
    }

    /**
     *
     * @param defaultCRS Default Reference to one coordinate reference system.
     */
    public void setDefaultCRS(String defaultCRS) {
        if(this.supportedCRS.isEmpty()) {
            this.supportedCRS.add(defaultCRS);
        }

        this.defaultCRS = defaultCRS;
    }

    /**
     *
     * @return Reference to supported coordinate references.
     */
    public List<String> getSupportedCRS() {
        return supportedCRS;
    }

    /**
     *
     * @param supportedCRS Reference to supported coordinate references.
     */
    public void setSupportedCRS(List<String> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }


    /**
     * Returns an InputDescription with the added BoundingBox Object.
     *
     * @return Initialized InputDescription
     */
    @Override
    final public InputDescriptionType toInputDescription() {
        InputDescriptionTypeBuilder desc;
        desc = new InputDescriptionTypeBuilder(this.identifier, this.title,
                BigInteger.valueOf(this.minOccur),
                BigInteger.valueOf(this.maxOccur));
        desc.setAbstract(this.theabstract);

        if (supportedCRS.size() <= 0) {
            //Sollte nie eintreten, da beim setzen von defaultCRS
            //defaultCRS dem supportedCRS Array hinzugefügt wird.
            desc.addNewBoundingBoxData(defaultCRS);

        } else {
            desc.addNewBoundingBoxData(defaultCRS, supportedCRS);
        }
        return desc.getIdt();
    }

    @Override
    public String toString() {
        return "InputBoundingBoxDataSpecifier{" + "identifier=" + identifier
                + ", title=" + title + ", theabstract=" + theabstract
                + ", defaultCRS=" + defaultCRS + ", minOccur=" + minOccur
                + ", maxOccur=" + maxOccur + ", supportedCRS="
                + supportedCRS + '}';
    }

    @Override
    final public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.identifier);
        hash = 73 * hash + Objects.hashCode(this.title);
        hash = 73 * hash + Objects.hashCode(this.theabstract);
        hash = 73 * hash + Objects.hashCode(this.defaultCRS);
        hash = 73 * hash + this.minOccur;
        hash = 73 * hash + this.maxOccur;
        hash = 73 * hash + Objects.hashCode(this.supportedCRS);
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    final public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InputBoundingBoxDataSpecifier other = (InputBoundingBoxDataSpecifier) obj;

        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.theabstract, other.theabstract)) {
            return false;
        }
        if (this.minOccur != other.minOccur) {
            return false;
        }
        if (this.maxOccur != other.maxOccur) {
            return false;
        }

        if (!Objects.equals(this.defaultCRS, other.defaultCRS)) {
            return false;
        }

        return Objects.equals(this.supportedCRS, other.supportedCRS);
    }
}
