package de.hsos.richwps.mb.dsl;

import com.mxgraph.model.mxCell;
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
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports a graph to DSL notation.
 *
 * @author jkovalev
 * @author dziegenh
 * @author dalcacer
 * @version 0.0.2
 */
public class Export {

    /**
     * Variable reference map. Does <b>not contain</b> input and output
     * variables.
     */
    protected Map<String, Reference> variables;
    /**
     * * Extended version of the (JGraphX) mxGraph.
     */
    protected Graph graph;

    /**
     * A structure to trace allready established bindings.
     */
    private ArrayList<Binding> bindings;

    /**
     * Initializes the variables and fills the vertex lists
     *
     * @param graph the exported graph
     */
    public Export(Graph graph) throws NoIdentifierException, IdentifierDuplicatedException {

        // ensure the graph meets all requirements to be translated
        GraphHandler.validate(graph);

        this.graph = graph;
        this.variables = new HashMap<>();
        this.bindings = new ArrayList<>();
    }

    /**
     * Launches the exporting pe and writes results to specifed file
     *
     * @param path file to write to
     * @param wpstendpoint for local/remote detection.
     * @throws Exception
     */
    public void export(String path, String wpstendpoint) throws Exception {

        Writer writer = new Writer();
        Worksequence ws = new Worksequence();

        createUniqueIdentifiers(graph.getAllFlowOutputPorts());

        // Topological sort is used to resolve dependencies
        TopologicalSorter sorter = new TopologicalSorter();
        List<mxICell> sorted = sorter.sort(graph);

        for (mxICell cell : sorted) {
            //handle process.
            if (this.graph.getGraphModel().isProcess(cell)) {
                ProcessEntity pe = ((ProcessEntity) this.graph.getGraphModel().getValue(cell));
                //local/remote identification based on given hostname.
                String baseuria = wpstendpoint.replace(IRichWPSProvider.DEFAULT_WPST_ENDPOINT, "");
                String baseurib = pe.getServer().replace(IRichWPSProvider.DEFAULT_WPS_ENDPOINT, "");
                boolean isLocalBinding = baseuria.equals(baseurib);
                this.handleProcessCell(cell, ws, isLocalBinding);

            } //handle outputs.
            else if (this.graph.getGraphModel().isFlowInput(cell)) {
                this.handleOutputCell(cell, ws);
            } //handle inptus.
            else if (this.graph.getGraphModel().isFlowOutput(cell)) {
                this.handleInputCell(cell, ws);
            }
        }
        writer.create(path, ws);
    }

    /**
     * Defines one global input, assigns inputs to variables in the reference
     * map
     *
     * @param input
     * @param ws Worksequence to write to
     * @throws Exception
     */
    protected void handleInputCell(mxICell input, Worksequence ws) throws Exception {

        //when global input cell is connected to global output cell, generate assignment.
        for (int i = 0; i < input.getEdgeCount(); i++) {
            if (input.getEdgeAt(i) instanceof GraphEdge) {
                GraphEdge transition = (GraphEdge) input.getEdgeAt(i);
                mxICell target = transition.getTargetPortCell();

                if (this.graph.getGraphModel().isGlobalOutputPort(target)) {
                    ProcessPort peout = (ProcessPort) target.getValue();
                    ProcessPort pein = (ProcessPort) input.getValue();
                    InReference in = new InReference(pein.getOwsIdentifier());
                    OutReference out = new OutReference(peout.getOwsIdentifier());
                    Assignment as = new Assignment(out, in);
                    ws.add(as);
                }
            }
        }


        /*ProcessPort source = (ProcessPort) input.getValue();
         String uniqueIdentifier = getUniqueIdentifier(source.getOwsIdentifier());
         // OWS Identifier is the original port identifier
         String owsIdentifier = getOwsIdentifier(source.getOwsIdentifier());

         VarReference variable = new VarReference(owsIdentifier);
         InReference inputReference = new InReference(owsIdentifier);
         Assignment assignment = new Assignment(variable, inputReference);
         // Save variable to variable reference map
         this.variables.put(source.getOwsIdentifier(), variable);
         // Write assignment to Worksequence
         Logger.log("Debug::Export#defineInput()\n Assignment: " + assignment.toNotation());
         ws.add(assignment);
         */
    }

