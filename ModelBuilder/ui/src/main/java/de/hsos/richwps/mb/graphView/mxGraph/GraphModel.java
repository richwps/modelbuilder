package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyComponentGroup;
import de.hsos.richwps.mb.properties.propertyComponents.PropertyDropdown;
import de.hsos.richwps.mb.properties.propertyComponents.PropertyTextField;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The model for the RIchWPS graph. Extends the underlying mxGraphModel.
 *
 * @author dziegenh
 */
public class GraphModel extends mxGraphModel implements IObjectWithProperties {

    private String name;

    private List<PropertyComponentGroup> propertyGroups;

    public GraphModel() {
        super();

        propertyGroups = new LinkedList<>();

        PropertyComponentGroup group1 = new PropertyComponentGroup(AppConstants.PROPERTIES_MODELDATA);
        group1.addObject(new PropertyTextField(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_IDENTIFIER, "id"));
        group1.addObject(new PropertyTextField(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ABSTRACT, "abstract"));
        group1.addObject(new PropertyTextField(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_TITLE, "title"));
        group1.addObject(new PropertyTextField(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_VERSION, "version"));

        // TODO add servers from the actual list.
        PropertyComponentGroup group2 = new PropertyComponentGroup("Deployment");
        group2.addObject(new PropertyDropdown(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ENDPOINT, new String[]{"Server a", "Server b"}));

        propertyGroups.add(group1);
        propertyGroups.add(group2);
    }

    public GraphModel(mxGraphModel modelToClone) throws CloneNotSupportedException {
        this();

        mergeChildren((mxICell) modelToClone.getRoot(), (mxCell) getRoot(), true);
    }

    public boolean isFlowInput(Object o) {
        if (!isVertex(o)) {
            return false;
        }

        if (getValue(o) instanceof ProcessPort) {
            return ((ProcessPort) getValue(o)).isFlowInput();
        }

        return false;
    }

    public boolean isFlowOutput(Object o) {
        if (!isVertex(o)) {
            return false;
        }

        if (getValue(o) instanceof ProcessPort) {
            return ((ProcessPort) getValue(o)).isFlowOutput();
        }

        return false;
    }

    /**
     * Return true if the cell is a process vertex (i.e. no edge and no port),.
     *
     * @param cell
     * @return
     */
    public boolean isProcess(Object o) {
        return isVertex(o) && !isFlowInput(o) && !isFlowOutput(o);
    }

    public boolean isLocalPort(Object o) {
        boolean isLocalInput = isFlowInput(o) && !isGlobalOutputPort(o);
        boolean isLocalOutput = isFlowOutput(o) && !isGlobalInputPort(o);
        return isVertex(o) && (isLocalInput || isLocalOutput);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGlobalPort(Object o) {
        if (!isVertex(o)) {
            return false;
        }

        if (getValue(o) instanceof ProcessPort) {
            return ((ProcessPort) getValue(o)).isGlobal();
        }

        return false;
    }

    /**
     * Returns true if the given Object is a global input cell.
     *
     * @param o
     * @return
     */
    public boolean isGlobalInputPort(Object o) {
        if (!isGlobalPort(o)) {
            return false;
        }

        return ((ProcessPort) getValue(o)).isGlobalInput();
    }

    /**
     * Returns true if the given Object is a global output cell.
     *
     * @param o
     * @return
     */
    public boolean isGlobalOutputPort(Object o) {
        if (!isGlobalPort(o)) {
            return false;
        }

        return ((ProcessPort) getValue(o)).isGlobalOutput();
    }

    boolean arePortTypesCompatible(ProcessPort p1, ProcessPort p2) {
        return p1.getDatatype().equals(p2.getDatatype());
    }

    boolean isOutputPortUsed(Object o, Graph graph) {
        Object parent;

        GraphModel model = (GraphModel) ((null == graph) ? this : graph.getModel());

        if (model.isGlobalPort(o)) {
            parent = o;
        } else {
            parent = model.getParent(o);
        }

        Object[] sourceOutgoingEdges = mxGraphModel.getOutgoingEdges(model, parent);
        for (Object out : sourceOutgoingEdges) {
            if (out instanceof GraphEdge) {
                GraphEdge outEdge = (GraphEdge) out;
                mxCell sourcePort = outEdge.getSourcePortCell();
                if (null != sourcePort && sourcePort.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isInputPortUsed(Object o, Graph graph) {
        Object parent;

        GraphModel model = (GraphModel) ((null == graph) ? this : graph.getModel());

        if (model.isGlobalPort(o)) {
            parent = o;
        } else {
            parent = model.getParent(o);
        }

        Object[] targetIncomingEdges = mxGraphModel.getIncomingEdges(model, parent);
        for (Object in : targetIncomingEdges) {
            if (in instanceof GraphEdge) {
                GraphEdge inEdge = (GraphEdge) in;
                mxCell targetCell = inEdge.getTargetPortCell();
                if (null != targetCell && targetCell.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public GraphModel clone() throws CloneNotSupportedException {
        GraphModel clone = new GraphModel();
        clone.mergeChildren((mxCell) getRoot(), (mxICell) clone.getRoot(), true);
        clone.setName(this.name);

        return clone;
    }

    @Override
    public String getPropertiesObjectName() {
        return "graph_model";
    }

    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        return this.propertyGroups;
    }

    public Object getPropertyValue(String propertyName) {
        for (PropertyComponentGroup aGroup : propertyGroups) {
            AbstractPropertyComponent component = aGroup.getPropertyObject(propertyName);
            if (null != component) {
                return component.getValue();
            }
        }
        return null;
    }

}
