package de.hsos.richwps.mb.monitor.boundary;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.MeasuredValue;
import de.hsos.ecs.richwps.wpsmonitor.client.WpsMonitorClient;
import de.hsos.ecs.richwps.wpsmonitor.client.WpsMonitorClientFactory;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsMetricResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsResource;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Integrates the RichWPS Monitor Client.
 *
 * @author dziegenh
 */
public class ProcessMetricProvider {

    public static final String PROPERTY_KEY_MONITOR_DATA = "monitor_data";
    public static final String PROPERTY_KEY_RESPONCE_METRIC = "response_metric";

    private HashMap<String, WpsResource> wpss;

    private WpsMonitorClient client;

    private String url;

    public ProcessMetricProvider() {
        this.wpss = new HashMap<>();
    }

    /**
     * Returns all property keys of metric properties.
     *
     * @return
     */
    public static String[] getPropertyKeys() {
        return new String[]{
            PROPERTY_KEY_RESPONCE_METRIC
        };
    }

    private boolean connect() {
        if (null != this.client && this.client.isReachable()) {
            return true;
        }

        try {
            this.client = new WpsMonitorClientFactory().create(new URL(url));

            if (!this.client.isReachable()) {
                return false;
            }

            List<WpsResource> allWps = this.client.getAllWps(true);

            this.wpss.clear();
            for (WpsResource wps : allWps) {
                final String urlString = wps.getWpsEndPoint().toExternalForm();
                this.wpss.put(urlString, wps);
            }

        } catch (Exception ex) {
            String msg = String.format(AppConstants.MONITOR_CONNECTION_ERROR, ex.getMessage());
            Object src = AppConstants.INFOTAB_ID_MONITOR;
            AppEvent.PRIORITY prio = AppEvent.PRIORITY.URGENT;

            AppEventService.getInstance().fireAppEvent(msg, src, prio);
            return false;
        }

        return true;
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
        groups.setPropertiesObjectName(PROPERTY_KEY_MONITOR_DATA);

        // try connect to monitor
        if ((null == client) && !connect()) {
            return groups;
        }

        try {

            // cancel if the processes' endpoint is not monitored
            server = new URL(server).toExternalForm();
            WpsResource wps = this.wpss.get(server);
            if (null == wps) {
                return groups;
            }

            WpsProcessResource wpsProcess = client.getWpsProcess(wps, identifier);

            // add metrics sub groups
            for (Map.Entry<String, WpsMetricResource> aMetric : wpsProcess.getMetrics().entrySet()) {
                PropertyGroup<Property<String>> subGroup = new PropertyGroup<>(aMetric.getKey());

                // add metric values to sub group as properties
                for (Map.Entry<String, MeasuredValue> aMetricValue : aMetric.getValue().getValues().entrySet()) {

                    // create property
                    String propertyName = aMetricValue.getKey();
                    String propertyType = Property.COMPONENT_TYPE_TEXTFIELD;
                    String propertyValue = aMetricValue.getValue().toString();
                    Property<String> property = new Property<>(propertyName, propertyType, propertyValue);

                    subGroup.addObject(property);
                }
                groups.addObject(subGroup);
            }
        } catch (Exception ex) {
            // ignore as usually thrown if process is not monitored;
            // the empty group indicates non-monitored processes.
            Logger.log("monitor client exception: " + ex);

            // force client recreation
            client = null;

        }

        return groups;
    }

    /**
     * Sets the given url for future process metric calls and forces the a
     * client recreation.
     *
     * @param url
     */
    public void setMonitorUrl(String url) {
        this.url = url;
        this.client = null;
    }

    /**
     * Returns the monitor URL that the client uses to connect.
     *
     * @return
     */
    public String getMonitorUrl() {
        return this.url;
    }

}
