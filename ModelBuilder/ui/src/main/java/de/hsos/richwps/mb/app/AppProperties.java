package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentComplexDataType;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentQosAnalysis;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentQosTargets;
import de.hsos.richwps.mb.entity.QoSAnaylsis;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.processProvider.boundary.DatatypeProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.IPropertyChangeListener;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.awt.Window;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class AppProperties {

    private AppGraphView graphView;

    private AppPropertiesView propertiesView;

    private AppActionProvider actionProvider;

    private DatatypeProvider datatypeProvider;
    private Window parentFrame;

    private final IPropertyChangeListener manageQosTargetsListener;
    private HashMap<String, double[]> processMetrics;

    public AppProperties() {
        manageQosTargetsListener = new IPropertyChangeListener() {
            @Override
            public void propertyChanged(Object source, IPropertyChangeListener.PropertyChangeType changeType) {
                setupQosGroup(graphView.getQosProperties());
                propertiesView.resetCurrentComponents();
            }
        };

        this.processMetrics = new HashMap<>();
    }

    public void setGraphView(AppGraphView graphView) {
        this.graphView = graphView;
    }

    public void setPropertiesView(AppPropertiesView propertiesView) {
        this.propertiesView = propertiesView;
    }

    public void setDatatypeProvider(DatatypeProvider datatypeProvider) {
        this.datatypeProvider = datatypeProvider;
    }

    public void setParentFrame(Window parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setActionProvider(AppActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    PropertyComponentComplexDataType createPropertyComplexDataTypeFormat(Property<DataTypeDescriptionComplex> property, AppPropertiesView appPropertiesView) {
        PropertyComponentComplexDataType propertyComplexDataTypeFormat = null;
        try {
            List<ComplexDataTypeFormat> formats = datatypeProvider.getComplexDatatypes();
            propertyComplexDataTypeFormat = new PropertyComponentComplexDataType(parentFrame, formats);
            propertyComplexDataTypeFormat.setProperty(property);
        } catch (LoadDataTypesException ex) {
            actionProvider.getAction(AppActionProvider.APP_ACTIONS.SHOW_ERROR_MSG).fireActionPerformed(AppConstants.FORMATS_CSV_FILE_LOAD_ERROR);
        }
        return propertyComplexDataTypeFormat;
    }

    /**
     * Recursive search for model QoS target properties.
     *
     * @param modelProperties
     * @return
     */
    PropertyGroup getModelQosProperties(Collection<? extends IObjectWithProperties> modelProperties) {
        PropertyGroup foundProperty = null;
        for (IObjectWithProperties p : modelProperties) {
            if (p instanceof PropertyGroup) {
                if (p.getPropertiesObjectName().equals(ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS)) {
                    foundProperty = (PropertyGroup) p;
                } else {
                    PropertyGroup subProperty = getModelQosProperties(p.getProperties());
                    if (null != subProperty) {
                        foundProperty = subProperty;
                    }
                }
            }
        }
        return foundProperty;
    }

    protected Property createEndpointProperty() {
        String propertyEndpointName = GraphModel.PROPERTIES_KEY_OWS_ENDPOINT;
        String propertyEndpointType = Property.COMPONENT_TYPE_DROPDOWN;
        Property property = new Property(propertyEndpointName, propertyEndpointType, null, true);
        property.setPossibleValuesTransient(true);
        return property;
    }

    /**
     * Recursive search for model endpoint property.
     *
     * @param modelProperties
     * @return
     */
    Property getModelEndpointProperty(Collection<? extends IObjectWithProperties> modelProperties) {
        Property foundProperty = null;
        for (IObjectWithProperties p : modelProperties) {
            if (p instanceof Property && p.getPropertiesObjectName().equals(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT)) {
                foundProperty = (Property) p;
            } else if (p instanceof PropertyGroup) {
                Property subProperty = getModelEndpointProperty(p.getProperties());
                if (null != subProperty) {
                    foundProperty = subProperty;
                }
            }
        }
        return foundProperty;
    }

    public void setupQosGroup(final PropertyGroup qosGroup) {
        String manageTargetsKey = PropertyComponentQosTargets.PROPERTY_NAME;
        Property<List<QoSTarget>> modelQosProperties = (Property<List<QoSTarget>>) qosGroup.getPropertyObject(manageTargetsKey);
        if (null == modelQosProperties) {
            modelQosProperties = new Property<>(PropertyComponentQosTargets.PROPERTY_NAME);
            modelQosProperties.setValue(new LinkedList<QoSTarget>());
            modelQosProperties.setComponentType(PropertyComponentQosTargets.COMPONENT_TYPE);
            modelQosProperties.setEditable(true);
            qosGroup.setProperty(manageTargetsKey, modelQosProperties);
        }
        // remove existing listener if exists.
        modelQosProperties.removeChangeListener(manageQosTargetsListener);
        modelQosProperties.addChangeListener(manageQosTargetsListener);

        // remove old properties
        List<QoSTarget> targets = modelQosProperties.getValue();
        Collection properties = new LinkedList<>(qosGroup.getProperties());
        for (Object aObject : properties) {
            if (aObject instanceof Property) {
                Property aProperty = (Property) aObject;
                if (!aProperty.getComponentType().equals(PropertyComponentQosTargets.COMPONENT_TYPE)) {
                    qosGroup.removeProperty(aProperty.getPropertiesObjectName());
                }
            }
        }

        // add target properties
        for (QoSTarget qoSTarget : targets) {
            final String metricTarget = qoSTarget.getTargetTitle();

            double worst = 0d, median = 0d, best = 0d;
            if (this.processMetrics.containsKey(metricTarget)) {
                double[] values = processMetrics.get(metricTarget);
                best = values[0];
                median = values[1];
                worst = values[2];
            }

            QoSAnaylsis qoSAnaylsis = new QoSAnaylsis(qoSTarget, worst, median, best);
            Property<QoSAnaylsis> targetProperty = new Property<>(metricTarget, qoSAnaylsis);
//            targetProperty.setTranslatable(false);
            targetProperty.setIsTransient(true);
            targetProperty.setComponentType(PropertyComponentQosAnalysis.COMPONENT_TYPE);
            qosGroup.addObject(targetProperty);
        }
    }

    void setMonitorData(HashMap<String, double[]> processMetrics) {
        this.processMetrics = processMetrics;
        updateMonitorData();
    }

    /**
     * Updates the QoS Analysis properties with the monitor values.
     */
    public void updateMonitorData() {
        List<QoSAnaylsis> unsetTargets = new LinkedList();

        PropertyGroup qosProperties = graphView.getQosProperties();
        final String qosComponentType = PropertyComponentQosAnalysis.COMPONENT_TYPE;
        for (Object aQosGroup : qosProperties.getProperties()) {
            if (aQosGroup instanceof Property) {
                Property qosProperty = (Property) aQosGroup;
                final String metricKey = qosProperty.getPropertiesObjectName();
                if (qosProperty.getComponentType().equals(qosComponentType)) {
                    QoSAnaylsis analysis = (QoSAnaylsis) qosProperty.getValue();
                    if (processMetrics.containsKey(metricKey)) {
                        double[] values = processMetrics.get(metricKey);
                        analysis.setBest(values[0]);
                        analysis.setMedian(values[1]);
                        analysis.setWorst(values[2]);
                    } else {
                        unsetTargets.add(analysis);
                    }
                }
            }
        }

        // reset targets without monitor values
        for (QoSAnaylsis analysis : unsetTargets) {
            analysis.setBest(0);
            analysis.setMedian(0);
            analysis.setWorst(0);
        }
    }
}
