/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;

/**
 *
 * @author dziegenh
 */
public class GraphModel extends mxGraphModel {
    private String name;

    public GraphModel() {
        super();
    }

    public GraphModel(mxGraphModel modelToClone) throws CloneNotSupportedException {
        super();

        mergeChildren((mxICell) modelToClone.getRoot(), (mxCell) getRoot(), true);
    }

    public mxGraphModel cloneMxgraphModel() throws CloneNotSupportedException {
        mxGraphModel clone = new mxGraphModel();
        clone.mergeChildren((mxCell) getRoot(), (mxICell) clone.getRoot(), true);
        return clone;
    }


    boolean isInput(Object o) {
        if (!isVertex(o)) {
            return false;
        }

        if(getValue(o) instanceof ProcessPort) {
            return ((ProcessPort) getValue(o)).isInput();
        }

        return false;
    }

    boolean isOutput(Object o) {
        if (!isVertex(o)) {
            return false;
        }

        if(getValue(o) instanceof ProcessPort) {
            return ((ProcessPort) getValue(o)).isOutput();
        }

        return false;
    }

    /**
     * Return true if the cell is a process vertex (i.e. no edge and no port),.
     *
     * @param cell
     * @return
     */
    boolean isProcess(Object o) {
        return isVertex(o) && !isInput(o) && !isOutput(o);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
