package de.hsos.richwps.mb.dsl;

import com.mxgraph.model.mxICell;
import de.hsos.richwps.dsl.api.Writer;
import de.hsos.richwps.dsl.api.elements.Assignment;
import de.hsos.richwps.dsl.api.elements.Execute;
import de.hsos.richwps.dsl.api.elements.InReference;
import de.hsos.richwps.dsl.api.elements.OutReference;
import de.hsos.richwps.dsl.api.elements.Reference;
import de.hsos.richwps.dsl.api.elements.VarReference;
import de.hsos.richwps.dsl.api.elements.Worksequence;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports a graph to dsl notation
 */
public class Export {

    /**
     * Variable reference map
     * input variables are stored to the reference map at the beginning
     * process outputs are stored to variables in the reference map
     * process inputs get their values from the reference map
     * output variables get their values from the reference map
     */
    protected Map<String, Reference> variables;
    protected Graph graph;

    /**
     * Initializes the variables and fills the vertex lists
     * @param graph the exported graph
     */
    public Export(Graph graph) {
        this.graph = graph;
        this.variables = new HashMap<String, Reference>();
    }
    
    /**
     * Defines one global input, assigns inputs to variables in the
     * reference map
     *
     * @param ws Worksequence to write to
     * @throws Exception
     */
    protected void defineInput(mxICell input, Worksequence ws) throws Exception {
        Logger.log("Defining input variable.");
        ProcessPort source = (ProcessPort) input.getValue();
        // @TODO: getOwsIdentifier is not ideal, collisions possible
        String identifier = source.getOwsIdentifier();
        VarReference variable = new VarReference(identifier);
        InReference inputReference = new InReference(identifier);
        Assignment assignment = new Assignment(variable, inputReference);
        // Save variable to variable reference map
        this.variables.put(identifier, variable);
        // Write assignment to Worksequence
        Logger.log("Assignment: " + assignment.toNotation());
        ws.add(assignment);
    }

    /**
     * Adds one process execute statement to worksequence, gets inputs from variables, stores
     * outputs to variables
     *
     * @param processVertex
     * @param ws Wroksequence to write to
     * @throws Exception
     */
    protected void defineExecute(mxICell processVertex, Worksequence ws) throws Exception {
        GraphModel model = graph.getGraphModel();
        
        // Get inputs from variable reference map
        Object[] incoming = graph.getIncomingEdges(processVertex);
        ProcessEntity process = (ProcessEntity) processVertex.getValue();
        // @TODO: identifier not ideal, not in $org/name$ syntax
        String identifier = process.getIdentifier();
        Execute execute = new Execute(identifier);
        for (Object in : incoming) {
            GraphEdge edge = (GraphEdge) in;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();
            // Reading varibale from varibale reference map
            Reference variable = this.variables.get(source.getOwsIdentifier());
            execute.addInput(variable, target.getOwsIdentifier());
        }
        
        // Store outputs to variable refenrece map
        Object[] outgoing = graph.getOutgoingEdges(processVertex);
        for (Object out : outgoing) {
            GraphEdge edge = (GraphEdge) out;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            // @TODO: identifier is not ideal
            String outIdentifier = source.getOwsIdentifier();
            // Writing varibale to varibale reference map
            VarReference variable = new VarReference(outIdentifier);
            // @TODO: set correct variable value
            Reference asd = this.variables.put(outIdentifier, variable);
            execute.addOutput(outIdentifier, variable);
        }
        Logger.log("Execute: " + execute.toNotation());
        ws.add(execute);
    }

    /**
     * Defines global output, gets its value from the reference map
     *
     * @param ws
     * @throws Exception
     */
    protected void defineOutput(mxICell output, Worksequence ws) throws Exception {
        Logger.log("Defining output variable.");
        // Check incoming edges and look up the values in variables from the reference map
        Object[] incoming = graph.getIncomingEdges(output);
        for (Object in : incoming) {
            GraphEdge edge = (GraphEdge) in;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();
            // @TODO: identifier is not ideal
            String inIdentifier = source.getOwsIdentifier();
            String outIdentifier = target.getOwsIdentifier();
            // Reading varibale from varibale reference map
            Reference variable = this.variables.get(inIdentifier);
            Reference outputReference = new OutReference(outIdentifier);
            Assignment assignment = new Assignment(outputReference, variable);
            Logger.log("Assignment: " + assignment.toNotation());
            ws.add(assignment);
        }
    }

    /**
     * Launches the exporting process and writes results to specifed file
     *
     * @param path file to write to
     * @throws Exception
     */
    public void export(String path) throws Exception {
        Writer writer = new Writer();
        Worksequence ws = new Worksequence();
        // Topological sort is used to resolve dependencies
        TopologicalSorter sorter = new TopologicalSorter();
        List<mxICell> sorted = sorter.sort(graph);
        Logger.log("Sorting graph");
        for (mxICell cell : sorted) {
            if(this.graph.getGraphModel().isProcess(cell)){
                this.defineExecute(cell, ws);
            }
            else if(this.graph.getGraphModel().isFlowInput(cell)){
                this.defineOutput(cell, ws);
            }
            else if(this.graph.getGraphModel().isFlowOutput(cell)){
                this.defineInput(cell, ws);
            }
        }
        Logger.log(ws.toNotation());
        writer.create(path, ws);
    }

}
