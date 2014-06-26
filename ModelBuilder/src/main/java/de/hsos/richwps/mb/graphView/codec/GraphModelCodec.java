/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxModelCodec;
import java.util.Map;
import org.w3c.dom.Node;

/**
 *
 * @author dziegenh
 */
public class GraphModelCodec extends mxModelCodec {

    public GraphModelCodec() {
        super();
    }

    public GraphModelCodec(Object o) {
        super(o);
    }

    public GraphModelCodec(Object template, String[] exclude, String[] idrefs,
            Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    public Node beforeDecode(mxCodec mxcdc, Node node, Object o) {
        Node result = super.beforeDecode(mxcdc, node, o);

        

        return result;
    }



}
