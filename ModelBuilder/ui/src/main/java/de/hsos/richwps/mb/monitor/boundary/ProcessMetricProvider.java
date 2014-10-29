/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.monitor.boundary;

import de.hsos.ecs.richwps.wpsmonitor.client.WpsMonitorClientException;
import de.hsos.ecs.richwps.wpsmonitor.client.WpsMonitorClientImpl;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessMetric;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *
 * @author dziegenh
 */
public class ProcessMetricProvider {

    private final WpsMonitorClientImpl client;

    public ProcessMetricProvider(String url) throws MalformedURLException {

        client = new WpsMonitorClientImpl(new URL(url));
    }

    public PropertyGroup<PropertyGroup<Property<String>>> getProcessMetric(String server, String identifier) throws WpsMonitorClientException, MalformedURLException {
        WpsProcessResource wpsProcess = client.getWpsProcess(new URL(server), identifier);

        PropertyGroup<PropertyGroup<Property<String>>> groups = new PropertyGroup<>();
        groups.setPropertiesObjectName("Monitor Data");
        
        for (Map.Entry<String, WpsProcessMetric> aMetric : wpsProcess.getMetrics().entrySet()) {
            PropertyGroup<Property<String>> subGroup = new PropertyGroup<>(translateMonitorKey(aMetric.getKey()));

            for (Map.Entry<String, Number> aMetricValue : aMetric.getValue().getData().entrySet()) {
                Property<String> property = new Property<>(aMetricValue.getKey(), Property.COMPONENT_TYPE_TEXTFIELD, aMetricValue.getValue().toString());
                subGroup.addObject(property);
            }
            
            groups.addObject(subGroup);
        }

        return groups;
    }

    private String translateMonitorKey(String key) {
        // TODO translate to readable caption
        return key;
    }

}
