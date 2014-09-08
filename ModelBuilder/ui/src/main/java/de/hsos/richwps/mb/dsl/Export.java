package de.hsos.richwps.mb.dsl;

import com.mxgraph.model.mxICell;
import de.hsos.richwps.dsl.api.Writer;
import de.hsos.richwps.dsl.api.elements.Assignment;
import de.hsos.richwps.dsl.api.elements.Binding;
import de.hsos.richwps.dsl.api.elements.Endpoint;
import de.hsos.richwps.dsl.api.elements.Execute;
import de.hsos.richwps.dsl.api.elements.InReference;
import de.hsos.richwps.dsl.api.elements.OutReference;
import de.hsos.richwps.dsl.api.elements.Reference;
import de.hsos.richwps.dsl.api.elements.VarReference;
import de.hsos.richwps.dsl.api.elements.Worksequence;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.dsl.exceptions.IdentifierDuplicatedException;
import de.hsos.richwps.mb.dsl.exceptions.NoIdentifierException;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports a graph to dsl notation
 *
 * @author Jewgeni Kovalev
 * @author dziegenh
 * @author dalcacer
 */
public class Export {

    /**
     * Variable reference map input variables are stored to the reference map at
 the beginning pe outputs are stored to variables in the reference
 map pe inputs get their values from the reference map output
 variables get their values from the reference map
     */
    protected Map<String, Reference> variables;
    protected Graph graph;

    /**
     * Initializes the variables and fills the vertex lists
     *
     * @param graph the exported graph
     */
    public Export(Graph graph) throws NoIdentifierException, IdentifierDuplicatedException {

        // ensure the graph meets all requirements to be translated
        GraphHandler.validate(graph);

        this.graph = graph;
        this.variables = new HashMap<String, Reference>();
    }

    /**
     * Defines one global input, assigns inputs to variables in the reference
     * map
     *
     * @param ws Worksequence to write to
     * @throws Exception
     */
    protected void defineInput(mxICell input, Worksequence ws) throws Exception {
        Logger.log("Defining input variable.");
        ProcessPort source = (ProcessPort) input.getValue();

        // unique identifier is necessaty to distinguish ports with the same owsIdentifier
        String uniqueIdentifier = getUniqueIdentifier(source.getOwsIdentifier());
        // OWS Identifier is the original port identifier
        String owsIdentifier = getOwsIdentifier(source.getOwsIdentifier());

        VarReference variable = new VarReference(owsIdentifier);
        InReference inputReference = new InReference(owsIdentifier);
        Assignment assignment = new Assignment(variable, inputReference);
        // Save variable to variable reference map
        this.variables.put(source.getOwsIdentifier(), variable);
        // Write assignment to Worksequence
        Logger.log("Assignment: " + assignment.toNotation());
        ws.add(assignment);
    }

