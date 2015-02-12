package de.hsos.richwps.mb.richWPS.entity.impl.descriptions;


import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import org.n52.wps.client.richwps.InputDescriptionTypeBuilder;

/**
 * Specifies a complexdata input.
 *
 * @author dalcacer
 */
public class InputComplexDataDescription implements IInputDescription {

    private String identifier;
    private String title;
    private String theabstract;
    private int minOccur = 0;
    private int maxOccur = 0;
    private int maximumMegabytes = 0;
    private List<String> defaulttype;
    private List<List> types;

    /**
     *
     */
    public final static int mimetype_IDX = 0;

    /**
     *
     */
    public final static int schema_IDX = 1;

    /**
     *
     */
    public final static int encoding_IDX = 2;

    /**
     * Constructs an empty InputSpecifier.
     */
    public InputComplexDataDescription() {

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
    public InputComplexDataDescription(final InputDescriptionType description) {
        SupportedComplexDataInputType ogctype = description.getComplexData();

        this.identifier = description.getIdentifier().getStringValue();
        if (description.getAbstract() != null) {
            this.theabstract = description.getAbstract().getStringValue();
        } else {
            this.theabstract = "";
        }
        this.title = description.getTitle().getStringValue();

        if (ogctype != null) {
            if (ogctype.getMaximumMegabytes() != null) {
                this.maximumMegabytes = ogctype.getMaximumMegabytes().intValue();
            }

        }
        ComplexDataCombinationsType subtypes = ogctype.getSupported();
        if (subtypes != null) {
            ComplexDataDescriptionType[] subtypes_ = subtypes.getFormatArray();

            this.types = new ArrayList<>();
            for (ComplexDataDescriptionType thetype : subtypes_) {
                List<String> atype = new ArrayList();
                atype.add(thetype.getMimeType());
                atype.add(thetype.getSchema());
                atype.add(thetype.getEncoding());
                this.types.add(atype);
            }
        }
        net.opengis.wps.x100.ComplexDataCombinationType thedefaulttype = ogctype.getDefault();
        ComplexDataDescriptionType thetype = thedefaulttype.getFormat();
        this.defaulttype = new ArrayList();
        defaulttype.add(thetype.getMimeType());
        defaulttype.add(thetype.getSchema());
        defaulttype.add(thetype.getEncoding());

        this.minOccur = description.getMinOccurs().intValue();
        this.maxOccur = description.getMaxOccurs().intValue();

    }

    /**
     *
     * @return
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAbstract() {
        return this.theabstract;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @return
     */
    @Override
    public int getMinOccur() {
        return minOccur;
    }

    /**
     *
     * @return
     */
    @Override
    public int getMaxOccur() {
        return maxOccur;
    }

    /**
     *
     * @return
     */
    public List<String> getDefaultType() {
        return this.defaulttype;
    }

    /**
     *
     * @return
     */
    public List<List> getTypes() {
        return this.types;
    }

    /**
     *
     * @param type
     * @return
     */
    public boolean isDefaultType(List type) {
        return type.equals(defaulttype);
    }

    /**
     *
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @param titel
     */
    public void setTitle(String titel) {
        this.title = titel;
    }

    /**
     *
     * @param theabstract
     */
    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

    /**
     *
     * @param minOccur
     */
    public void setMinOccur(int minOccur) {
        this.minOccur = minOccur;
    }

    /**
     *
     * @param maxOccur
     */
    public void setMaxOccur(int maxOccur) {
        this.maxOccur = maxOccur;
    }

    /**
     *
     * @param defaulttype
     */
    public void setDefaulttype(List<String> defaulttype) {
        this.defaulttype = defaulttype;
    }

    /**
     *
     * @param types
     */
    public void setTypes(List<List> types) {
        this.types = types;
    }

    /**
     *
     * @return
     */
    public int getMaximumMegabytes() {
        return maximumMegabytes;
    }

    /**
     *
     * @param maximumMegabytes
     */
    public void setMaximumMegabytes(int maximumMegabytes) {
        this.maximumMegabytes = maximumMegabytes;
    }

    /**
     * Returns an ogc/wps-compliant input-description.
     *
     * @return InputDescriptionType ogc/wps-compliant input-description.
     */
    @Override
    public InputDescriptionType toInputDescription() {
        InputDescriptionTypeBuilder desc;
        desc = new InputDescriptionTypeBuilder(this.identifier, this.title, BigInteger.valueOf(this.minOccur), BigInteger.valueOf(this.maxOccur));
        desc.setAbstract(this.theabstract);

        //create supported type list
        List<ComplexDataDescriptionType> supportedFormatList = new ArrayList();
        for (List atype : this.types) {
            String mimetype = (String) atype.get(InputComplexDataDescription.mimetype_IDX);
            String schema = (String) atype.get(InputComplexDataDescription.schema_IDX);
            schema = this.nullify(schema);
            String encoding = (String) atype.get(InputComplexDataDescription.encoding_IDX);
            encoding = this.nullify(encoding);

            ComplexDataDescriptionType ogctype = InputDescriptionTypeBuilder.createComplexDataDescriptionType(mimetype, encoding, schema);
            supportedFormatList.add(ogctype);
        }

        //create defaulttype
        String mimetype = (String) this.defaulttype.get(InputComplexDataDescription.mimetype_IDX);
        String schema = (String) this.defaulttype.get(InputComplexDataDescription.schema_IDX);
        schema = this.nullify(schema);
        String encoding = (String) this.defaulttype.get(InputComplexDataDescription.encoding_IDX);
        encoding = this.nullify(encoding);

        ComplexDataDescriptionType ogcdefaulttype = InputDescriptionTypeBuilder.createComplexDataDescriptionType(mimetype, encoding, schema);

        desc.addNewComplexData(ogcdefaulttype, supportedFormatList, BigInteger.valueOf(this.maximumMegabytes));
        return desc.getIdt();
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "InputComplexDataSpecifier{" + " identifier=" + identifier + ", title=" + title + ", theabstract=" + theabstract + ", minOccur=" + minOccur + ", maxOccur=" + maxOccur + ", maximumMegabytes=" + maximumMegabytes + ", defaulttype=" + defaulttype + ", types=" + types + '}';
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InputComplexDataDescription other = (InputComplexDataDescription) obj;

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
        return true;
    }

    //TRICKY wps-client-lib needs null.
    private String nullify(String astring) {
        if (astring == null) {
            return astring;
        }
        if (astring.isEmpty()) {
            return null;
        }
        return astring;
    }
}
