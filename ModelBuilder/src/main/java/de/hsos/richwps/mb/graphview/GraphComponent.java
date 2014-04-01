/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.graphview;

import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * Only accepts the custom graph.
 * @author dziegenh
 */
public class GraphComponent extends mxGraphComponent {

    public GraphComponent(mxGraph mxgrph) {
        super((Graph) mxgrph);
    }

    @Override
    protected mxConnectionHandler createConnectionHandler() {
        connectionHandler = new GraphConnectionHandler(this);
        return connectionHandler;
    }


    Graph getTheGraph() {
        return (Graph) graph;
    }

}
