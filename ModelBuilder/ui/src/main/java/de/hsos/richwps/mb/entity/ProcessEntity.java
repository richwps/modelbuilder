package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A specific WPS process with input and output ports.
 *
 * @author dziegenh
 */
public class ProcessEntity extends OwsObjectWithProperties {

    public static String PROPERTIES_KEY_SERVER = "Server";
    public static String PROPERTIES_KEY_INPUT_PORTS = "Inputs";
    public static String PROPERTIES_KEY_OUTPUT_PORTS = "Outputs";
    public static String PROPERTIES_KEY_VERSION = "Version";

    private LinkedList<ProcessPort> inputPorts;
    private LinkedList<ProcessPort> outputPorts;

    public static String toolTipCssForMainContainer;

    public ProcessEntity() {
        this("", "");
    }

    public ProcessEntity(String server, String owsIdentifier) {
        super(owsIdentifier);

        this.inputPorts = new LinkedList<>();
        this.outputPorts = new LinkedList<>();

        createProperties(server, owsIdentifier);
    }

    private HashMap<String, PropertyGroup<? extends IObjectWithProperties>> additionalGroups = new HashMap<>();

    /**
     * Sets a specific property. If it's a property group, it is added to the
     * additional property groups.
     *
     * @param propertyName
     * @param property
     */
    @Override
    public void setProperty(String propertyName, IObjectWithProperties property) {
        if (property instanceof PropertyGroup && !propertyName.equals(OWS_PROPERTY_GROUP_NAME)) {
            this.additionalGroups.put(propertyName, (PropertyGroup) property);
        } else {
            super.setProperty(propertyName, property);
        }
    }

    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        List<IObjectWithProperties> properties = new LinkedList<>();
        properties.add(owsGroup);

        // add inputs
        PropertyGroup inputsGroup = new PropertyGroup(PROPERTIES_KEY_INPUT_PORTS);
        inputsGroup.setIsTransient(true);
        if (null != inputPorts) {
            for (ProcessPort port : inputPorts) {
                inputsGroup.addObject(port);
            }
            properties.add(inputsGroup);
        }

        // add outputs
        PropertyGroup outputsGroup = new PropertyGroup(PROPERTIES_KEY_OUTPUT_PORTS);
        outputsGroup.setIsTransient(true);
        if (null != outputPorts) {
            for (ProcessPort port : outputPorts) {
                outputsGroup.addObject(port);
            }
            properties.add(outputsGroup);
        }

        // add additional groups
        for (PropertyGroup pGroup : additionalGroups.values()) {
            properties.add(pGroup);
        }

