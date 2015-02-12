package de.hsos.richwps.mb.richWPS.entity.impl.descriptions;


import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
public class OutputBoundingBoxDataDescription implements IOutputValue {

    /**
     * Unambiguous identifier or name of a process, input, or output, unique for
     * this server.
     */
    private String identifier;

    /**
     * Title of a process, input, or output, normally available for display to a
     * human.
     */
    private String title;

    /**
     * Text of the abstract.abstractText Brief narrative description of a
     * process, input, or output, normally available for display to a human.
     */
    private String theabstract;

    /**
     * Default Reference to one coordinate reference system.
     */
    private String defaultCRS;

    /**
     * Reference to supported coordinate references.
     */
    private List<String> supportedCRS;

    /**
     * Constructs an empty OutputBoundingBoxDataSpecifier.
     */
    public OutputBoundingBoxDataDescription() {
        this.identifier = "";
        this.theabstract = "";
        this.title = "";
        this.defaultCRS = "";
        this.supportedCRS = new LinkedList();
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
    public OutputBoundingBoxDataDescription(String identifier, String theabstract,
            String title, String defaultCRS, LinkedList<String> supportedCRS) {
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
    public OutputBoundingBoxDataDescription(final OutputDescriptionType description) {
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

        this.supportedCRS = new LinkedList();
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
    final public String getDefaultCRS() {
        return defaultCRS;
    }

    /**
     *
     * @param defaultCRS Default Reference to one coordinate reference system.
     */
    final public void setDefaultCRS(String defaultCRS) {
        if (this.supportedCRS.isEmpty()) {
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

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    
    /**
     *
     * @param supportedCRS Reference to supported coordinate references.
     */
    public void setSupportedCRS(List<String> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }

    /**
     * Returns an OutputDescription with the added BoundingBox Object.
     *
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
            ArrayList list = new ArrayList(supportedCRS);
            ogctype.addNewBoundingBoxOutput(defaultCRS, list);
        }

        return ogctype.getOdt();
    }

    final public int hashCode() {
        int hash = 7;
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
        final OutputBoundingBoxDataDescription other;
        other = (OutputBoundingBoxDataDescription) obj;
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
        return "OutputBoundingBoxDataSpecifier{"
                + ", identifier=" + identifier + ", title=" + title
                + ", theabstract=" + theabstract + ", defaultCRS="
                + defaultCRS + ", supportedCRS=" + supportedCRS + '}';
    }

}
