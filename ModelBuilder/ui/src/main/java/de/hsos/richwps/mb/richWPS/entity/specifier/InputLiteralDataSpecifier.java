package de.hsos.richwps.mb.richWPS.entity.specifier;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataSpecifier implements IInputSpecifier {

    private String identifier;
    private String subtype;
    private String title;
    private String theabstract;
    private String value;

    public InputLiteralDataSpecifier(final String identifier, final String type, final String title, final String theabstract) {
        this.identifier = identifier;
        this.subtype = type;
        this.title = title;
        this.theabstract = theabstract;
    }

    public InputLiteralDataSpecifier(final InputDescriptionType description) {
        this.identifier = description.getIdentifier().getStringValue();
        this.theabstract = description.getAbstract().getStringValue();
        this.title = description.getTitle().getStringValue();
        LiteralInputType type = description.getLiteralData();
        this.subtype = type.getDataType().getReference();

    }

    @Override
    public Class getType() {
        return this.getClass();
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public void setSubtype(String type) {
        this.subtype = type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return theabstract;
    }

    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
