package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The model for the RichWPS graph. Extends the underlying mxGraphModel.
 *
 * @author dziegenh
 */
public class GraphModel extends mxGraphModel implements IObjectWithProperties {

    private List<PropertyGroup> propertyGroups;

    public GraphModel() {
        super();

        propertyGroups = new LinkedList<>();

        // TODO move keys here
        PropertyGroup group1 = new PropertyGroup(AppConstants.PROPERTIES_MODELDATA);
        group1.addObject(new Property(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_IDENTIFIER, "", true));
        group1.addObject(new Property(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ABSTRACT, "", true));
        group1.addObject(new Property(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_TITLE, "", true));
        group1.addObject(new Property(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_VERSION, "", true));

        propertyGroups.add(group1);
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

    public GraphModel clone() {
        GraphModel clone = new GraphModel();
        try {
            clone.mergeChildren((mxCell) getRoot(), (mxICell) clone.getRoot(), true);
        } catch (CloneNotSupportedException ex) {
            Logger.log("GraphModel: clone not supported!! " + ex);
        }

        for (PropertyGroup pGroup : this.propertyGroups) {
            clone.propertyGroups.add((PropertyGroup) pGroup.clone());
        }

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
        for (PropertyGroup aGroup : propertyGroups) {
            IObjectWithProperties property = aGroup.getPropertyObject(propertyName);
            if (null != property && (property instanceof Property)) {
                return ((Property) property).getValue();
            }
        }
        return null;
    }

    public void addProperty(Property property) {
        propertyGroups.get(0).addObject(property);
    }

    public void setPropertyGroups(PropertyGroup[] propertyGroups) {
        this.propertyGroups.clear();

        if (null == propertyGroups) {
            return;
        }

        for (PropertyGroup group : propertyGroups) {
            this.propertyGroups.add(group);
        }
    }

    @Override
    public void setProperty(String propertyName, IObjectWithProperties property) {
        // find the parent group of the property 
        for (PropertyGroup group : this.propertyGroups) {

            // try to find the property inside the group
            for (Object aGroupProperty : group.getProperties()) {
                if (aGroupProperty instanceof IObjectWithProperties) {
                    IObjectWithProperties aProperty = (IObjectWithProperties) aGroupProperty;

                    // property in group found: set it to the given parameter
                    if (aProperty.getPropertiesObjectName().equals(propertyName)) {
                        group.setProperty(propertyName, property);
                    }

                }

            }
        }
    }

    @Override
    public void setPropertiesObjectName(String name) {
        // ignore, name is fix
    }

    @Override
    public boolean isTransient() {
        return false;
    }

}
