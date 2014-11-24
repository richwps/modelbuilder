package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Encoding and decoding of objects with properties.
 *
 * @author dziegenh
 */
public class ObjectWithPropertiesCodec extends mxObjectCodec {

    protected final String FIELD_PROPERTIES_NAME = "properties";
    public static final String ATTRIBUTE_OBJECTNAME = "object_name";

    public ObjectWithPropertiesCodec(Object template) {
        super(template);
    }

    public ObjectWithPropertiesCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    /**
     * Decodes the properties and adds them to the current object. Other child
     * nodes are delegated to the super class.
     *
     * @param dec
     * @param child
     * @param obj
     */
    @Override
    protected void decodeChild(mxCodec dec, Node child, Object obj) {

        Element item = (Element) child;

        if (item.getNodeName().equals("Array") && item.getAttribute("as").equals(FIELD_PROPERTIES_NAME)) {
            List decodedProperties = (List) dec.decode(item);
            for (Object decodedProperty : decodedProperties) {
                if (null != obj && (obj instanceof IObjectWithProperties) && (decodedProperty instanceof IObjectWithProperties)) {
                    IObjectWithProperties property = (IObjectWithProperties) decodedProperty;
                    IObjectWithProperties objectWithProperties = ((IObjectWithProperties) obj);

                    // set property to the current object
                    objectWithProperties.setProperty(property.getPropertiesObjectName(), property);

                }
            }

            return;
        }

        super.decodeChild(dec, child, obj);
    }

    @Override
    public Node afterEncode(mxCodec enc, Object obj, Node node) {
        Node encoded = super.afterEncode(enc, obj, node);

        if (obj instanceof OwsObjectWithProperties) {

            // TODO don't add properties to already encoded ports !!!
            OwsObjectWithProperties theObject = (OwsObjectWithProperties) obj;

            Element encodedProperties = (Element) enc.encode(theObject.getProperties());
            encodedProperties.setAttribute("as", FIELD_PROPERTIES_NAME);
            encoded.appendChild(encodedProperties);

            // don't persist generated tool tip texts
            Element nodeEl = (Element) node;
            nodeEl.removeAttribute("toolTipText");

        }

        return encoded;
    }

    @Override
    public Object beforeEncode(mxCodec enc, Object obj, Node node) {
        // TODO save object name as attribute?
        if (obj instanceof IObjectWithProperties) {
//            IObjectWithProperties objectWithProperties = (IObjectWithProperties) obj;

//            if (objectWithProperties.isTransient()) {
//                return null;
//            }
//
//            String name = objectWithProperties.getPropertiesObjectName();
//            Element nodeEl = (Element) node;
//            nodeEl.setAttribute(ATTRIBUTE_OBJECTNAME, name);
        }

        return super.beforeEncode(enc, obj, node);
    }

    public static String getValueForViews(String value) {
        return (null == value || value.isEmpty()) ? "-" : value.trim();
    }

}
