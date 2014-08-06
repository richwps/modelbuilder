package de.hsos.richwps.mb.richWPS.entity.specifier;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataInputType;

/**
 * Specifies a complexdata input.
 *
 * @author dalcacer
 */
public class InputComplexDataSpecifier implements IInputSpecifier {

    private InputDescriptionType description;
    private SupportedComplexDataInputType type;

    private String identifier;
    private String titel;
    private String theabstract;
    private List<String> mimetypes;
    private String schema;
    private String encoding;

    public InputComplexDataSpecifier(InputDescriptionType description) {
        this.description = description;
        this.type = description.getComplexData();

        this.identifier = description.getIdentifier().getStringValue();
        this.theabstract = description.getAbstract().getStringValue();
        this.titel = description.getTitle().getStringValue();
        ComplexDataCombinationsType subtypes = this.type.getSupported();
        ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

        this.mimetypes = new ArrayList<>();
        for (ComplexDataDescriptionType thetype : subtypes_) {
            this.mimetypes.add(thetype.getMimeType());

        }
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
        return this.titel;
    }

    public List<String> getMimetypes() {
        return this.mimetypes;
    }

    public String getEncodingByMimetype(String mimetype) {
        ComplexDataCombinationsType subtypes = this.type.getSupported();
        ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

        for (ComplexDataDescriptionType thetype : subtypes_) {
            if (thetype.getMimeType().equals(mimetype)) {
                return thetype.getEncoding();
            }
        }
        return "";
    }

    public String getSchemaByMimetype(String mimetype) {
        ComplexDataCombinationsType subtypes = this.type.getSupported();
        ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

        for (ComplexDataDescriptionType thetype : subtypes_) {
            if (thetype.getMimeType().equals(mimetype)) {
                return thetype.getSchema();
            }
        }
        return "";
    }
}
