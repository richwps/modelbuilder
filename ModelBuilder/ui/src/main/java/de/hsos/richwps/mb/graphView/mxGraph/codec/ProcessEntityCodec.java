package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessEntity;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Encodes additional port attributes (mainly the datatype description) and
 * manages the assignment of unique port entities using their identifiers.
 *
 * @author dziegenh
 */
public class ProcessEntityCodec extends ObjectWithPropertiesCodec {

    public ProcessEntityCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    public ProcessEntityCodec(Object o) {
        super(o);
    }

    public ProcessEntityCodec() {
        super(null);
    }

    @Override
    public Object beforeEncode(mxCodec enc, Object obj, Node node) {
        if (null != obj && obj instanceof ProcessEntity) {
            ((ProcessEntity) obj).setToolTipText("");
        }

        return super.beforeEncode(enc, obj, node);
    }

    @Override
    public Node encode(mxCodec enc, Object obj) {

        if (obj instanceof OwsObjectWithProperties) {
            OwsObjectWithProperties owsObj = (OwsObjectWithProperties) obj;

            if (hasBeenEncoded(owsObj)) {

                // remove already encoded children
                Element encoded = (Element) super.encode(enc, obj);
                NodeList childNodes = encoded.getChildNodes();
                while (childNodes.getLength() > 0) {
                    encoded.removeChild(childNodes.item(0));
                }

                return encoded;
            }
        }

        return super.encode(enc, obj);

    }

    @Override
    public Object afterDecode(mxCodec dec, Node node, Object obj) {
        if (null != obj && obj instanceof ProcessEntity) {
            ((ProcessEntity) obj).setToolTipText(null);
        }

        return super.afterDecode(dec, node, obj);
    }

}
