package de.hsos.richwps.mb.dsl;

import com.mxgraph.model.mxICell;
import de.hsos.richwps.dsl.api.Writer;
import de.hsos.richwps.dsl.api.elements.*;
import de.hsos.richwps.mb.app.AppConstants;
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
 * @version 0.0.4
 */
public class Exporter {

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
     * A structure to trace allready established exectes.
     */
    private ArrayList<Binding> executes;

    /**
     * The workflow.
     */
    private Workflow workflow;

    /**
     * Constructs a new Exporter.
     *
     * @param graph the exported graph.
     */
    public Exporter(Graph graph) throws NoIdentifierException, IdentifierDuplicatedException {

        // ensure the graph meets all requirements to be translated
        GraphHandler.validate(graph);

        this.graph = graph;
        this.variables = new HashMap<>();
        this.bindings = new ArrayList<>();
        this.executes = new ArrayList<>();
        this.workflow = new Workflow();
    }

    /**
     * Launches the export and writes results to specifed file.
     *
     * @param path file to write to.
     * @param wpstendpoint for local/remote detection.
     * @throws Exception
     */
    public void export(String path) throws Exception {
        
        String identifier = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_IDENTIFIER);
        String title = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_TITLE);
        String version = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_VERSION);
        String wpstuurl = (String)  this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ENDPOINT);
        
        Writer writer = new Writer();

        createUniqueIdentifiers(graph.getAllFlowOutputPorts());

        // Topological sort is used to resolve dependencies
        TopologicalSorter sorter = new TopologicalSorter();
        List<mxICell> sorted = sorter.sort(graph);

        for (mxICell cell : sorted) {
            //handle process.
            if (this.graph.getGraphModel().isProcess(cell)) {
                ProcessEntity pe = ((ProcessEntity) this.graph.getGraphModel().getValue(cell));
                //local/remote identification based on given hostname.
                String baseuria = wpstuurl.replace(IRichWPSProvider.DEFAULT_WPST_ENDPOINT, "");
                String baseurib = pe.getServer().replace(IRichWPSProvider.DEFAULT_WPS_ENDPOINT, "");
                boolean isLocalBinding = baseuria.equals(baseurib);
                this.handleProcessCell(cell, isLocalBinding);

            } //handle outputs.
            else if (this.graph.getGraphModel().isFlowInput(cell)) {
                this.handleOutputCell(cell);
            } //handle inptus.
            else if (this.graph.getGraphModel().isFlowOutput(cell)) {
                this.handleInputCell(cell);
            }
        }
        writer.create(path, this.workflow);
    }

    /**
     * Defines one global input, assigns inputs to variables in the
     * reference map.
     *
     * @param input
     * @param ws Worksequence to write to
     * @throws Exception
     */
    private void handleInputCell(mxICell input) throws Exception {

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
                    this.workflow.add(as);
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
         Logger.log("Debug::Exporter#defineInput()\n Assignment: " + assignment.toNotation());
         ws.add(assignment);
         */
    }

    /**
     * Defines global output, gets its value from the reference map
     *
     * @throws Exception
     */
    private void handleOutputCell(mxICell output) throws Exception {
        // Check incoming edges and look up the values in variables from the reference map
        Object[] incoming = graph.getIncomingEdges(output);
        for (Object in : incoming) {
            GraphEdge edge = (GraphEdge) in;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();

            // unique identifier is necessary to distinguish ports with the same owsIdentifier
            String uniqueInIdentifier = getUniqueIdentifier(source.getOwsIdentifier());
            // OWS Identifier is the original port identifier
            //String owsInIdentifier = uniqueInIdentifier.split(" ")[0];

            // ... same for out identifiers
            String uniqueOutIdentifier = getUniqueIdentifier(target.getOwsIdentifier());
            String owsOutIdentifier = getOwsIdentifier(target.getOwsIdentifier());

            //String inIdentifier = source.getOwsIdentifier();
            //String outIdentifier = target.getOwsIdentifier();
            // Reading varibale from varibale reference map
            Reference variable = this.variables.get(source.getOwsIdentifier());
            Reference outputReference = new OutReference(owsOutIdentifier);
            Assignment assignment = new Assignment(outputReference, variable);
            this.workflow.add(assignment);
        }
    }

    /**
     * Adds an execute statement to workflow, gets inputs from variables, stores
     * outputs to variables
     *
     * @param processVertex
     * @param isLocalBinding information about binding type.
     * @throws Exception e.
     */
    private void handleProcessCell(mxICell processVertex, boolean isLocalBinding) throws Exception {

        // Get inputs from variable reference map
        Object[] incoming = graph.getIncomingEdges(processVertex);
        Object[] outgoing = graph.getOutgoingEdges(processVertex);
        ProcessEntity pe = (ProcessEntity) processVertex.getValue();

        String rolaidentifier = handleBinding(pe, isLocalBinding);
        Execute execute = new Execute(rolaidentifier);
        handleIngoingProcessCellTransitions(incoming, execute);
        handleOutgoingProcessCellTransitions(outgoing, execute);
        this.workflow.add(execute);
    }

    /**
     * Deal with bindings for an according execute statement/process.
     *
     * @param The according process entity.
     * @param isLocalBinding information whether to create a remote or local
     * binding.
     * @return a unique identifier, which can be used for an execute statement.
     * @throws MalformedURLException
     */
    private String handleBinding(ProcessEntity entity, boolean isLocalBinding) throws MalformedURLException {
        String identifier = entity.getOwsIdentifier();
        //Create a unique identifier based on the place of execution.
        String rolaidentifier = "";
        Binding bindingA;
        if (isLocalBinding) {
            rolaidentifier = "local/" + identifier;
            bindingA = new Binding(rolaidentifier, identifier);
        } else {
            URL aURL = new URL(entity.getServer());
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
            this.workflow.add(bindingA);
        }

        return rolaidentifier;
    }

    /**
     * Handle ingoing process transitions and map given variables to
     * wps:process:inputs.
     *
     * @param outgoing List of ingoing transitions.
     * @param execute According execute statement/process.
     * @throws Exception ROLA-exception in case the input is malformed.
     */
    private void handleIngoingProcessCellTransitions(Object[] incoming, Execute execute) throws Exception {
        //Handle ingoing transitions. Map variables to wps:process:inputs.

        for (Object in : incoming) {
            //For each transition.
            GraphEdge edge = (GraphEdge) in;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();

            if (source.isGlobal()) {
                InReference inref = new InReference(source.getOwsIdentifier());
                execute.addInput(inref, target.getOwsIdentifier());
            } else {
                // Reading varibale from varibale reference map
                // lookup variable
                VarReference variable = null;
                String unique_src = this.getUniqueIdentifier(source.getOwsIdentifier());
                //FIXME
                String owsin = this.getOwsIdentifier(source.getOwsIdentifier());
                if (this.variables.containsKey(unique_src)) {
                    //variable allready declared, lets use it.
                    variable = (VarReference) this.variables.get(unique_src);
                } else {
                    //variable does not exist, we need to create it first.
                    variable = new VarReference(unique_src);
                    this.variables.put(unique_src, variable);
                }
                execute.addInput(variable, owsin);
            }
        }
    }

    /**
     * Handle outgoing process transitions and map wps:process:outputs to given
     * variables or globally available outputs.
     *
     * @param outgoing List of outgoing transitions.
     * @param execute According execute statement/process.
     * @throws Exception ROLA-exception in case the output is malformed.
     */
    private void handleOutgoingProcessCellTransitions(Object[] outgoing, Execute execute) throws Exception {
        List<String> vars = new ArrayList<>();
        for (Object out : outgoing) {
            GraphEdge edge = (GraphEdge) out;
            ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();

            if (target.isGlobal()) {
                OutReference outref = new OutReference(this.getUniqueIdentifier(target.getOwsIdentifier()));
                execute.addOutput(source.getOwsIdentifier(), outref);
            } else {
                String unique_src = this.getUniqueIdentifier(source.getOwsIdentifier());
                String owsout = this.getOwsIdentifier(source.getOwsIdentifier());
                VarReference variable = new VarReference(unique_src);
                if (!vars.contains(unique_src)) {    //allready set?
                    vars.add(unique_src);
                    execute.addOutput(owsout, variable);
                }
            }
        }
    }

    private void createUniqueIdentifiers(List<ProcessPort> ports) {
        GraphHandler.createRawIdentifiers(ports);
    }

    private String getOwsIdentifier(String rawIdentifier) {
        return GraphHandler.getOwsIdentifier(rawIdentifier);
    }

    private String getUniqueIdentifier(String rawIdentifier) {
        return GraphHandler.getUniqueIdentifier(rawIdentifier);
    }
}
