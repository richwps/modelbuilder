package de.hsos.richwps.mb.richWPS.entity.impl.descriptions;

import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import java.util.Objects;
import net.opengis.wps.x100.OutputDescriptionType;
import org.n52.wps.client.richwps.OutputDescriptionTypeBuilder;
import org.n52.wps.io.data.binding.literal.AbstractLiteralDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralDataDescription implements IOutputValue {

    private String identifier;

    private String theabstract;

    private String title;

    private String typereference;

    /**
     * Constructs an empty OutputLiteralDataDescription.
     */
    public OutputLiteralDataDescription() {
        this.identifier = "";
        this.theabstract = "";
        this.title = "";
        this.typereference = "";
    }

    /**
     * Constructs an OutputLiteralDataDescription.
     *
     * @param identifier
     * @param theabstract
     * @param title
     * @param type
     */
    public OutputLiteralDataDescription(String identifier, String theabstract,
            String title, String type) {
        this.identifier = identifier;
        this.theabstract = theabstract;
        this.title = title;
        this.typereference = type;
    }

    /**
     *
     * @param description
     */
    public OutputLiteralDataDescription(OutputDescriptionType description) {
        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.title = description.getTitle().getStringValue();
        this.typereference = description.getLiteralOutput().getDataType().getReference();
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
     * @return
     */
    @Override
    public String getAbstract() {
        return this.theabstract;
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
     * @return
     */
    public String getType() {
        return typereference;
    }

    /**
     *
     * @param subtype
     */
    public void setType(String subtype) {
        this.typereference = subtype;
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
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @param subtype
     * @return
     */
    public Class typeToClass(String subtype) {
        if (subtype.equals("xs:string")) {
            return LiteralStringBinding.class;
        } else if (subtype.equals("xs:int")) {
            return LiteralIntBinding.class;
        }
        return AbstractLiteralDataBinding.class;
        /**
         * AbstractLiteralDataBinding.java LiteralAnyURIBinding.java
         * LiteralBase64BinaryBinding LiteralBooleanBinding.java
         * LiteralByteBinding.java LiteralDateTimeBinding.java
         * LiteralDoubleBinding.java LiteralFloatBinding.java
         * LiteralIntBinding.java LiteralLongBinding.java
         * LiteralShortBinding.java LiteralStringBinding.java
         */
    }

    /**
     *
     * @return
     */
    @Override
    public OutputDescriptionType toOutputDescription() {
        OutputDescriptionTypeBuilder ogctype = new OutputDescriptionTypeBuilder(identifier, title);
        ogctype.setAbstract(this.theabstract);
        ogctype.addNewLiteralOutput(this.typereference);

        return ogctype.getOdt();
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.identifier);
        hash = 89 * hash + Objects.hashCode(this.theabstract);
        hash = 89 * hash + Objects.hashCode(this.title);
        hash = 89 * hash + Objects.hashCode(this.typereference);
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
        final OutputLiteralDataDescription other = (OutputLiteralDataDescription) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.theabstract, other.theabstract)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.typereference, other.typereference)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "OutputLiteralDataDescription{" + "identifier=" + identifier + ", theabstract=" + theabstract + ", title=" + title + ", typereference=" + typereference + '}';
    }

}
