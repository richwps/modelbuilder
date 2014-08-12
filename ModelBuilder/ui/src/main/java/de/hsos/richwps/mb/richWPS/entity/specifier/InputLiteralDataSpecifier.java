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
    private String type;
    private String title;
    private String theabstract;
    private String value;

    public InputLiteralDataSpecifier(final String identifier, final String type, final String title, final String theabstract) {
        this.identifier = identifier;
        this.type = type;
        this.title = title;
        this.theabstract = theabstract;
    }

    public InputLiteralDataSpecifier(final InputDescriptionType description) {
        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }

        this.title = description.getTitle().getStringValue();
        LiteralInputType thetype = description.getLiteralData();
        this.type = thetype.getDataType().getReference();
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    public String getType() {
        return this.type;
    }

    public void setSubtype(String type) {
        this.type = type;
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
