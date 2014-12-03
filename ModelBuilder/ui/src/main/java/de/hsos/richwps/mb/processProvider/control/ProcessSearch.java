package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.sp.client.ows.SPClient;
import java.util.LinkedList;
import java.util.List;
import de.hsos.richwps.sp.client.ows.gettypes.Process;

/**
 *
 * @author dziegenh
 */
public class ProcessSearch {
    private final SPClient spClient;

    public ProcessSearch(SPClient spClient) {
        this.spClient = spClient;
    }

    public List<ProcessEntity> getProcessesByKeyword(String query) {

        List<ProcessEntity> processes = new LinkedList<>();

        Process[] searchResult = new Process[]{};

        try {
            searchResult = spClient.searchProcessByKeyword(query);
        } catch (Exception ex) {
            Logger.log("Error searching process at SP: " + ex);
        }

        for (Process aProcess : searchResult) {
            try {
                ProcessEntity processEntity = EntityConverter.createProcessEntity(aProcess);
                processes.add(processEntity);

            } catch (Exception ex) {
                Logger.log("Error receiving process from SP: " + ex);
            }
        }

        return processes;
    }
}
