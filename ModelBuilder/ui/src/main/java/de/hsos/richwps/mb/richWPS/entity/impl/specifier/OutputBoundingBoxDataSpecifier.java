package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.CRSsType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.SupportedCRSsType;
import org.n52.wps.client.richwps.OutputDescriptionTypeBuilder;

/**
 *
 * @author caduevel
 */
public class OutputBoundingBoxDataSpecifier implements IOutputSpecifier {

    /**
     * The specified Outputdescription.
     */
    private OutputDescriptionType description;

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
     * Reference to supported coordinate references.
     */
    private ArrayList<String> supportedCRS;

    /**
     * Constructs an empty OutputBoundingBoxDataSpecifier.
     */
    public OutputBoundingBoxDataSpecifier() {
        this.identifier = "";
        this.theabstract = "";
        this.title = "";
        this.defaultCRS = "";
        this.supportedCRS = new ArrayList();
    }

    /**
     * Constructs an OutputBoundingBoxDataSpecifier.
     *
     * @param identifier
     * @param theabstract
     * @param title
     * @param defaultCRS
     * @param supportedCRS
     */
    public OutputBoundingBoxDataSpecifier(String identifier, String theabstract,
            String title, String defaultCRS, ArrayList<String> supportedCRS) {
        this.identifier = identifier;
        this.theabstract = theabstract;
        this.title = title;
        this.defaultCRS = defaultCRS;
        this.supportedCRS = supportedCRS;
    }

    /**
     * Constructs BoundingBoxDataSpecifier using OutputDescription with BBData.
     *
     * @param description
     */
    public OutputBoundingBoxDataSpecifier(OutputDescriptionType description) {
        this.description = description;
        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.title = description.getTitle().getStringValue();
        SupportedCRSsType boundingBoxOutput;
        boundingBoxOutput = description.getBoundingBoxOutput();
        this.defaultCRS = boundingBoxOutput.getDefault().getCRS();

        this.supportedCRS = new ArrayList();
        CRSsType supported = boundingBoxOutput.getSupported();
        this.supportedCRS.addAll(Arrays.asList(supported.getCRSArray()));
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getAbstract() {
        return this.theabstract;
    }

    @Override
    public String getTitle() {
        return this.title;
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
     * @return The specified OutputDescription.
     */
    final public OutputDescriptionType getDescription() {
        return description;
    }

    /**
     *
     * @param description The specified OutputDescription.
     */
    final public void setDescription(OutputDescriptionType description) {
        this.description = description;
    }

    /**
     *
     * @return Default Reference to one coordinate reference system.
     */
    final public String getDefaultCRS() {
        return defaultCRS;
    }

    /**
     *
     * @param defaultCRS Default Reference to one coordinate reference system.
     */
    final public void setDefaultCRS(String defaultCRS) {
        if(this.supportedCRS.isEmpty()) {
            this.supportedCRS.add(defaultCRS);
        }

        this.defaultCRS = defaultCRS;
    }

    /**
     *
     * @return Reference to supported coordinate references.
     */
    public ArrayList<String> getSupportedCRS() {
        return supportedCRS;
    }

    /**
     *
     * @param supportedCRS Reference to supported coordinate references.
     */
    public void setSupportedCRS(ArrayList<String> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }

    /**
     * Returns an OutputDescription with the added BoundingBox Object.
     * @return Initialized BoundingBox-OutputDescription.
     */
    @Override
    final public OutputDescriptionType toOutputDescription() {
        OutputDescriptionTypeBuilder ogctype;
        ogctype = new OutputDescriptionTypeBuilder(identifier, title);
        ogctype.setAbstract(this.theabstract);

        if (supportedCRS.size() <= 0) {
            ogctype.addNewBoundingBoxOutput(defaultCRS);

        } else {
            ogctype.addNewBoundingBoxOutput(defaultCRS, supportedCRS);
        }



        return ogctype.getOdt();
    }

    final public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.description);
        hash = 31 * hash + Objects.hashCode(this.identifier);
        hash = 31 * hash + Objects.hashCode(this.title);
        hash = 31 * hash + Objects.hashCode(this.theabstract);
        hash = 31 * hash + Objects.hashCode(this.defaultCRS);
        hash = 31 * hash + Objects.hashCode(this.supportedCRS);
        return hash;
    }

    @Override
    final public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OutputBoundingBoxDataSpecifier other;
        other = (OutputBoundingBoxDataSpecifier) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.theabstract, other.theabstract)) {
            return false;
        }
        if (!Objects.equals(this.defaultCRS, other.defaultCRS)) {
            return false;
        }
        return Objects.equals(this.supportedCRS, other.supportedCRS);
    }

    @Override
    final public String toString() {
        return "OutputBoundingBoxDataSpecifier{" + "description=" + description
                + ", identifier=" + identifier + ", title=" + title
                + ", theabstract=" + theabstract + ", defaultCRS="
                + defaultCRS + ", supportedCRS=" + supportedCRS + '}';
    }

}
