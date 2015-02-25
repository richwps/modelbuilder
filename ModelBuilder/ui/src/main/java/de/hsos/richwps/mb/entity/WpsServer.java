package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.ui.UiHelper;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Entity holding a WPS server.
 *
 * @author dziegenh
 */
public class WpsServer {

    private WpsServerSource source;

    private String endpoint = "";

    private final List<ProcessEntity> processes = new LinkedList<>();

    public WpsServer(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(endpoint.length() + 10);
        String processString = "0";
        if (null != this.processes) {
            processString = "" + this.processes.size();
        }
        sb.append(this.endpoint).append(" [").append(processString).append("]");
        return endpoint;
    }

    public List<ProcessEntity> getProcesses() {
        return processes;
    }

    public void addProcess(ProcessEntity process) {
        processes.add(process);
    }

    public void setSource(WpsServerSource source) {
        this.source = source;
    }

    public WpsServerSource getSource() {
        return source;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof WpsServer)) {
            return false;
        }

        WpsServer other = (WpsServer) obj;

        boolean equal = UiHelper.equalOrBothNull(this.endpoint, other.endpoint);
        equal &= UiHelper.equalOrBothNull(this.processes, other.processes);

        return equal;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        if (null != this.endpoint) {
            hash = 29 * hash + Objects.hashCode(this.endpoint);
        }
        if (null != this.processes) {
            hash = 13 * hash + Objects.hashCode(this.processes);
        }

        return hash;
    }

}
