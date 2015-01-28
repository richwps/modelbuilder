package de.hsos.richwps.mb.dsl;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import de.hsos.richwps.dsl.api.Writer;
import de.hsos.richwps.dsl.api.elements.*;
import de.hsos.richwps.mb.dsl.exceptions.IdentifierDuplicatedException;
import de.hsos.richwps.mb.dsl.exceptions.NoIdentifierException;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses a copy of the current graph to create DSL notation.
 *
 * @author jkovalev
 * @author dziegenh
 * @author dalcacer
 * @version 0.0.6
 */
public class Exporter {

    /**
     * Variable reference map. Does <b>not contain</b> input and output
     * variables.
     */
    protected Map<String, Reference> variables;
    /**
     * Association variable to edges.
     */
    protected HashMap<String, String> edges;
    /**
     * * Extended version of the (JGraphX) mxGraph.
     */
    protected Graph graph;

    /**
     * A structure to trace allready established bindings.
     */
    private List<Binding> bindings;

    /**
     * A structure to trace allready established executes.
     */
    private List<Execute> executes;

    /**
     * The workflow.
     */
    private Workflow workflow;

    private Map<String, String> owsmap = new HashMap<>();
    private List<mxICell> sorted;

    /**
     * Constructs a new Exporter.
     *
     * @param graph the exported graph.
     * @throws de.hsos.richwps.mb.dsl.exceptions.NoIdentifierException
     * @throws de.hsos.richwps.mb.dsl.exceptions.IdentifierDuplicatedException
     */
    public Exporter(Graph graph) throws NoIdentifierException, IdentifierDuplicatedException {

        // ensure the graph meets all requirements to be translated
        GraphHandler.validate(graph);

        this.graph = graph;
        this.variables = new HashMap<>();
        this.bindings = new ArrayList<>();
        this.executes = new ArrayList<>();
        this.edges = new HashMap<>();
        this.workflow = new Workflow();
        this.prepareGraph();
    }

    private void prepareGraph() {
        long uniqueId = 0;

        for (mxCell portCell : this.graph.getAllFlowOutputCells()) {
            ProcessPort port = (ProcessPort) this.graph.getModel().getValue(portCell);
            port = port.clone();
            this.graph.getModel().setValue(portCell, port);
            String owsidentifier = port.getOwsIdentifier();

            String hashed = owsidentifier + "_" + (uniqueId++);
            port.setOwsIdentifier(hashed);
            this.owsmap.put(hashed, owsidentifier);
        }

        for (mxCell portCell : this.graph.getAllFlowInputCells()) {
            ProcessPort port = (ProcessPort) this.graph.getModel().getValue(portCell);
            port = port.clone();
            this.graph.getModel().setValue(portCell, port);
            String owsidentifier = port.getOwsIdentifier();

            String hashed = owsidentifier + "_" + (uniqueId++);
            port.setOwsIdentifier(hashed);
            this.owsmap.put(hashed, owsidentifier);
        }

        // Topological sort is used to resolve dependencies
        TopologicalSorter sorter = new TopologicalSorter();
        sorted = sorter.sort(graph);
    }

    /**
     * Launches the export and writes results to specifed file.
     *
     * @param path file to write to.
     * @throws Exception
     */
    public void export(String path) throws Exception {

        final String url = (String) this.graph.getGraphModel().getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);

        Writer writer = new Writer();

        for (mxICell cell : sorted) {
            //handle process.
            if (this.graph.getGraphModel().isProcess(cell)) {
                ProcessEntity pe = ((ProcessEntity) this.graph.getGraphModel().getValue(cell));
                //local/remote identification based on hostname.
                //truncate the endpoints and compare.
                final String baseuria = url.replace(IRichWPSProvider.DEFAULT_WPS_ENDPOINT, "");
                final String baseurib = pe.getServer().replace(IRichWPSProvider.DEFAULT_WPS_ENDPOINT, "");
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
        //write out all bindings 
        for (Binding b : this.bindings) {
            this.workflow.add(b);
        }
        //write out all executes
        for (Execute e : this.executes) {
            this.workflow.add(e);
        }
        //finally write out the whole workflow
        writer.create(path, this.workflow);
    }

    /**
     * Defines one global input, assigns inputs to variables in the reference
     * map.
     *
     * @param input
     * @param ws workflow to write to
     * @throws Exception
     */
    private void handleInputCell(mxICell input) throws Exception {

        for (int i = 0; i < input.getEdgeCount(); i++) {
            if (input.getEdgeAt(i) instanceof GraphEdge) {
                GraphEdge transition = (GraphEdge) input.getEdgeAt(i);
                mxICell target = transition.getTargetPortCell();

                //when global input cell is connected to global output cell,
                //directly generate assignment.
                if (this.graph.getGraphModel().isGlobalOutputPort(target)) {
                    ProcessPort peout = (ProcessPort) target.getValue();
                    ProcessPort pein = (ProcessPort) input.getValue();

                    final String inidentifier = this.truncate(pein.getOwsIdentifier());
                    final String outidentifier = this.truncate(peout.getOwsIdentifier());

                    InReference in = new InReference(inidentifier);
                    OutReference out = new OutReference(outidentifier);
                    Assignment as = new Assignment(out, in);
                    this.workflow.add(as);
                }
            }
        }
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
            final ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            final ProcessPort outputcell = (ProcessPort) edge.getTargetPortCell().getValue();

            final String owsOutIdentifier = this.truncate(outputcell.getOwsIdentifier());
            Reference outputReference = new OutReference(owsOutIdentifier);

            //Reading varibale from varibale reference map
            Reference variable = this.variables.get(source.getOwsIdentifier());

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
        handleIngoingProcessCellEdges(incoming, execute);
        handleOutgoingProcessCellEdges(outgoing, execute);
        this.executes.add(execute);
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
        }
        return rolaidentifier;
    }

