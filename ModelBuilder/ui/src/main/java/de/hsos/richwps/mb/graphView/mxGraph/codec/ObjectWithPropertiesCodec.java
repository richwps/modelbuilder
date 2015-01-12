package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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

    @Override
    public Object afterDecode(mxCodec dec, Node node, Object obj) {
        Object decoded = super.afterDecode(dec, node, obj);

        if (decoded instanceof OwsObjectWithProperties) {
            OwsObjectWithProperties owsObject = (OwsObjectWithProperties) decoded;
            owsObject.setToolTipText(null);
        }

        return decoded;
    }

    @Override
    public Object decode(mxCodec dec, Node node, Object into) {
        Object decoded = super.decode(dec, node, into);

        // properties: create property with generic type depending on component type
        if (decoded instanceof Property) {
            Property property = (Property) decoded;
            
            // instantiate property depending on component type
            if (null != property.getComponentType()) {
                if (property.getComponentType().equals(Property.COMPONENT_TYPE_INTEGER)) {
                    Property<Integer> intProperty = new Property<>(property.getPropertiesObjectName());
                    intProperty.setComponentType(property.getComponentType());
                    intProperty.setEditable(property.isEditable());
                    intProperty.setIsTransient(property.isTransient());

                    // TODO parse possible values from String to Integer !
//                    intProperty.setPossibleValues(property.getPossibleValues());
                    
                    try {
                        // no value in attribute: avoid nullpointer by leaving the default value
                        if (null != property.getValue()) {
                            intProperty.setValue(Integer.parseInt((String) property.getValue()));
                        }
                        
                    } catch (NumberFormatException ex) {
                        // just don't use value if it can't be parsed
                        Logger.log("Property value can't be parsed for COMPONENT_TYPE_INTEGER.");
                        intProperty.setValue(null);
                    }

                    decoded = intProperty;
                }
            }
        }

        return decoded;
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
