/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.codec;

import com.mxgraph.io.mxCellCodec;
import com.mxgraph.io.mxCodec;
import java.util.Map;
import org.w3c.dom.Node;

/**
 *
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

    // TODO fix missing attributes bug! (here?)
    @Override
    protected void encodeValue(mxCodec mxcdc, Object o, String string, Object o1, Node node) {
        super.encodeValue(mxcdc, o, string, o1, node); //To change body of generated methods, choose Tools | Templates.
        System.out.println(node);
    }

    // TODO fix missing attributes bug! (here?)
    @Override
    public Object beforeEncode(mxCodec mxcdc, Object o, Node node) {
        return  super.beforeEncode(mxcdc, o, node);
    }






}
