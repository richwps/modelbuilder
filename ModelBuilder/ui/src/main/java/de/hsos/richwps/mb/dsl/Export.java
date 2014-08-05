package de.hsos.richwps.mb.dsl;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.dsl.api.Writer;
import de.hsos.richwps.dsl.api.elements.Assignment;
import de.hsos.richwps.dsl.api.elements.Execute;
import de.hsos.richwps.dsl.api.elements.InReference;
import de.hsos.richwps.dsl.api.elements.OutReference;
import de.hsos.richwps.dsl.api.elements.Reference;
import de.hsos.richwps.dsl.api.elements.VarReference;
import de.hsos.richwps.dsl.api.elements.Worksequence;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.util.ArrayList;
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
    protected List<mxCell> processVertices;
    protected List<mxCell> outputVertices;
    protected List<mxCell> inputVertices;
    protected Graph graph;

    /**
     * Initializes the variables and fills the vertex lists
     * @param graph the exported graph
     */
    public Export(Graph graph) {
        this.graph = graph;
        this.variables = new HashMap<String, Reference>();
        this.inputVertices = new ArrayList<mxCell>();
        this.processVertices = new ArrayList<mxCell>();
        this.outputVertices = new ArrayList<mxCell>();

        this.initVertices();
    }

    /**
     * Iterates through all vertices, detects inputs, outputs and processes
     */
    protected void initVertices(){
        System.out.println("Initializing vertices");
        Object[] vertices = graph.getChildVertices(graph.getDefaultParent());
        GraphModel model = graph.getGraphModel();
        for (Object vertex : vertices) {
            if (model.isProcess(vertex)) {
                System.out.println("Process found");
                this.processVertices.add((mxCell) vertex);
            } else if (model.isFlowInput(vertex)) {
                System.out.println("Output found");
                this.outputVertices.add((mxCell) vertex);
            } else if (model.isFlowOutput(vertex)) {
                System.out.println("Input found");
                this.inputVertices.add((mxCell) vertex);
            }
        }
    }

    /**
     * Iterates through global inputs, assigns inputs to variables in the reference map
     * @param ws Worksequence to write to
     * @throws Exception
     */
    protected void defineInputs(Worksequence ws) throws Exception {
        System.out.println("Defining input variables.");
        for (mxCell input : this.inputVertices) {
            ProcessPort source = (ProcessPort) input.getValue();
            // @TODO: getOwsIdentifier is not ideal, collisions possible
            String identifier = source.getOwsIdentifier();
            VarReference variable = new VarReference(identifier);
            InReference inputReference = new InReference(identifier);
            Assignment assignment = new Assignment(variable, inputReference);
            // Save variable to variable reference map
            this.variables.put(identifier, variable);
            // Write assignment to Worksequence
            System.out.println("Assignment: "+assignment.toNotation());
            ws.add(assignment);
        }
    }

    /**
     * Iterates through Processeses, gets their inputs from variables, stores outputs to variables
     * @param ws Wroksequence to write to
     * @throws Exception
     */
    protected void defineExecutes(Worksequence ws) throws Exception {
        // 2. iterate through processes
        // read inputs from variable reference map,
        // write outputs to variable reference map
        GraphModel model = graph.getGraphModel();
        for(mxCell vertex : this.processVertices){
            // 2.1 get inputs from variable reference map
            Object[] incoming = graph.getIncomingEdges(vertex);
            ProcessEntity process = (ProcessEntity) vertex.getValue();
            // @TODO: identifier not ideal, not in $org/name$ syntax
            String identifier = process.getIdentifier();
            Execute execute = new Execute(identifier);
            for(Object in : incoming){
                GraphEdge edge = (GraphEdge) in;
                ProcessPort source = (ProcessPort) edge.getSourcePortCell().getValue();
                ProcessPort target = (ProcessPort) edge.getTargetPortCell().getValue();
                // Reading varibale from varibale reference map
                Reference variable = this.variables.get(source.getOwsIdentifier());
                execute.addInput(variable, target.getOwsIdentifier());
            }
            // 2.2 store outputs to variable refenrece map
            Object[] outgoing = graph.getOutgoingEdges(vertex);
            for(Object out : outgoing){
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
            System.out.println("Execute: "+execute.toNotation());
            ws.add(execute);
        }
    }

    /**
     * Iterates through global outputs, gets their values from the reference map
     * @param ws
     * @throws Exception
     */
    protected void defineOutputs(Worksequence ws) throws Exception {
        System.out.println("Defining output variables.");
        for (mxCell output : this.outputVertices) {
            // Check incoming edges and look up the values in variables from the reference map
            Object[] incoming = graph.getIncomingEdges(output);
            for(Object in : incoming){
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
                System.out.println("Assignment: "+assignment.toNotation());
                ws.add(assignment);
            }
        }
    }

    /**
     * Launches the exporting process and writes results to specifed file
     * @param path file to write to
     * @throws Exception
     */
    public void export(String path) throws Exception {
        Writer writer = new Writer();
        Worksequence ws = new Worksequence();
        defineInputs(ws);
        defineExecutes(ws);
        defineOutputs(ws);
        System.out.println(ws.toNotation());
        writer.create(path, ws);
    }
}
