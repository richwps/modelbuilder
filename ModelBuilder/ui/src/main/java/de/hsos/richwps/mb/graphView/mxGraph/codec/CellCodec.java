package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCellCodec;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Encoding and decoding of objects with properties.
 *
 * @author dziegenh
 */
public class CellCodec extends mxCellCodec {

    /**
     * Name of the (xml) attribute holding the reference id.
     */
    private final String ATTR_REFERENCE_ID = "id";

    /**
     * Contains IDs of encoded objects to prevent dublicate encodings.
     */
    private static List<mxCell> encodedObjects = new LinkedList<>();

    /**
     * Maps port IDs to decoded ports to re-assign port the right instances to
     * the model elements.
     */
    private static HashMap<String, mxCell> decodedObjects;

    public CellCodec() {
        super();
    }

    public CellCodec(Object template) {
        super(template);
    }

    public CellCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    public Object afterDecode(mxCodec dec, Node node, Object obj) {
        Object decoded = super.afterDecode(dec, node, obj);

        // identify non-global process ports
        Node idNode = node.getAttributes().getNamedItem(ATTR_REFERENCE_ID);
        if (idNode != null) {
            String id = idNode.getNodeValue();

            if (decoded instanceof mxCell) {
                mxCell cell = (mxCell) getDecodedObject(id);

                // a) object already decoded: return existing instance
                if (cell != null) {
                    return cell;

                    // b) first time port is decoded: save instance
                } else {
                    cell = (mxCell) decoded;

                    putDecodedReference(id, cell);
                }
            }
        }

        return decoded;
    }

    @Override
    public Node afterEncode(mxCodec enc, Object obj, Node node) {
        Node encoded = super.afterEncode(enc, obj, node);

        if (obj instanceof mxCell) {

            mxCell theObject = (mxCell) obj;

            if (!hasBeenEncoded(theObject)) {
                putEncodingReference(theObject);

            } else {

                Element nodeEl = (Element) node;

                // remove already encoded children
                final NodeList childNodes = nodeEl.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    nodeEl.removeChild(childNodes.item(i));
                }

                // remove already encoded attributes
                NamedNodeMap attributes = nodeEl.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    final String nodeName = attributes.item(i).getNodeName();
                    if (!nodeName.equals(ATTR_REFERENCE_ID)) {
                        nodeEl.removeAttribute(nodeName);
                    }
                }
            }

        }

        return encoded;
    }

    public static boolean hasBeenEncoded(mxCell object) {
        return encodedObjects.contains(object);
    }

    protected mxCell getDecodedObject(String id) {
        return decodedObjects.get(id);
    }

    protected void putDecodedReference(String id, mxCell object) {
        decodedObjects.put(id, object);
    }

    protected void putEncodingReference(mxCell enceodedObject) {
        encodedObjects.add(enceodedObject);
    }

    protected int getEncodingReferenceId(mxCell encodedObject) {
        return encodedObjects.indexOf(encodedObject);
    }

    public static void reset() {
        encodedObjects.clear();
        decodedObjects = new HashMap<>();
    }

}
