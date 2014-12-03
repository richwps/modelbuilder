package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.exception.ProcessMetricProviderNotAvailable;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dziegenh
 */
public class MonitorDataConverter {
    
    private ProcessMetricProvider metricProvider;

    protected ProcessMetricProvider getMetricProvider() {
        if(null == metricProvider) {
            try {
                metricProvider = new ProcessMetricProvider(null);
            } catch (MalformedURLException ex) {
                Logger.getLogger(MonitorDataConverter.class.getName()).log(Level.SEVERE, null, ex);
                
                // returning null indicates the error
            }
        }
        
        return metricProvider;
    }
    
    
    public void addProcessMetrics(ProcessEntity process) throws ProcessMetricProviderNotAvailable {

        // FIXME re-enable process metrics when monitor client is faster !!! (currently THE bottleneck!)
//        if(null == getMetricProvider()) {
//            throw new ProcessMetricProviderNotAvailable();
//        }
//        
//        // get metric properties group
//        String server = process.getServer();
//        String identifier = process.getOwsIdentifier();
//        PropertyGroup processMetric = processMetricProvider.getProcessMetric(server, identifier);
//
//        // add metric properties group to the process
//        String metricPropertyName = processMetric.getPropertiesObjectName();
//        processMetric.setIsTransient(true);
//        process.setProperty(metricPropertyName, processMetric);
    }
    
}
