package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataType;
import org.n52.wps.client.transactional.OutputDescriptionTypeBuilder;

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
    private List<String> defaulttype;

    /**
     *
     */
    public static int mimetype_IDX = 0;

    /**
     *
     */
    public static int schema_IDX = 1;

    /**
     *
     */
    public static int encoding_IDX = 2;

    private SupportedComplexDataType type;

    /**
     * Constructs a new empty OuputComplexDataSpecifier.
     */
    public OutputComplexDataSpecifier() {
        this.identifier = "";
        this.theabstract = "";
        this.title = "";
        this.types = new ArrayList<>();
        this.defaulttype = new ArrayList<>();
    }

    /**
     *
     * @param description
     */
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

        net.opengis.wps.x100.ComplexDataCombinationType thedefaulttype = this.type.getDefault();
        ComplexDataDescriptionType thetype = thedefaulttype.getFormat();
        this.defaulttype = new ArrayList();
        defaulttype.add(thetype.getMimeType());
        defaulttype.add(thetype.getSchema());
        defaulttype.add(thetype.getEncoding());

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
    public OutputDescriptionType getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(OutputDescriptionType description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getTheabstract() {
        return theabstract;
    }

    /**
     *
     * @param theabstract
     */
    public void setTheabstract(String theabstract) {
        this.theabstract = theabstract;
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
        if (type.equals(defaulttype)) {
            return true;
        }
        return false;
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
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @param defaulttype
     */
    public void setDefaulttype(List<String> defaulttype) {
        this.defaulttype = defaulttype;
    }

    /**
     *
     * @return
     */
    @Override
    public OutputDescriptionType toOutputDescription() {

        //create supported type list
        ComplexDataCombinationsType supportedFormats = ComplexDataCombinationsType.Factory.newInstance();
        for (List atype : this.types) {

    
            String mimetype = (String) atype.get(InputComplexDataSpecifier.mimetype_IDX);
            String schema = (String) atype.get(InputComplexDataSpecifier.schema_IDX);
            schema=this.nullify(schema);
            String encoding = (String) atype.get(InputComplexDataSpecifier.encoding_IDX);//TRICKY wps-client-lib needs null.
            encoding=this.nullify(encoding);
            //de.hsos.richwps.mb.Logger.log("Debug::OutputComplexDataSpecifier::toOutputDesc\n mimetype, schema, encoding " + mimetype + ", " + schema + ", " + encoding);
            ComplexDataDescriptionType desctype = supportedFormats.addNewFormat();
            desctype.setEncoding(encoding);
            desctype.setMimeType(mimetype);
            desctype.setSchema(schema);
        }

        //create defaulttype
        
        String mimetype = (String) this.defaulttype.get(InputComplexDataSpecifier.mimetype_IDX);
        String schema = (String) this.defaulttype.get(InputComplexDataSpecifier.schema_IDX);
        schema=this.nullify(schema);
        String encoding = (String) this.defaulttype.get(InputComplexDataSpecifier.encoding_IDX);
        encoding=this.nullify(encoding);

        //de.hsos.richwps.mb.Logger.log("Debug::OutputComplexDataSpecifier::toOutputDesc\n mimetype, schema, encoding " + mimetype + ", " + schema + ", " + encoding);
        ComplexDataCombinationType ogcdefaulttype = OutputDescriptionTypeBuilder.createComplexDataCombiType(mimetype, encoding, schema);

        OutputDescriptionTypeBuilder description = new OutputDescriptionTypeBuilder(ogcdefaulttype, supportedFormats, this.identifier, this.title);
        description.setAbstract(this.theabstract);

        return description.getOdt();
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.identifier);
        hash = 89 * hash + Objects.hashCode(this.theabstract);
        hash = 89 * hash + Objects.hashCode(this.title);
        hash = 89 * hash + Objects.hashCode(this.types);
        hash = 89 * hash + Objects.hashCode(this.defaulttype);
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
        final OutputComplexDataSpecifier other = (OutputComplexDataSpecifier) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.theabstract, other.theabstract)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
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

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "OutputComplexDataSpecifier{" + "identifier=" + identifier + ", theabstract=" + theabstract + ", title=" + title + ", types=" + types + ", defaulttype=" + defaulttype + '}';
    }

}
