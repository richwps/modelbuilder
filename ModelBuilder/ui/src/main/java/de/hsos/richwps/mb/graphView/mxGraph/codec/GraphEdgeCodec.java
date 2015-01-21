package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCellCodec;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import java.util.Map;
import org.w3c.dom.Node;

/**
 * Codec for GraphEdges - main task is to encode the edge port cells.
 * @author dziegenh
 */
public class GraphEdgeCodec extends mxCellCodec {

    public GraphEdgeCodec() {
        super();
    }

    public GraphEdgeCodec(Object o) {
        super(o);
    }

    public GraphEdgeCodec(Object template, String[] exclude, String[] idrefs,
            Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    
    @Override
    public Node afterEncode(mxCodec mxcdc, Object o, Node node) {
        
        Node result = super.afterEncode(mxcdc, o, node);

        // encode ports
        if(o instanceof GraphEdge) {
            GraphEdge edge = (GraphEdge) o;
            
//            Node sourceNode = mxcdc.encode(edge.getSourcePortCell());
//            node.appendChild(sourceNode);
//
//            Node targetNode = mxcdc.encode(edge.getTargetPortCell());
//            node.appendChild(targetNode);
        }

        return result;
    }

}
