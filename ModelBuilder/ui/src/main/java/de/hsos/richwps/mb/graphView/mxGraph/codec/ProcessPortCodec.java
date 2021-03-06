package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.control.ProcessPortFactory;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyHelper;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Encodes additional port attributes (mainly the datatype description) and
 * manages the assignment of unique port entities using their identifiers.
 *
 * @author dziegenh
 */
public class ProcessPortCodec extends ObjectWithPropertiesCodec {

    @Deprecated
    public static final String ATTR_DATATYPE = "datatype";
    private static final String ATTR_FLOWINPUT = "flowInput";

    public ProcessPortCodec(Object template) {
        super(template);
    }

    public ProcessPortCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    protected void decodeAttribute(mxCodec dec, Node attr, Object obj) {

        // decode datatype attribute
        if (obj instanceof de.hsos.richwps.mb.entity.oldVersions.ProcessPort) {
            de.hsos.richwps.mb.entity.oldVersions.ProcessPort port = (de.hsos.richwps.mb.entity.oldVersions.ProcessPort) obj;
            if (attr.getNodeName().equals(ATTR_DATATYPE)) {
                port.setDatatype(ProcessPortDatatype.getValueByName(attr.getNodeValue()));
                return;
            }

        } else if (attr.getNodeName().equals(ATTR_FLOWINPUT)) {
            // ignore "flowInput" which is set by the specific sub classes
            return;
        }

        super.decodeAttribute(dec, attr, obj);
    }

    @Override
    protected void decodeChild(mxCodec dec, Node child, Object obj) {
        // ignore datatype child (if exists), as the datatype is encoded as an attribute.
        if (!child.getNodeName().equals("ProcessPortDatatype")) {
            super.decodeChild(dec, child, obj);
        }
    }

    @Override
    protected void encodeValue(mxCodec enc, Object obj, String fieldname, Object value, Node node) {
        if (obj instanceof ProcessPort) {
            // don't encode port (value) again...
            if (hasBeenEncoded((ProcessPort) obj)) {
                return;
            }
        }

        super.encodeValue(enc, obj, fieldname, value, node);
    }

    @Override
    public Node beforeDecode(mxCodec dec, Node node, Object obj) {
        // compatibility hook for older models without reference ids:
        // set id as reference_id
        Element nodeEl = (Element) node;
        String refAttr = nodeEl.getAttribute(ATTR_REFERENCE_ID);
        if (null == refAttr || refAttr.isEmpty()) {
            String id = nodeEl.getAttribute("port_id");
            nodeEl.setAttribute(ATTR_REFERENCE_ID, id);
        }

        return super.beforeDecode(dec, node, obj);
    }

    @Override
    public Object afterDecode(mxCodec dec, Node node, Object obj) {
        try {
            // workaround to support older model versions
            if (obj instanceof de.hsos.richwps.mb.entity.oldVersions.ProcessPort) {

                Element nodeEl = (Element) node;
                String id = nodeEl.getAttribute(ATTR_REFERENCE_ID);
                IObjectWithProperties decodedObject = getDecodedObject(id);
                if (null != decodedObject) {
                    return decodedObject;
                }

                de.hsos.richwps.mb.entity.oldVersions.ProcessPort decodedOldPort = (de.hsos.richwps.mb.entity.oldVersions.ProcessPort) obj;
                ProcessPort port;
                boolean isInput = decodedOldPort.isFlowInput();
                boolean isGlobal = decodedOldPort.isGlobal();
                ProcessPortDatatype datatype = decodedOldPort.getDatatype();

                if (isGlobal && isInput) {
                    port = ProcessPortFactory.createGlobalInputPort(datatype);
                } else if (isGlobal && !isInput) {
                    port = ProcessPortFactory.createGlobalOutputPort(datatype);
                } else if (!isGlobal && isInput) {
                    port = ProcessPortFactory.createLocalInputPort(datatype);
                } else {
                    port = ProcessPortFactory.createLocalOutputPort(datatype);
                }

                port.copyValuesFrom(decodedOldPort);
                obj = port;
            }
        } catch (LoadDataTypesException ex) {
            AppEventService.getInstance().fireAppEvent(AppConstants.LOAD_DATATYPES_ERROR, AppConstants.INFOTAB_ID_EDITOR, AppEvent.PRIORITY.URGENT);
        }

        // match port properties to update older model versions
        if (obj instanceof ProcessPort) {

            // get the port's property names as a list
            final ProcessPort port = (ProcessPort) obj;
            List<String> propertyNames = PropertyHelper.getPropertyNames((IObjectWithProperties) obj);

            // get properties list of the prototype of the current port type
            List<String> prototypeProperties = getPrototypeProperties(port);

            // check each loaded property against the properties of the port prototype
            for (String property : propertyNames) {
                // no prototype property: remove it from the port
                if (!prototypeProperties.contains(property)) {
                    port.setProperty(property, null);

                }
//                else {
//                    prototypeProperties.remove(property);
//                }
            }

//            for (String property : prototypeProperties) {
//                Logger.log("PROPERTY MATCH ERROR:  prototype property '" + property + "' is missing!");
//            }
        }

        return super.afterDecode(dec, node, obj);
    }

    private List<String> getPrototypeProperties(ProcessPort port) {
        try {
            Constructor<? extends ProcessPort> constructor = port.getClass().getConstructor(new Class[]{boolean.class});
            ProcessPort prototype = constructor.newInstance(port.isGlobal());
            return PropertyHelper.getPropertyNames(prototype);

        } catch (Exception ex) {
            // ignore, null will be returned below.
        }

        return null;
    }

}
