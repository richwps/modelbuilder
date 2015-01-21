package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import java.util.HashMap;
import java.util.LinkedList;
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

    /**
     * Name of the (xml) attribute holding the reference id.
     */
    protected final String ATTR_REFERENCE_ID = "reference_id";

    protected final String FIELD_PROPERTIES_NAME = "properties";

    public static final String ATTRIBUTE_OBJECTNAME = "object_name";

    /**
     * Contains IDs of encoded objects to prevent dublicate encodings.
     */
    private static List<IObjectWithProperties> encodedObjects = new LinkedList<>();

    /**
     * Maps port IDs to decoded ports to re-assign port the right instances to
     * the model elements.
     */
    private static HashMap<String, IObjectWithProperties> decodedObjects;

    private static long encodingReferenceId = 0;

    public ObjectWithPropertiesCodec(Object template) {
        super(template);
    }

    public ObjectWithPropertiesCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    public Object afterDecode(mxCodec dec, Node node, Object obj) {
        Object decoded = super.afterDecode(dec, node, obj);

        // identify non-global process ports
        Node idNode = node.getAttributes().getNamedItem(ATTR_REFERENCE_ID);
        if (idNode != null) {
            String id = idNode.getNodeValue();

            if (decoded instanceof OwsObjectWithProperties) {
                OwsObjectWithProperties owsObject = (OwsObjectWithProperties) getDecodedObject(id);

                // a) object already decoded: return existing instance
                if (owsObject != null) {
                    return owsObject;

                    // b) first time port is decoded: save instance
                } else {
                    owsObject = (OwsObjectWithProperties) decoded;

                    owsObject.setToolTipText(null);
                    putDecodedReference(id, owsObject);
                }
            }
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

            OwsObjectWithProperties theObject = (OwsObjectWithProperties) obj;
            long referenceId;

            if (!hasBeenEncoded(theObject)) {
                Element encodedProperties = (Element) enc.encode(theObject.getProperties());
                encodedProperties.setAttribute("as", FIELD_PROPERTIES_NAME);
                encoded.appendChild(encodedProperties);

                putEncodingReference(theObject);
                referenceId = getNextEncodingReferenceId();

            } else {
                referenceId = getEncodingReferenceId(theObject);
            }

            // don't persist generated tool tip texts
            Element nodeEl = (Element) node;
            nodeEl.removeAttribute("toolTipText");
            nodeEl.setAttribute(ATTR_REFERENCE_ID, "" + referenceId);
        }

        return encoded;
    }

    public static String getValueForViews(String value) {
        return (null == value || value.isEmpty()) ? "-" : value.trim();
    }

    public static boolean hasBeenEncoded(IObjectWithProperties object) {
        return encodedObjects.contains(object);
    }

    protected IObjectWithProperties getDecodedObject(String id) {
        return decodedObjects.get(id);
    }

    protected void putDecodedReference(String id, IObjectWithProperties object) {
        decodedObjects.put(id, object);
    }

    protected void putEncodingReference(IObjectWithProperties enceodedObject) {
        encodedObjects.add(enceodedObject);
    }

    protected int getEncodingReferenceId(IObjectWithProperties encodedObject) {
        return encodedObjects.indexOf(encodedObject);
    }

    protected long getNextEncodingReferenceId() {
        return encodingReferenceId++;
    }

    public static void reset() {
        encodedObjects.clear();
        decodedObjects = new HashMap<>();
        encodingReferenceId = 0;
    }

}
