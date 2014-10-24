/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    @Override
    public Node beforeDecode(mxCodec dec, Node node, Object obj) {
//        NodeList arrays = ((Element) node).getElementsByTagName("Array");
        NodeList childNodes = ((Element) node).getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element item = (Element) childNodes.item(i);

                if (item.getNodeName().equals("Array") && item.getAttribute("as").equals(FIELD_PROPERTIES_NAME)) {
                    List decodedProperties = (List) dec.decode(item);
                    for (Object decodedProperty : decodedProperties) {
                        if (null != obj && (obj instanceof IObjectWithProperties) && (decodedProperty instanceof IObjectWithProperties)) {
                            IObjectWithProperties property = (IObjectWithProperties) decodedProperty;
                            IObjectWithProperties objectWithProperties = ((IObjectWithProperties) obj);

//                            objectWithProperties.setPropertiesObjectName(item.getAttribute(ATTRIBUTE_OBJECTNAME));
                            // set property to the current object
                            objectWithProperties.setProperty(property.getPropertiesObjectName(), property);

                        }
                    }

                    node.removeChild(item);
                }
            }
        }

        return super.beforeDecode(dec, node, obj);
    }

    @Override
    public Node afterEncode(mxCodec enc, Object obj, Node node) {
        Node encoded = super.afterEncode(enc, obj, node);

        if (obj instanceof OwsObjectWithProperties) {

        // TODO don't add properties to already encoded ports !!!
//            if (!(obj instanceof ProcessPort) || !ProcessPortCodec.hasBeenEncoded((ProcessPort) obj)) {

                OwsObjectWithProperties theObject = (OwsObjectWithProperties) obj;

                Element encodedProperties = (Element) enc.encode(theObject.getProperties());
                encodedProperties.setAttribute("as", FIELD_PROPERTIES_NAME);
                encoded.appendChild(encodedProperties);
//            }
        }

        return encoded;
    }

    @Override
    public Object beforeEncode(mxCodec enc, Object obj, Node node) {
        // save object name as attribute
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

        return super.beforeEncode(enc, obj, node); //To change body of generated methods, choose Tools | Templates.
    }

}
