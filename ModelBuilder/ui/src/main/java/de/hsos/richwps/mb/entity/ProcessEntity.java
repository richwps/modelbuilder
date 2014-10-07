package de.hsos.richwps.mb.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A specific WPS process with input and output ports.
 *
 * @author dziegenh
 */
public class ProcessEntity implements IOwsObject, Serializable {

    private String owsTitle;
    private String owsAbstract;
    private String server;
    private String owsIdentifier;

    private LinkedList<ProcessPort> inputPorts;
    private LinkedList<ProcessPort> outputPorts;
    private String toolTipText;
    private boolean isLocal;
    
    public static String toolTipCssForMainContainer;

    private boolean isFullyLoaded = false;

    public ProcessEntity() {
        this("", "");
    }

    public ProcessEntity(String server, String owsIdentifier) {
        this.server = server;
        this.owsIdentifier = owsIdentifier;
        this.isLocal = false;

        this.inputPorts = new LinkedList<>();
        this.outputPorts = new LinkedList<>();
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
        this.owsTitle = owsTitle;
        toolTipText = null;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     *
     * @param owsIdentifier
     */
    @Override
    public void setOwsIdentifier(String owsIdentifier) {
        this.owsIdentifier = owsIdentifier;
        toolTipText = null;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     *
     * @param owsAbstract
     */
    @Override
    public void setOwsAbstract(String owsAbstract) {
        this.owsAbstract = owsAbstract;
        toolTipText = null;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    @Override
    public String getOwsIdentifier() {
        return owsIdentifier;
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

    @Override
    public String getOwsTitle() {
        return owsTitle;
    }

    @Override
    public String getOwsAbstract() {
        return owsAbstract;
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
        ProcessEntity clone = new ProcessEntity(server, owsIdentifier);
        clone.owsAbstract = owsAbstract;
        clone.owsTitle = owsTitle;
        clone.toolTipText = null; // indicate lazy initialisation

        for (ProcessPort inPort : inputPorts) {
            clone.addInputPort(inPort.clone());
        }

        for (ProcessPort outPort : outputPorts) {
            clone.addOutputPort(outPort.clone());
        }

        return clone;
    }

}
