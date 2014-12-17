package de.hsos.richwps.mb.monitor.boundary;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.MeasuredValue;
import de.hsos.ecs.richwps.wpsmonitor.client.WpsMonitorClient;
import de.hsos.ecs.richwps.wpsmonitor.client.WpsMonitorClientFactory;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsMetricResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.ui.UiHelper;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Integrates the RichWPS Monitor Client.
 *
 * @author dziegenh
 */
public class ProcessMetricProvider {

    private final WpsMonitorClient client;

    private final HashMap<String, String> translations;

    private String mainPropertyGroupName = "monitor data";

    public ProcessMetricProvider(String url) throws Exception {
        client = new WpsMonitorClientFactory().create(new URL(url));
        translations = new HashMap<>();
    }

    public void setMainPropertyGroupName(String mainPropertyGroupName) {
        this.mainPropertyGroupName = mainPropertyGroupName;
    }

    /**
     * Receives metric values from the monitor client for the given process and
     * creates representing property groups.
     *
     * @param server
     * @param identifier
     * @return
     */
    public PropertyGroup getProcessMetric(String server, String identifier) {

        // create property group containing all metrics
        PropertyGroup<PropertyGroup<Property<String>>> groups = new PropertyGroup<>();
        groups.setPropertiesObjectName(this.mainPropertyGroupName);

        try {
            WpsProcessResource wpsProcess = client.getWpsProcess(new URL(server), identifier);

            // add metrics sub groups
            for (Map.Entry<String, WpsMetricResource> aMetric : wpsProcess.getMetrics().entrySet()) {
                PropertyGroup<Property<String>> subGroup = new PropertyGroup<>(translateMonitorKey(aMetric.getKey()));

                // add metric values to sub group as properties
                //TODO adjust API usage.
                for (Map.Entry<String, MeasuredValue> aMetricValue : aMetric.getValue().getValues().entrySet()) {
//
//                 // create property
                    String propertyName = translateMonitorKey(aMetricValue.getKey());
                    String propertyType = Property.COMPONENT_TYPE_TEXTFIELD;
                    String propertyValue = aMetricValue.getValue().toString();
                    Property<String> property = new Property<>(propertyName, propertyType, propertyValue);
//
                    subGroup.addObject(property);
                }
                groups.addObject(subGroup);
            }
        } catch (Exception ex) {
            // ignore as usually thrown if process is not monitored;
            // the empty group indicates non-monitored processes.
            Logger.log("monitor client exception: " + ex);
        }

        return groups;
    }

    /**
     * Sets a readable translation for a key used by the monitor.
     *
     * @param monitorKey
     * @param translation
     */
    public void addMonitorKeyTranslation(String monitorKey, String translation) {
        this.translations.put(monitorKey, translation);
    }

    /**
     * Translates monitor keys to readable ModelBuilder property name.
     *
     * @param key a key used by the monitor
     * @return readable property name
     */
    private String translateMonitorKey(String key) {

        // return translation if exists
        if (this.translations.containsKey(key)) {
            return this.translations.get(key);
        }

        // else: return with uppered first character
        return UiHelper.upperFirst(key);
    }

}