        return properties;
    }

    private void createProperties(String server, String owsIdentifier) {
        // create OWS object properties
        super.createProperties(owsIdentifier);

        // additional properties
        owsGroup.addObject(new Property<>(PROPERTIES_KEY_VERSION, Property.COMPONENT_TYPE_TEXTFIELD, ""));
        owsGroup.addObject(new Property<>(PROPERTIES_KEY_SERVER, Property.COMPONENT_TYPE_TEXTFIELD, server));
    }

    public void setInputPorts(LinkedList<ProcessPort> ports) {
        this.inputPorts = ports;
    }

    public void setOutputPorts(LinkedList<ProcessPort> ports) {
        this.outputPorts = ports;
    }

    public void setServer(String server) {
        owsGroup.getPropertyObject(PROPERTIES_KEY_SERVER).setValue(server);
    }

    public String getServer() {
        return (String) owsGroup.getPropertyObject(PROPERTIES_KEY_SERVER).getValue();
    }

    public int getNumInputs() {
        return inputPorts.size();
    }

    public int getNumOutputs() {
        return outputPorts.size();
    }

    @Override
    public String toString() {
        return getOwsTitle();
    }

    public void addInputPort(ProcessPort port) {
        port.setFlowInput(true);
        inputPorts.add(port);
    }

    public void addOutputPort(ProcessPort port) {
        port.setFlowOutput(true);
        outputPorts.add(port);
    }

    public List<ProcessPort> getInputPorts() {
        return inputPorts;
    }

    public List<ProcessPort> getOutputPorts() {
        return outputPorts;
    }

    @Override
    public String getToolTipText() {
        if (null == toolTipText) {

            String owsTitle = OwsObjectWithProperties.getValueForViews(getOwsTitle());
            String owsIdentifier = OwsObjectWithProperties.getValueForViews(getOwsIdentifier());
            String owsAbstract = OwsObjectWithProperties.getValueForViews(getOwsAbstract());

            // prepare input port TTTs
            List<String> inPortTexts = new LinkedList<>();
            int portTextsLength = 0;
            for (ProcessPort port : getInputPorts()) {
                String portTtt = port.getToolTipText();

                // remove html tags
                portTtt = portTtt.replaceAll("<html>", "").replaceAll("</html>", "");
                inPortTexts.add(portTtt);

                // ttt length + "<br>"
                portTextsLength += portTtt.length() + 4;
            }

            // prepare output port TTTs
            List<String> outPortTexts = new LinkedList<>();
            for (ProcessPort port : getOutputPorts()) {
                String portTtt = port.getToolTipText();

                // remove html tags
                portTtt = portTtt.replaceAll("<html>", "").replaceAll("</html>", "");
                outPortTexts.add(portTtt);

                portTextsLength += portTtt.length() + 4;
            }

// @TODO calculate new capacity after the refactoring! (maybe also use template strings / format() for an easy way to get the tags' length)
            // length of vars + length of port texts + size of "<html></html>" tags + size of "<b></b>" tags + size of "<hr>" tags + size of "<br>" tags
            int sbCapacity = owsTitle.length() + owsIdentifier.length() + owsAbstract.length() + portTextsLength + 13 + 7 + 4 + 8;

            StringBuilder sb = new StringBuilder(sbCapacity);
            sb.append("<html><body style='").append(ProcessEntity.toolTipCssForMainContainer).append("'><b>").append(owsTitle).append("</b><br>").append(owsIdentifier).append("<br><i>").append(owsAbstract).append("</i>");

            // add port TTTs if available
            if (!inPortTexts.isEmpty() && !outPortTexts.isEmpty()) {

                for (String text : inPortTexts) {
                    sb.append(text);
                }

                for (String text : outPortTexts) {
                    sb.append(text);
                }
            }

            sb.append("</body></html>");
            toolTipText = sb.toString();
        }

        return toolTipText;
    }

    @Override
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    /**
     * ProcessEntities are equal if their server and identifier match.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof ProcessEntity)) {
            return false;
        }

        ProcessEntity other = (ProcessEntity) obj;
        return other.getServer().equals(this.getServer()) && other.getOwsIdentifier().equals(this.getOwsIdentifier());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.getServer());
        hash = 83 * hash + Objects.hashCode(this.getOwsIdentifier());
        return hash;
    }

    public ProcessEntity clone() {
        ProcessEntity clone = new ProcessEntity();
        super.cloneInto(clone);

        clone.setServer(getServer());
        clone.toolTipText = null; // indicate lazy init.

        for (ProcessPort port : getInputPorts()) {
            clone.addInputPort(port.clone());
        }

        for (ProcessPort port : getOutputPorts()) {
            clone.addOutputPort(port.clone());
        }

        for (Entry<String, PropertyGroup<? extends IObjectWithProperties>> addGroup : additionalGroups.entrySet()) {
            clone.setProperty(addGroup.getKey(), addGroup.getValue());
        }

        return clone;
    }

    public void setOwsPropertyValue(String propertyKey, Object value) {
        Property property = this.owsGroup.getPropertyObject(propertyKey);
        if (null != property) {
            property.setValue(value);
        }
    }

    /**
     * Replaces all values by cloning the values of the given process.
     *
     * @param other
     */
    public void copyValuesFrom(ProcessEntity other) {
        if (System.identityHashCode(this) == System.identityHashCode(other)) {
            return;
        }

        super.copyValuesFrom(other);

        setServer(other.getServer());

        this.inputPorts = new LinkedList<>();
        this.outputPorts = new LinkedList<>();
        this.additionalGroups = new HashMap<>();

        for (ProcessPort port : other.getInputPorts()) {
            this.addInputPort(port.clone());
        }

        for (ProcessPort port : other.getOutputPorts()) {
            this.addOutputPort(port.clone());
        }

        for (Entry<String, PropertyGroup<? extends IObjectWithProperties>> addGroup : other.additionalGroups.entrySet()) {
            this.setProperty(addGroup.getKey(), addGroup.getValue());
        }
    }
}