    /**
     * Adds one pe execute statement to worksequence, gets inputs from
 variables, stores outputs to variables
     *
     * @param processVertex
     * @param ws Wroksequence to write to.
     * @param isLocalBinding information about binding type.
     * @throws Exception
     */
    protected void defineExecute(mxICell processVertex, Worksequence ws, boolean isLocalBinding) throws Exception {
        
         // Get inputs from variable reference map
        Object[] incoming = graph.getIncomingEdges(processVertex);
        ProcessEntity pe = (ProcessEntity) processVertex.getValue();
        // @TODO: identifier not ideal, not in $org/name$ syntax
        String identifier = pe.getOwsIdentifier();

        String rolaidentifier ="";
        Binding bindingA;
        if(isLocalBinding){
            rolaidentifier="local/"+identifier;
            bindingA = new Binding(rolaidentifier, pe.getOwsIdentifier());
            
        }else{
            URL aURL = new URL(pe.getServer());
            String servershorthand = aURL.getHost();
            de.hsos.richwps.mb.Logger.log("Hostname: "+servershorthand);
            servershorthand=servershorthand.substring(5,10);
            rolaidentifier=servershorthand+"/"+identifier;
            
            bindingA = new Binding(rolaidentifier, pe.getOwsIdentifier());
            
            Endpoint e = new Endpoint();
            e.setProtocol(aURL.getProtocol());
            e.setHost(aURL.getHost());
            if(aURL.getPort()!=-1){
                e.setPort(aURL.getPort());
            }else{
                e.setPort(80);
            }
            e.setPath(aURL.getPath());
            bindingA.setEndpoint(e);
        }
        
        ws.add(bindingA);
       
        Execute execute = new Execute(rolaidentifier);
        for (Object in : incoming) {
            GraphEdge edge = (GraphEdge) in;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();
            // Reading varibale from varibale reference map
            Reference variable = this.variables.get(source.getOwsIdentifier());
            execute.addInput(variable, getOwsIdentifier(target.getOwsIdentifier()));
        }
        //Logger.log("EX ID: " +identifier);
        // Store outputs to variable refenrece map
        Object[] outgoing = graph.getOutgoingEdges(processVertex);
        for (Object out : outgoing) {
            GraphEdge edge = (GraphEdge) out;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            // @TODO: identifier is not ideal
            String uniqueOutIdentifier = getUniqueIdentifier(source.getOwsIdentifier());
            String owsOutIdentifier = getOwsIdentifier(source.getOwsIdentifier());

            // Writing varibale to varibale reference map
            VarReference variable = new VarReference(getUniqueIdentifier(source.getOwsIdentifier()));
            // @TODO: set correct variable value
            Reference asd = this.variables.put(source.getOwsIdentifier(), variable);
            execute.addOutput(owsOutIdentifier, variable);
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

            // unique identifier is necessary to distinguish ports with the same owsIdentifier
            String uniqueInIdentifier = getUniqueIdentifier(source.getOwsIdentifier());
            // OWS Identifier is the original port identifier
//            String owsInIdentifier = uniqueInIdentifier.split(" ")[0];

            // ... same for out identifiers
            String uniqueOutIdentifier = getUniqueIdentifier(target.getOwsIdentifier());
            String owsOutIdentifier = getOwsIdentifier(target.getOwsIdentifier());

//            String inIdentifier = source.getOwsIdentifier();
//            String outIdentifier = target.getOwsIdentifier();
            // Reading varibale from varibale reference map
            Reference variable = this.variables.get(source.getOwsIdentifier());
            Reference outputReference = new OutReference(owsOutIdentifier);
            Assignment assignment = new Assignment(outputReference, variable);
            Logger.log("Assignment: " + assignment.toNotation());
            ws.add(assignment);
        }
    }

    /**
     * Launches the exporting pe and writes results to specifed file
     *
     * @param path file to write to
     * @throws Exception
     */
    public void export(String path, String wpstendpoint) throws Exception {
        Writer writer = new Writer();
        Worksequence ws = new Worksequence();

        createUniqueIdentifiers(graph.getAllFlowOutputPorts());

        // Topological sort is used to resolve dependencies
        TopologicalSorter sorter = new TopologicalSorter();
        List<mxICell> sorted = sorter.sort(graph);
        Logger.log("Sorting graph");
        for (mxICell cell : sorted) {
            if (this.graph.getGraphModel().isProcess(cell)) {
                ProcessEntity pe = ((ProcessEntity) this.graph.getGraphModel().getValue(cell));

                String baseuria = wpstendpoint.replace("/WPST", "");
                String baseurib = pe.getServer().replace("/WebProcessingService", "");
                boolean isLocalBinding = baseuria.equals(baseurib);

                this.defineExecute(cell, ws, isLocalBinding);

            } else if (this.graph.getGraphModel().isFlowInput(cell)) {
                this.defineOutput(cell, ws);
            } else if (this.graph.getGraphModel().isFlowOutput(cell)) {
                this.defineInput(cell, ws);
            }
        }
        Logger.log(ws.toNotation());
        writer.create(path, ws);
    }

    protected void createUniqueIdentifiers(List<ProcessPort> ports) {
        GraphHandler.createRawIdentifiers(ports);
    }

    protected String getOwsIdentifier(String rawIdentifier) {
        return GraphHandler.getOwsIdentifier(rawIdentifier);
    }

    protected String getUniqueIdentifier(String rawIdentifier) {
        return GraphHandler.getUniqueIdentifier(rawIdentifier);
    }

}
