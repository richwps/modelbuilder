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
    private int minOccur = 0;
    private int maxOccur = 0;

    private List<String> defaulttype;
    private List<List> types;
    public final static int mimetype_IDX = 0;
    public final static int schema_IDX = 1;
    public final static int encoding_IDX = 2;

    /**
     * Constructs a new InputSpecifier for complex data.
     *
     * @param description 52n InputDescriptionType.
     */
    public InputComplexDataSpecifier(InputDescriptionType description) {
        this.description = description;
        this.type = description.getComplexData();

        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.titel = description.getTitle().getStringValue();

        ComplexDataCombinationsType subtypes = this.type.getSupported();
        ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

        this.types = new ArrayList<>();
        for (ComplexDataDescriptionType thetype : subtypes_) {
            List<String> atype = new ArrayList();
            atype.add(thetype.getMimeType());
            atype.add(thetype.getSchema());
            atype.add(thetype.getEncoding());
            this.types.add(atype);
        }
        net.opengis.wps.x100.ComplexDataCombinationType thedefaulttype = this.type.getDefault();
        ComplexDataDescriptionType thetype = thedefaulttype.getFormat();
        this.defaulttype = new ArrayList();
        defaulttype.add(thetype.getMimeType());
        defaulttype.add(thetype.getSchema());
        defaulttype.add(thetype.getEncoding());

        this.minOccur = description.getMinOccurs().intValue();
        this.maxOccur = description.getMaxOccurs().intValue();
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

    @Override
    public int getMinOccur() {
        return minOccur;
    }

    @Override
    public int getMaxOccur() {
        return maxOccur;
    }

    public List<String> getDefaultType() {
        return this.defaulttype;
    }

    public List<List> getTypes() {
        return this.types;
    }

    public boolean isDefaultType(List type) {
        if(type.equals(defaulttype)){
            return true;   
        }
        return false;
    }
}
