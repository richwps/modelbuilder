/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.graphview;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

/**
 *
 * @author dziegenh
 */
public class GraphModel extends mxGraphModel {

    public GraphModel() {
        super();
    }


    boolean isInput(Object o) {
        if(!isVertex(o))
            return false;

        // TODO badly mocked!! Improve when ProcessDescription Model exists!!!
        return ((mxCell) o).getValue().toString().contains("In ");
    }

    boolean isOutput(Object o) {
        if(!isVertex(o))
            return false;

        // TODO badly mocked!! Improve when ProcessDescription Model exists!!!
        return ((mxCell) o).getValue().toString().contains("Out ");
    }

}
