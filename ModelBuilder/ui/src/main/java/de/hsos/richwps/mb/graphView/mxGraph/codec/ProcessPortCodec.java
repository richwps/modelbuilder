/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dziegenh
 */
public class ProcessPortCodec extends mxObjectCodec {

    public ProcessPortCodec(Object template) {
        super(template);
    }

    public ProcessPortCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    protected void decodeAttribute(mxCodec dec, Node attr, Object obj) {
        if (obj instanceof ProcessPort && attr.getNodeName().equals(ATTR_DATATYPE)) {
            ProcessPort port = (ProcessPort) obj;
            port.setDatatype(ProcessPortDatatype.getValueByName(attr.getNodeValue()));

        } else {
            super.decodeAttribute(dec, attr, obj); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    protected void decodeChild(mxCodec dec, Node child, Object obj) {
        // ignore datatype child as the datatype is also encoded as an attribute.
        if (!child.getNodeName().equals("ProcessPortDatatype")) {
            super.decodeChild(dec, child, obj);
        }
    }

    @Override
    protected void encodeObject(mxCodec mxcdc, Object o, Node node) {
        super.encodeObject(mxcdc, o, node);

        if (o instanceof ProcessPort) {
            ProcessPort port = (ProcessPort) o;
            Element nodeEl = (Element) node;
            nodeEl.setAttribute(ATTR_DATATYPE, port.getDatatype().name());
        }

    }
    public static final String ATTR_DATATYPE = "datatype";

}
