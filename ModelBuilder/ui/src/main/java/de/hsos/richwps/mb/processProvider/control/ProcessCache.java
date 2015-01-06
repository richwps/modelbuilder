package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.entity.ProcessKey;
import de.hsos.richwps.mb.processProvider.entity.ProcessLoadingStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dziegenh
 */
public class ProcessCache {

    private HashMap<ProcessKey, ProcessEntity> cache = new HashMap<>();

    private HashMap<ProcessKey, ProcessLoadingStatus> loadingStatus = new HashMap<>();

    /**
     * Gets a process from the cache.
     *
     * @param server identifies the process.
     * @param identifier identifies the process.
     * @return the chached process or null if it is not cached.
     */
    public ProcessEntity getCachedProcess(String server, String identifier) {
        ProcessKey key = new ProcessKey(server, identifier);

        return this.cache.get(key);
    }

    /**
     * Adds the process to the cache.
     *
     * @param process the process which should be cached.
     * @param loadingComplete true if the process loading is completed.
     * @return true if the process is added, false if the process data is
     * incomplete or the process has already been added.
     */
    public boolean addProcess(ProcessEntity process, boolean loadingComplete) {
        if (null == process) {
            return false;
        }

        String server = process.getServer();
        String identifier = process.getOwsIdentifier();

        if (null == server || null == identifier) {
            return false;
        }

        ProcessKey key = new ProcessKey(server, identifier);

        // update the process if it's already cached
        if (this.cache.containsKey(key)) {
            ProcessEntity cached = this.cache.get(key);
            cached.copyValuesFrom(process);
        } else {

            // add process + loading status to cache
            this.cache.put(key, process);
        }

        // set the processes' loading status
        ProcessLoadingStatus status = this.loadingStatus.get(key);
        if (true == loadingComplete) {
            status = ProcessLoadingStatus.COMPLETE;
        }
        if (null == status) {
            status = ProcessLoadingStatus.NOT_LOADED;
        }
        this.loadingStatus.put(key, status);

        return true;
    }

    /**
     * Return true if the given process is in the cache and completely loaded.
     *
     * @param server
     * @param identifier
     * @return
     */
    public boolean isLoaded(String server, String identifier) {
        ProcessKey key = new ProcessKey(server, identifier);
        ProcessLoadingStatus status = this.loadingStatus.get(key);

        if (null == status) {
            return false;
        }

        return status.equals(ProcessLoadingStatus.COMPLETE);
    }

    /**
     * Returns the loading status for the given process or null if the process
     * is not cached.
     *
     * @param server
     * @param identifier
     * @return
     */
    public ProcessLoadingStatus getLoadingStatus(String server, String identifier) {
        ProcessKey key = new ProcessKey(server, identifier);
        return this.loadingStatus.get(key);
    }

    /**
     * Sets the loading status for the given process.
     *
     * @param server identifies the process.
     * @param identifier identifies the process.
     * @param isLoaded loading status which should be set.
     */
    public void setIsLoaded(String server, String identifier, boolean isLoaded) {
        ProcessKey key = new ProcessKey(server, identifier);

        ProcessLoadingStatus status = ProcessLoadingStatus.COMPLETE;
        if (!isLoaded) {
            status = ProcessLoadingStatus.NOT_LOADED;
        }

        this.loadingStatus.put(key, status);
    }

    public void resetLoadingStates() {
        HashMap<ProcessKey, ProcessLoadingStatus> result = new HashMap<>();

        Set<Map.Entry<ProcessKey, ProcessLoadingStatus>> entries = this.loadingStatus.entrySet();

        for (Map.Entry<ProcessKey, ProcessLoadingStatus> anEntry : entries) {
            if (anEntry.getValue().equals(ProcessLoadingStatus.COMPLETE)) {
                result.put(anEntry.getKey(), ProcessLoadingStatus.RESET);
            } else {
                result.put(anEntry.getKey(), anEntry.getValue());
            }
        }

        this.loadingStatus = result;
    }

}
