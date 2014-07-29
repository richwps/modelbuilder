/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxModelCodec;
import de.hsos.richwps.mb.graphView.GraphModel;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Encodes additional GraphModel attributes (e.g. the name).
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
    protected void encodeObject(mxCodec mxcdc, Object o, Node node) {
        super.encodeObject(mxcdc, o, node);

        if (o instanceof GraphModel) {
            GraphModel model = (GraphModel) o;
            Element nodeEl = (Element) node;
            nodeEl.setAttribute("name", model.getName());
        }
        
    }

}
