package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxModelCodec;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Encodes additional GraphModel attributes (e.g. the name).
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
    public Object decode(mxCodec dec, Node node, Object into) {
        ProcessPortCodec.reset();
        return super.decode(dec, node, into); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void encodeObject(mxCodec mxcdc, Object o, Node node) {
        super.encodeObject(mxcdc, o, node);

        if (o instanceof GraphModel) {

            // reset temporary port ID list etc.
            ProcessPortCodec.reset();

            GraphModel model = (GraphModel) o;
            Element nodeEl = (Element) node;
            nodeEl.setAttribute("name", model.getName());
        }

    }

}
