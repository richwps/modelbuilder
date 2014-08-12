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
    private List<List> types;
    public static int mimetype_IDX = 0;
    public static int schema_IDX = 1;
    public static int encoding_IDX = 2;

    private SupportedComplexDataType type;

    public OutputComplexDataSpecifier(OutputDescriptionType description) {
        this.description = description;

        types = new ArrayList<List>();
        type = description.getComplexOutput();
        this.identifier = description.getIdentifier().getStringValue();

        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.title = description.getTitle().getStringValue();
        ComplexDataCombinationsType subtypes = type.getSupported();

        ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

        for (ComplexDataDescriptionType thetype : subtypes_) {
            List<String> atype = new ArrayList<>();
            atype.add(thetype.getMimeType());
            atype.add(thetype.getSchema());
            atype.add(thetype.getEncoding());
            this.types.add(atype);
        }
        
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

    public List<List> getTypes() {
        return types;
    }

}
