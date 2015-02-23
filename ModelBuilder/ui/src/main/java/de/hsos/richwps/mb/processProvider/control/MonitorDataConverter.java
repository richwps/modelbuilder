package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.exception.ProcessMetricProviderNotAvailable;
import de.hsos.richwps.mb.properties.PropertyGroup;

/**
 *
 * @author dziegenh
 */
public class MonitorDataConverter {
    
    private ProcessMetricProvider metricProvider;

    public void setMetricProvider(ProcessMetricProvider metricProvider) {
        this.metricProvider = metricProvider;
    }
    
    protected ProcessMetricProvider getMetricProvider() {
        return metricProvider;
    }
    
    public void addProcessMetrics(ProcessEntity process) throws ProcessMetricProviderNotAvailable {

        if(null == getMetricProvider()) {
            throw new ProcessMetricProviderNotAvailable();
        }
        
        // get metric properties group
        String server = process.getServer();
        String identifier = process.getOwsIdentifier();
        PropertyGroup processMetric = getMetricProvider().getProcessMetric(server, identifier);

        // add metric properties group to the process
        String metricPropertyName = processMetric.getPropertiesObjectName();
        processMetric.setIsTransient(true);
        process.setProperty(metricPropertyName, processMetric);
    }
    
}
