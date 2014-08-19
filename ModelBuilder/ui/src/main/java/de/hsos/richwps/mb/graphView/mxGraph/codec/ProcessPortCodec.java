/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dziegenh
 */
public class ProcessPortCodec extends mxObjectCodec {

    /**
     * 
     */
    private final String ATTR_PORT_ID = "port_id";

    /**
     * Contains IDs of encoded ports to prevent dublicate encodings.
     */
    private static List<ProcessPort> encodedPorts = new LinkedList<>();

    /**
     * Maps port IDs to decoded ports to re-assign port the right instances to the model elements.
     */
    private static HashMap<String, ProcessPort> decodedPorts;
    
    public ProcessPortCodec(Object template) {
        super(template);
    }

    public ProcessPortCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    protected void decodeAttribute(mxCodec dec, Node attr, Object obj) {
        // decode datatype attribute
        if (obj instanceof ProcessPort && attr.getNodeName().equals(ATTR_DATATYPE)) {
            ProcessPort port = (ProcessPort) obj;
            port.setDatatype(ProcessPortDatatype.getValueByName(attr.getNodeValue()));

        } else {
            super.decodeAttribute(dec, attr, obj);
        }
    }

    @Override
    protected void decodeChild(mxCodec dec, Node child, Object obj) {
        // ignore datatype child (if exists), as the datatype is encoded as an attribute.
        if (!child.getNodeName().equals("ProcessPortDatatype")) {
            super.decodeChild(dec, child, obj);
        }
    }

    public static final String ATTR_DATATYPE = "datatype";

    public static void reset() {
        encodedPorts.clear();
        decodedPorts = new HashMap<>();
    }

    @Override
    public Object afterDecode(mxCodec dec, Node node, Object obj) {
        obj = super.afterDecode(dec, node, obj); //To change body of generated methods, choose Tools | Templates.

        // identify non-global process ports
        Node idNode = node.getAttributes().getNamedItem(ATTR_PORT_ID);
        if (idNode != null) {
            String id = idNode.getNodeValue();

            if (obj instanceof ProcessPort) {
                ProcessPort port = decodedPorts.get(id);

                // a) port already decoded
                if (port != null) {
                    return port;

                    // b) first time port is decoded
                } else {
                    decodedPorts.put(id, (ProcessPort) obj);
                }
            }
        }

        return obj;
    }

    @Override
    public Object beforeEncode(mxCodec enc, Object obj, Node node) {

        // add port id to node
        if (obj instanceof ProcessPort) {// && !((ProcessPort) obj).isGlobal()) {

            // a) existing ID (port already encoded)
            if (encodedPorts.contains(obj)) {
                int id = encodedPorts.indexOf(obj);
                Element nodeEl = (Element) node;
                nodeEl.setAttribute(ATTR_PORT_ID, "" + id);
                return obj;

                // b) create new ID (port to be encoded)
            } else {
//                encodedPorts.add((ProcessPort) obj);
                int id = encodedPorts.size(); // current node will be the next list entry
                Element nodeEl = (Element) node;
                nodeEl.setAttribute(ATTR_PORT_ID, "" + id);
            }
        }

        return super.beforeEncode(enc, obj, node);
    }

    @Override
    public Node afterEncode(mxCodec enc, Object obj, Node node) {
        if (obj instanceof ProcessPort) {
            ProcessPort port = (ProcessPort) obj;

//            if (!port.isGlobal() &&
            if(!encodedPorts.contains(obj)) {
                encodedPorts.add(port);
            }
        }

        return super.afterEncode(enc, obj, node);
    }

    @Override
    public Node encode(mxCodec enc, Object obj) {
        Node node = super.encode(enc, obj);
        return node;
    }

    @Override
    protected void encodeValue(mxCodec enc, Object obj, String fieldname, Object value, Node node) {
        if (obj instanceof ProcessPort) {
            // don't encode port (value) again...
            if (encodedPorts.contains(obj)) {
                return;
            }

            // encode datatype enum value as attribute (default encoding would be a child node with no value!)
            if (fieldname.equals(ATTR_DATATYPE)) {
                ProcessPort port = (ProcessPort) obj;
                Element nodeEl = (Element) node;
                nodeEl.setAttribute(ATTR_DATATYPE, port.getDatatype().name());
                return;
            }
        }

        super.encodeValue(enc, obj, fieldname, value, node);
    }

}
