package de.hsos.richwps.mb.richWPS.entity.specifier;

import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import net.opengis.wps.x100.OutputDescriptionType;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralDataSpecifier implements IOutputSpecifier {

    private OutputDescriptionType description;
    private String identifier;
    private String theabstract;
    private String title;

    public OutputLiteralDataSpecifier(OutputDescriptionType description) {
        this.description = description;
        this.identifier = description.getIdentifier().getStringValue();
        this.theabstract = description.getAbstract().getStringValue();
        this.title = description.getTitle().getStringValue();

    }

    @Override
    public Class getType() {
        return this.getClass();
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
}
