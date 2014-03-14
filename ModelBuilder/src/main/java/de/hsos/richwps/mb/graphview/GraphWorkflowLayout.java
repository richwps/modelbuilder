/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphProperties;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.view.mxGraph;
import java.util.Map;

class GraphLayoutComponent {

    int getWidth() {
        // TODO calculate or get bounding box width
        //mock:
        return 300;
    }

}

/**
 *
 * @author dziegenh
 */
public class GraphWorkflowLayout extends mxGraphLayout {

    private mxAnalysisGraph ag;

    public GraphWorkflowLayout(mxGraph mxgrph) {
        super(mxgrph);

        ag = new mxAnalysisGraph();
        ag.setGraph(getGraph());
        Map<String, Object> ag_p = ag.getProperties();
        ag_p.put(mxGraphProperties.DIRECTED, "1");
        ag.setProperties(ag_p);
    }

    @Override
    public void execute(Object o) {

        // A graph component contains connected cells. Graph components are not connected to each other!
        Object[][] gcs = mxGraphStructure.getGraphComponents(ag);

        for (Object[] gc : gcs) {
            // TODO layout current tree

        }

        /*
         try {
         mxGraphStructure.getSinkVertices(ag);
         System.err.println(mxGraphStructure.getSourceVertices(ag));
         } catch (StructuralException ex) {
         Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
    }

}