    /**
     * Handle ingoing process edges and map given variables to
     * wps:process:inputs. This method deals with the with-section of an
     * execute-command.
     *
     * @param outgoing List of ingoing edges.
     * @param execute according execute statement/process.
     * @throws Exception ROLA-exception in case the input is malformed.
     */
    private void handleIngoingProcessCellEdges(Object[] incoming, Execute execute) throws Exception {

        for (Object in : incoming) {
            //for each edge.
            GraphEdge edge = (GraphEdge) in;
            final ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            final ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();

            //global input to this process
            if (source.isGlobal()) {
                final String inidentifier = this.truncate(source.getOwsIdentifier());
                final String wpsinidentifer = this.truncate(target.getOwsIdentifier());
                InReference inref = new InReference(inidentifier);
                execute.addInput(inref, wpsinidentifer);
            } //variable input to this process
            else {

                final String unique_src = source.getOwsIdentifier();
                //take variable from memory, or create new one.
                VarReference variable;
                if (this.variables.containsKey(unique_src)) {
                    variable = (VarReference) this.variables.get(unique_src);
                } else {
                    variable = new VarReference(unique_src);
                    this.variables.put(unique_src, variable);
                }

                final String owsinidentifier = this.truncate(target.getOwsIdentifier());
                execute.addInput(variable, owsinidentifier);
            }
        }// for
    }

    /**
     * Handle outgoing process edges and map wps:process:outputs to given
     * variables or globally available outputs. This method deals with the
     * store-section of an execute-command.
     *
     * @param outgoing List of outgoing edges.
     * @param execute according execute statement/process.
     * @throws Exception ROLA-exception in case the output is malformed.
     */
    private void handleOutgoingProcessCellEdges(Object[] outgoing, Execute execute) throws Exception {
        List<String> vars = new ArrayList<>();

        for (Object out : outgoing) {
            //for each edge
            GraphEdge edge = (GraphEdge) out;
            final ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
            final ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();

            if (target.isGlobal()) {
                final String outidentifer = this.truncate(target.getOwsIdentifier());
                final String wpsoutidentifer = this.truncate(source.getOwsIdentifier());
                OutReference outref = new OutReference(outidentifer);
                execute.addOutput(wpsoutidentifer, outref);
            } else {
                //
                // Declare reusable variables for outgoing edges.
                // e.g. [...] store
                // output as var.out
                // output2 as var.out2
                // [....]
                // [C][L]
                //  |   |
                //  |   ----------
                //  |            |
                // [C][L]    [C][L]
                // [....]    [....]
                final String unique_src = source.getOwsIdentifier();

                //take variable from memory, or create new one.
                VarReference variable;
                if (this.variables.containsKey(unique_src)) {
                    variable = (VarReference) this.variables.get(unique_src);
                } else {
                    variable = new VarReference(unique_src);
                    this.variables.put(unique_src, variable);
                }

                if (!vars.contains(unique_src)) {
                    // avoid redundant declaration if an output is used multiple
                    // times
                    //
                    // [....]
                    // [C][L]
                    //  |
                    //  |----------
                    //  |         |
                    // [C][L]    [C][L]
                    // [....]    [....]
                    vars.add(unique_src);
                    final String owsoutidentifier = this.truncate(source.getOwsIdentifier());
                    execute.addOutput(owsoutidentifier, variable);
                    //trace this edge
                    if (this.edges.containsKey(unique_src)) {
                        String value = (String) this.edges.get(unique_src);
                        value += ",from " + edge.getSource().getValue() + " to " + edge.getTarget().getValue();
                        this.edges.put(unique_src, value);
                    } else {
                        this.edges.put(unique_src, "from " + edge.getSource().getValue() + " to " + edge.getTarget().getValue());
                    }
                }
            }
        }// for 
    }

    /**
     * Identifier are designed to be unique (name_NUMBER). In order to retain
     * the common ows:identifer, we need to truncate the _NUMBER.
     *
     * @param rawIdentifier
     * @return
     */
    private String truncate(String rawIdentifier) {
        return rawIdentifier.split("_")[0];
        //return GraphHandler.getOwsIdentifier(rawIdentifier);
    }

    /**
     * Delivers variables associated with edges.
     *
     * @return variables associated to edges.
     */
    public Map<String, String> getEdges() {
        System.err.println(this.variables);
        return this.edges;
    }
}