    /**
     * Defines global output, gets its value from the reference map
     *
     * @param ws
     * @throws Exception
     */
    protected void handleOutputCell(mxICell output, Worksequence ws) throws Exception {
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
            ws.add(assignment);
        }
    }

    /**
     * Adds one pe execute statement to worksequence, gets inputs from
     * variables, stores outputs to variables
     *
     * @param processVertex
     * @param ws Worksequence to write to.
     * @param isLocalBinding information about binding type.
     * @throws Exception e.
     */
    protected void handleProcessCell(mxICell processVertex, Worksequence ws, boolean isLocalBinding) throws Exception {

        // Get inputs from variable reference map
        Object[] incoming = graph.getIncomingEdges(processVertex);
        Object[] outgoing = graph.getOutgoingEdges(processVertex);
        ProcessEntity pe = (ProcessEntity) processVertex.getValue();

        String rolaidentifier = handleBinding(pe, isLocalBinding, ws);
        Execute execute = new Execute(rolaidentifier);
        handleIngoingProcessCellTransitions(incoming, execute);
        handleOutgoingProcessCellTransitions(outgoing, execute);
        ws.add(execute);
    }

    private String handleBinding(ProcessEntity pe, boolean isLocalBinding, Worksequence ws) throws MalformedURLException {
        String identifier = pe.getOwsIdentifier();
        //Create a unique identifier based on the place of execution.
        String rolaidentifier = "";
        Binding bindingA;
        if (isLocalBinding) {
            rolaidentifier = "local/" + identifier;
            bindingA = new Binding(rolaidentifier, identifier);
        } else {
            URL aURL = new URL(pe.getServer());
            //a unique portion for each host.
            int servershorthand = aURL.getHost().hashCode();
            rolaidentifier = "remote" + servershorthand + "/" + identifier;

            bindingA = new Binding(rolaidentifier, identifier);

            Endpoint e = new Endpoint();
            e.setProtocol(aURL.getProtocol());
            e.setHost(aURL.getHost());
            if (aURL.getPort() != -1) {
                e.setPort(aURL.getPort());
            } else {
                e.setPort(80);
            }
            e.setPath(aURL.getPath());
            bindingA.setEndpoint(e);
        }

        if (!this.bindings.contains(bindingA)) {
            this.bindings.add(bindingA);
            ws.add(bindingA);
        }

        return rolaidentifier;
    }

    private void handleIngoingProcessCellTransitions(Object[] incoming, Execute execute) throws Exception {
        //Handle ingoing transitions. Map variables to wps:process:inputs.
        //All global inputs are variables.

        //FIXME input to ows not correct.
        for (Object in : incoming) {
            GraphEdge edge = (GraphEdge) in;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();

            if (source.isGlobal()) {
                InReference inref = new InReference(target.getOwsIdentifier());
                execute.addInput(inref, source.getOwsIdentifier());
            } else {
                // Reading varibale from varibale reference map
                // lookup variable
                VarReference variable = null;
                if (this.variables.containsKey(source.getOwsIdentifier())) {
                    //variable allready declared, lets use it.
                    variable = (VarReference) this.variables.get(source.getOwsIdentifier());
                } else {
                    //variable does not exist, we need to create it first.
                    variable = new VarReference(source.getOwsIdentifier());
                    this.variables.put(source.getOwsIdentifier(), variable);
                }
                execute.addInput(variable, source.getOwsIdentifier());
            }
        }
    }

    private void handleOutgoingProcessCellTransitions(Object[] outgoing, Execute execute) throws Exception {
        //Handle outgoing transitions. Map wps:process:outputs to variables or
        //outputs.
        List<String> vars = new ArrayList<>();
        for (Object out : outgoing) {
            GraphEdge edge = (GraphEdge) out;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();

            // Writing varibale to varibale reference map
            if (target.isGlobal()) {
                OutReference outref = new OutReference(target.getOwsIdentifier());
                execute.addOutput(source.getOwsIdentifier(), outref);
            } else {
                VarReference variable = new VarReference(this.getUniqueIdentifier(source.getOwsIdentifier()));
                if (!vars.contains(variable.getId())) {    //allready set?
                    vars.add(variable.getId());
                    execute.addOutput(source.getOwsIdentifier(), variable);
                }
            }
        }
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
