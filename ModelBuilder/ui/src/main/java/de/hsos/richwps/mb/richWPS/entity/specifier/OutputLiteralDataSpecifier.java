package de.hsos.richwps.mb.richWPS.entity.specifier;

import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import java.util.Objects;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.wps.x100.LiteralOutputType;
import net.opengis.wps.x100.OutputDescriptionType;
import org.n52.wps.client.transactional.BasicOutputDescriptionType;
import org.n52.wps.io.data.binding.literal.AbstractLiteralDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralDataSpecifier implements IOutputSpecifier {

    private OutputDescriptionType description;
    private String identifier;
    private String theabstract;
    private String title;
    private String typereference;

    /**
     * Constructs an empty OutputLiteralDataSpecifier.
     */
    public OutputLiteralDataSpecifier() {
        this.identifier = "";
        this.theabstract = "";
        this.title = "";
        this.typereference = "";
    }

    /**
     * Constructs an OutputLiteralDataSpecifier.
     * @param identifier
     * @param theabstract
     * @param title
     * @param type 
     */
    public OutputLiteralDataSpecifier(String identifier, String theabstract, String title, String type) {
        this.identifier = identifier;
        this.theabstract = theabstract;
        this.title = title;
        this.typereference = type;
    }

    public OutputLiteralDataSpecifier(OutputDescriptionType description) {
        this.description = description;
        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.title = description.getTitle().getStringValue();
        this.typereference = description.getLiteralOutput().getDataType().getReference();
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

    public String getType() {
        return typereference;
    }

    public void setType(String subtype) {
        this.typereference = subtype;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

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

    @Override
    public BasicOutputDescriptionType toBasicOutputDescriptionType() {

        
        BasicOutputDescriptionType ogctype = new BasicOutputDescriptionType(identifier, title);
        ogctype.setAbstract(this.theabstract);
        ogctype.addNewLiteralOutput(this.typereference);

        System.out.println(ogctype.getOdt().toString());
        return ogctype;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.identifier);
        hash = 89 * hash + Objects.hashCode(this.theabstract);
        hash = 89 * hash + Objects.hashCode(this.title);
        hash = 89 * hash + Objects.hashCode(this.typereference);
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
        final OutputLiteralDataSpecifier other = (OutputLiteralDataSpecifier) obj;
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

    @Override
    public String toString() {
        return "OutputLiteralDataSpecifier{" + "identifier=" + identifier + ", theabstract=" + theabstract + ", title=" + title + ", typereference=" + typereference + '}';
    }

}
