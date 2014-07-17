/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.codec;

import com.mxgraph.io.mxCellCodec;
import com.mxgraph.io.mxCodec;
import de.hsos.richwps.mb.graphView.GraphEdge;
import java.util.Map;
import org.w3c.dom.Element;
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
            Element nodeEl = (Element) node;
            mxCellCodec cellCodec = new mxCellCodec();
            
            Node sourceNode = cellCodec.encode(mxcdc, edge.getSourcePortCell());
            node.appendChild(sourceNode);

            Node targetNode = cellCodec.encode(mxcdc, edge.getTargetPortCell());
            node.appendChild(targetNode);
        }

        return result;
    }

}