package de.hsos.richwps.mb.richWPS.entity.specifier;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import java.math.BigInteger;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import org.n52.wps.client.transactional.BasicInputDescriptionType;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataSpecifier implements IInputSpecifier {

    private String identifier;
    private String type;
    private String title;
    private String theabstract;
    private int minOccur = 0;
    private int maxOccur = 0;

    

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
        this.minOccur = description.getMinOccurs().intValue();
        this.maxOccur = description.getMaxOccurs().intValue();
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
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAbstract() {
        return theabstract;
    }


    @Override
    public int getMinOccur() {
        return minOccur;
    }

    @Override
    public int getMaxOccur() {
        return maxOccur;
    }

    @Override
    public BasicInputDescriptionType toBasicInputDescriptionType() {
        BasicInputDescriptionType desc;
        desc = new BasicInputDescriptionType(this.identifier, this.title, BigInteger.valueOf(this.minOccur), BigInteger.valueOf(this.maxOccur));
        
        
        return desc;
    }

}
