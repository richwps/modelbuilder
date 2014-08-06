package de.hsos.richwps.mb.richWPS.entity.specifier;

import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataType;

/**
 *
 * @author dalcacer
 */
public class OutputComplexDataSpecifier implements IOutputSpecifier {

    private OutputDescriptionType description;
    private String identifier;
    private String theabstract;
    private String title;
    private List<String> mimetypes;
    private List<String> schema;
    private SupportedComplexDataType type;

    public OutputComplexDataSpecifier(OutputDescriptionType description) {
        this.description = description;

        mimetypes = new ArrayList<String>();
        type = description.getComplexOutput();
        this.identifier = description.getIdentifier().getStringValue();

        this.theabstract = description.getAbstract().getStringValue();
        this.title = description.getTitle().getStringValue();
        ComplexDataCombinationsType subtypes = type.getSupported();

        ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

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
        return this.title;
    }

    public OutputDescriptionType getDescription() {
        return description;
    }

    public void setDescription(OutputDescriptionType description) {
        this.description = description;
    }

    public String getTheabstract() {
        return theabstract;
    }

    public void setTheabstract(String theabstract) {
        this.theabstract = theabstract;
    }

    public List<String> getMimetypes() {
        return mimetypes;
    }

    public void setMimetypes(List<String> mimetypes) {
        this.mimetypes = mimetypes;
    }

    public List<String> getSchema() {
        return schema;
    }

    public void setSchema(List<String> schema) {
        this.schema = schema;
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
