package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.util.Collection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author dziegenh
 */
public class PropertyGroupCodec extends mxObjectCodec {

    public PropertyGroupCodec(Object template) {
        super(template);
    }

    @Override
    public Object decode(mxCodec dec, Node node, Object into) {

        Object decoded = super.decode(dec, node, into);

        if (decoded instanceof PropertyGroup) {
            PropertyGroup decodedGroup = (PropertyGroup) decoded;
            
            // get group name from attribute
            String groupName = ((Element) node).getAttribute(ObjectWithPropertiesCodec.ATTRIBUTE_OBJECTNAME);
            decodedGroup.setPropertiesObjectName(groupName);

            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);

                Object decodedChild = dec.decode(item);

                // check child type
                boolean ignoreChild = false;
                ignoreChild |= decodedChild instanceof ProcessPort;
                ignoreChild |= !(decodedChild instanceof IObjectWithProperties);

                if (!ignoreChild) {
                    decodedGroup.addObject((IObjectWithProperties) decodedChild);
                }
            }
        }

        return decoded;
    }

    @Override
    public Node encode(mxCodec enc, Object obj) {
        if (obj instanceof PropertyGroup) {
            PropertyGroup pGroup = (PropertyGroup) obj;
            
            if(pGroup.isTransient()) {
                return enc.encode("");
            }
            
            Collection<? extends IObjectWithProperties> properties = pGroup.getProperties();

            // create property array for encoding
            IObjectWithProperties[] pArr = new IObjectWithProperties[properties.size()];
            int i = 0;
            for (IObjectWithProperties property : properties) {
                pArr[i++] = property;
            }

            Element encode = (Element) encode(enc, pArr);
            // save object name as attribute
            encode.setAttribute(ObjectWithPropertiesCodec.ATTRIBUTE_OBJECTNAME, pGroup.getPropertiesObjectName());

            return encode;
        }

        return super.encode(enc, obj);
    }

}
