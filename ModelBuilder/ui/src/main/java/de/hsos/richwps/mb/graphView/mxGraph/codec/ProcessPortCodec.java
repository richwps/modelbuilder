package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
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

    public static final String ATTR_DATATYPE = "datatype";

    public ProcessPortCodec(Object template) {
        super(template);
    }

    public ProcessPortCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    protected void decodeAttribute(mxCodec dec, Node attr, Object obj) {

        // decode datatype attribute
        if (obj instanceof ProcessPort) {
            ProcessPort port = (ProcessPort) obj;
            if (attr.getNodeName().equals(ATTR_DATATYPE)) {
                port.setDatatype(ProcessPortDatatype.getValueByName(attr.getNodeValue()));

            } else {
                super.decodeAttribute(dec, attr, obj);
            }
        }
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

    @Override
    public Node beforeDecode(mxCodec dec, Node node, Object obj) {
        // compatibility hook for older models without reference ids:
        // set id as reference_id
        Element nodeEl = (Element) node;
        String refAttr = nodeEl.getAttribute(ATTR_REFERENCE_ID);
        if(null == refAttr || refAttr.isEmpty()) {
            String id = nodeEl.getAttribute("port_id");
            nodeEl.setAttribute(ATTR_REFERENCE_ID, id);
        }
        
        return super.beforeDecode(dec, node, obj);
    }

    
    
}
