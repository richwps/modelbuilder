package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.properties.propertyComponents.PropertyMultilineLabel;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A specific WPS process with input and output ports.
 *
 * @author dziegenh
 */
public class ProcessEntity implements IOwsObject, IObjectWithProperties, Serializable {

    public static String KEY_IDENTIFIER;
    public static String KEY_SERVER;
    public static String KEY_TITLE;
    public static String KEY_ABSTRACT;

    private LinkedList<ProcessPort> inputPorts;
    private LinkedList<ProcessPort> outputPorts;
    private String toolTipText;
    private boolean isLocal;

    public static String toolTipCssForMainContainer;

    private boolean isFullyLoaded = false;

    private HashMap<String, AbstractPropertyComponent> properties = new HashMap<>();
    private PropertyGroup owsGroup;

    public ProcessEntity() {
        this("", "");
    }

    public ProcessEntity(String server, String owsIdentifier) {
//        this.server = server;
//        this.owsIdentifier = owsIdentifier;
        this.isLocal = false;

        this.inputPorts = new LinkedList<>();
        this.outputPorts = new LinkedList<>();

        createProperties(server, owsIdentifier);
    }

    public void setIsFullyLoaded(boolean isFullyLoaded) {
        this.isFullyLoaded = isFullyLoaded;
    }

    public boolean isIsFullyLoaded() {
        return isFullyLoaded;
    }

    public void setInputPorts(LinkedList<ProcessPort> ports) {
        this.inputPorts = ports;
    }

    public void setOutputPorts(LinkedList<ProcessPort> ports) {
        this.outputPorts = ports;
    }

    /**
     * Sets the title and resets the toolTipText.
     *
     * @param owsTitle
     */
    @Override
    public void setOwsTitle(String owsTitle) {
        owsGroup.getPropertyComponent(KEY_TITLE).setValue(owsTitle);
        toolTipText = null;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     *
     * @param owsIdentifier
     */
    @Override
    public void setOwsIdentifier(String owsIdentifier) {
        owsGroup.getPropertyComponent(KEY_IDENTIFIER).setValue(owsIdentifier);
        toolTipText = null;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     *
     * @param owsAbstract
     */
    @Override
    public void setOwsAbstract(String owsAbstract) {
        owsGroup.getPropertyComponent(KEY_ABSTRACT).setValue(owsAbstract);
        toolTipText = null;
    }

    public void setServer(String server) {
        owsGroup.getPropertyComponent(KEY_SERVER).setValue(server);
    }

    public String getServer() {
        return (String) owsGroup.getPropertyComponent(KEY_SERVER).getValue();
    }

    @Override
    public String getOwsIdentifier() {
        return (String) owsGroup.getPropertyComponent(KEY_IDENTIFIER).getValue();
    }

    @Override
    public String getOwsTitle() {
        return (String) owsGroup.getPropertyComponent(KEY_TITLE).getValue();
    }

    @Override
    public String getOwsAbstract() {
        return (String) owsGroup.getPropertyComponent(KEY_ABSTRACT).getValue();
    }
    
    public int getNumInputs() {
        return inputPorts.size();
    }

    public int getNumOutputs() {
        return outputPorts.size();
    }

    public boolean isIsLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
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


    public String getToolTipText() {
        if (null == toolTipText) {

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

// @TODO calculate new capacity after the refactoring!
            // length of vars + length of port texts + size of "<html></html>" tags + size of "<b></b>" tags + size of "<hr>" tags + size of "<br>" tags
            int sbCapacity = getOwsTitle().length() + getOwsIdentifier().length() + getOwsAbstract().length() + portTextsLength + 13 + 7 + 4 + 8;

            StringBuilder sb = new StringBuilder(sbCapacity);
            sb.append("<html><body style='").append(ProcessEntity.toolTipCssForMainContainer).append("'><b>").append(getOwsTitle()).append("</b><br>").append(getOwsIdentifier()).append("<br><i>").append(getOwsAbstract()).append("</i>");

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

    public ProcessEntity clone() {
        ProcessEntity clone = new ProcessEntity(getServer(), getOwsIdentifier());
        clone.setOwsAbstract(getOwsAbstract());
        clone.setOwsTitle(getOwsTitle());
        clone.toolTipText = null; // indicate lazy initialisation

        for (ProcessPort inPort : inputPorts) {
            clone.addInputPort(inPort.clone());
        }

        for (ProcessPort outPort : outputPorts) {
            clone.addOutputPort(outPort.clone());
        }

        return clone;
    }

    @Override
    public String getPropertiesObjectName() {
        return "ProcessEntity";
    }

    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        return Arrays.asList(new IObjectWithProperties[]{
            owsGroup
        });
    }

    private void createProperties(String server, String owsIdentifier) {
        owsGroup = new PropertyGroup("Process");
        owsGroup.addComponent(new PropertyMultilineLabel(KEY_IDENTIFIER, owsIdentifier, false));
        owsGroup.addComponent(new PropertyMultilineLabel(KEY_SERVER, server, false));
        owsGroup.addComponent(new PropertyMultilineLabel(KEY_TITLE, "", false));
        owsGroup.addComponent(new PropertyMultilineLabel(KEY_ABSTRACT, "", false));
    }

}
