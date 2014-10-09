package de.hsos.richwps.mb.semanticProxy.entity;

import de.hsos.richwps.mb.entity.ProcessEntity;
import java.util.LinkedList;
import java.util.List;

/**
 * Entity holding a WPS server.
 *
 * @author dziegenh
 */
public class WpsServer {

    private String endpoint = "";

    private List<ProcessEntity> processes = new LinkedList<>();

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
        return endpoint;
    }

    public List<ProcessEntity> getProcesses() {
        return processes;
    }

    public void addProcess(ProcessEntity process) {
        processes.add(process);
    }

}
