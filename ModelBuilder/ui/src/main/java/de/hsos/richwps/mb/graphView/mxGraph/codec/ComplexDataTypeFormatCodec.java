package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCellCodec;
import com.mxgraph.io.mxCodec;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Codec for GraphEdges - main task is to encode the edge port cells.
 *
 * @author dziegenh
 */
public class ComplexDataTypeFormatCodec extends mxCellCodec {

    public ComplexDataTypeFormatCodec() {
        super();
    }

    public ComplexDataTypeFormatCodec(Object o) {
        super(o);
    }

    public ComplexDataTypeFormatCodec(Object template, String[] exclude, String[] idrefs,
            Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    public Node afterEncode(mxCodec mxcdc, Object o, Node node) {

        Node result = super.afterEncode(mxcdc, o, node);

        // remove tooltiptext
        if (o instanceof ComplexDataTypeFormat) {
            Element nodeEl = (Element) node;
            nodeEl.removeAttribute(ATTR_TTT);
        }

        return result;
    }

    /**
     * Name of the toolTipText attribute.
     */
    private final String ATTR_TTT = "toolTipText";

}
