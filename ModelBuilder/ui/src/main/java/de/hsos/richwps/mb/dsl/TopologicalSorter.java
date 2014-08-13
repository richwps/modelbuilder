package de.hsos.richwps.mb.dsl;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TopologicalSorter {

    /**
     * unresolved dependencies, used for topological sorting
     */
    protected Map<mxICell, List<mxICell>> unresolvedDependencies;
    /**
     * Set of processes with resolved dependencies for topological sorting
     */
    protected Set<mxICell> verticesWithoutDependencies;
    /**
     * Topologically sorted list of processes
     */
    protected List<mxICell> sorted;

    /**
     * List of all process Vertices
     */
    public TopologicalSorter() {
        this.init();
    }

    private void init() {
        this.unresolvedDependencies = new HashMap<mxICell, List<mxICell>>();
        this.verticesWithoutDependencies = new HashSet<mxICell>();
        this.sorted = new ArrayList<mxICell>();
    }

    /**
     * Performs the topological sorting algorithm on the graph
     *
     * @param graph the graph to be sorted
     * @return sorted vertices
     */
    public List<mxICell> sort(Graph graph) {
        this.init();
        Object[] vertices = graph.getChildVertices(graph.getDefaultParent());
        for (Object vertex : vertices) {
            // create list of unresolved dependencies for each vertix
            if (!this.unresolvedDependencies.containsKey((mxCell) vertex)) {
                this.unresolvedDependencies.put((mxCell) vertex, new ArrayList<mxICell>());
            }
            Object[] incoming = graph.getIncomingEdges(vertex);
            // find vertices without dependencies
            if (incoming.length == 0) {
                this.verticesWithoutDependencies.add((mxCell) vertex);
            } else {
                List<mxICell> dependencies = this.unresolvedDependencies.get((mxCell) vertex);
                for (Object edge : incoming) {
                    mxCell dependency = (mxCell) ((GraphEdge) edge).getSource();
                    dependencies.add(dependency);
                }
            }
        }
        // Topological Sort until every dependency is resolved
        while (!this.verticesWithoutDependencies.isEmpty()) {
            // Pick any vertix without dependencies and move to sorted list
            mxICell vertex = this.verticesWithoutDependencies.iterator().next();
            this.verticesWithoutDependencies.remove(vertex);
            this.sorted.add(vertex);
            Logger.log(getCellName(vertex) + " was resolved and added to sorted List.");
            // Resolve dependencies on child vertices
            Object[] outgoing = graph.getOutgoingEdges(vertex);
            for (Object edge : outgoing) {
                mxICell child = ((GraphEdge) edge).getTarget();
                List<mxICell> dependencies = this.unresolvedDependencies.get(child);
                dependencies.remove(vertex);
                Logger.log("Outgoing connection to " + getCellName(child) + " resolved.");
                // Queue child vertix if it has no further dependencies
                if (dependencies.isEmpty()) {
                    this.verticesWithoutDependencies.add(child);
                    Logger.log(getCellName(child) + "has no further dependencies.");
                }
            }
        }
        
        return sorted;
    }

    /**
     * Gets a readable cell name for debugging
     * @TODO delete this function after testing
     * @param cell
     * @return readable cell name
     */
    private String getCellName(mxICell cell) {
        Object entity = cell.getValue();
        switch (cell.getValue().getClass().getSimpleName()) {
            case "ProcessEntity": {
                return (((ProcessEntity) entity).getOwsIdentifier());
            }
            case "ProcessPort": {
                return (((ProcessPort) entity).getOwsIdentifier());
            }
            default: {
                return cell.getValue().getClass().getName();
            }
        }
    }
}
