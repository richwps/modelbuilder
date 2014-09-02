package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import org.n52.wps.client.transactional.InputDescriptionTypeBuilder;

/**
 * Specifies a complexdata input.
 *
 * @author dalcacer
 */
public class InputComplexDataSpecifier implements IInputSpecifier {

    private InputDescriptionType description;
    private SupportedComplexDataInputType ogctype;

    private String identifier;
    private String title;
    private String theabstract;
    private int minOccur = 0;
    private int maxOccur = 0;
    private int maximumMegabytes = 0;

    private List<String> defaulttype;
    private List<List> types;
    public final static int mimetype_IDX = 0;
    public final static int schema_IDX = 1;
    public final static int encoding_IDX = 2;

    /**
     * Constructs an empty InputSpecifier.
     */
    public InputComplexDataSpecifier() {

        this.maximumMegabytes = BigInteger.ONE.intValue();
        this.identifier = "";
        this.title = "";
        this.theabstract = "";
        this.defaulttype = new ArrayList();
        this.types = new ArrayList();
    }

    /**
     * Constructs a new InputSpecifier for complex data.
     *
     * @param description 52n InputDescriptionType.
     */
    public InputComplexDataSpecifier(InputDescriptionType description) {
        this.description = description;
        this.ogctype = description.getComplexData();

        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.title = description.getTitle().getStringValue();

        if (this.ogctype != null) {
            if (this.ogctype.getMaximumMegabytes() != null) {
                this.maximumMegabytes = this.ogctype.getMaximumMegabytes().intValue();
            }

        }
        ComplexDataCombinationsType subtypes = this.ogctype.getSupported();
        ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

        this.types = new ArrayList<>();
        for (ComplexDataDescriptionType thetype : subtypes_) {
            List<String> atype = new ArrayList();
            atype.add(thetype.getMimeType());
            atype.add(thetype.getSchema());
            atype.add(thetype.getEncoding());
            this.types.add(atype);
        }
        net.opengis.wps.x100.ComplexDataCombinationType thedefaulttype = this.ogctype.getDefault();
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
        return this.title;
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
        return type.equals(defaulttype);
    }

    public void setType(SupportedComplexDataInputType type) {
        this.ogctype = type;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setTitle(String titel) {
        this.title = titel;
    }

    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

    public void setMinOccur(int minOccur) {
        this.minOccur = minOccur;
    }

    public void setMaxOccur(int maxOccur) {
        this.maxOccur = maxOccur;
    }

    public void setDefaulttype(List<String> defaulttype) {
        this.defaulttype = defaulttype;
    }

    public void setTypes(List<List> types) {
        this.types = types;
    }

    public int getMaximumMegabytes() {
        return maximumMegabytes;
    }

    public void setMaximumMegabytes(int maximumMegabytes) {
        this.maximumMegabytes = maximumMegabytes;
    }

    @Override
    public InputDescriptionType toInputDescription() {
        InputDescriptionTypeBuilder desc;
        desc = new InputDescriptionTypeBuilder(this.identifier, this.title, BigInteger.valueOf(this.minOccur), BigInteger.valueOf(this.maxOccur));
        desc.setAbstract(this.theabstract);

        //create supported type list
        List<ComplexDataDescriptionType> supportedFormatList = new ArrayList();
        for (List atype : this.types) {
            String mimetype = (String) atype.get(InputComplexDataSpecifier.mimetype_IDX);
            String schema = (String) atype.get(InputComplexDataSpecifier.schema_IDX);
            String encoding = (String) atype.get(InputComplexDataSpecifier.encoding_IDX);
            ComplexDataDescriptionType ogctype = InputDescriptionTypeBuilder.createComplexDataDescriptionType(mimetype, encoding, schema);
            supportedFormatList.add(ogctype);
        }

        //create defaulttype
        String mimetype = (String) this.defaulttype.get(InputComplexDataSpecifier.mimetype_IDX);
        String schema = (String) this.defaulttype.get(InputComplexDataSpecifier.schema_IDX);
        String encoding = (String) this.defaulttype.get(InputComplexDataSpecifier.encoding_IDX);
        ComplexDataDescriptionType ogcdefaulttype = InputDescriptionTypeBuilder.createComplexDataDescriptionType(mimetype, encoding, schema);

        desc.addNewComplexData(ogcdefaulttype, supportedFormatList, BigInteger.valueOf(this.maximumMegabytes));
        return desc.getIdt();
    }

    @Override
    public String toString() {
        return "InputComplexDataSpecifier{" + " identifier=" + identifier + ", title=" + title + ", theabstract=" + theabstract + ", minOccur=" + minOccur + ", maxOccur=" + maxOccur + ", maximumMegabytes=" + maximumMegabytes + ", defaulttype=" + defaulttype + ", types=" + types + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.identifier);
        hash = 71 * hash + Objects.hashCode(this.title);
        hash = 71 * hash + Objects.hashCode(this.theabstract);
        hash = 71 * hash + this.minOccur;
        hash = 71 * hash + this.maxOccur;
        hash = 71 * hash + this.maximumMegabytes;
        hash = 71 * hash + Objects.hashCode(this.defaulttype);
        hash = 71 * hash + Objects.hashCode(this.types);
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
        final InputComplexDataSpecifier other = (InputComplexDataSpecifier) obj;

        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.theabstract, other.theabstract)) {
            return false;
        }
        if (this.minOccur != other.minOccur) {
            return false;
        }
        if (this.maxOccur != other.maxOccur) {
            return false;
        }
        if (this.maximumMegabytes != other.maximumMegabytes) {
            return false;
        }
        if (!Objects.equals(this.defaulttype, other.defaulttype)) {
            return false;
        }
        return Objects.equals(this.types, other.types);
    }
}
