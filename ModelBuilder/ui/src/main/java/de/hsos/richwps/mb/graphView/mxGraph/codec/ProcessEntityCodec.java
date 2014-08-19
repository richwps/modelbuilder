/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.graphView.mxGraph.codec;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxObjectCodec;
import java.util.Map;
import org.w3c.dom.Node;

/**
 *
 * @author dziegenh
 */
public class ProcessEntityCodec extends mxObjectCodec {

    public ProcessEntityCodec(Object template) {
        super(template);
    }

    public ProcessEntityCodec(Object template, String[] exclude, String[] idrefs, Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    protected void encodeObject(mxCodec enc, Object obj, Node node) {
        // reset temporary port ID list etc.
        ProcessPortCodec.reset();
        super.encodeObject(enc, obj, node);
    }
}
