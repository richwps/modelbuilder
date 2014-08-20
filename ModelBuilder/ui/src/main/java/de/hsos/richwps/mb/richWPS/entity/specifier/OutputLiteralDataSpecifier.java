package de.hsos.richwps.mb.richWPS.entity.specifier;

import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import net.opengis.wps.x100.OutputDescriptionType;
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
    private String subtype;

    public OutputLiteralDataSpecifier(OutputDescriptionType description) {
        this.description = description;
        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.title = description.getTitle().getStringValue();
        this.subtype = description.getLiteralOutput().getDataType().toString();
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

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
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

}
