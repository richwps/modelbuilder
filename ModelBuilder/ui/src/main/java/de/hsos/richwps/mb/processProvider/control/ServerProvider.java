package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.ows.gettypes.Network;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 *
 * @author dziegenh
 */
public class ServerProvider {

    private Network net;
    private WPS[] wpss;

    public void setNet(Network net) {
        this.net = net;
        clearCache();
    }

    public void clearCache() {
        this.wpss = null;
    }

    public WPS[] getWPSs() throws Exception {
        if (null == this.wpss) {
            this.wpss = net.getWPSs();
        }

        return this.wpss;
    }

    public Collection<String> getAllServersFromSemanticProxy() {
        LinkedList<String> servers = new LinkedList<>();

        // indicate error occurences but don't abort loading servers.
        // (errors are handled after the loading is done)
        String errorMsg = null;

        if (null != net) {
            try {
                getWPSs();
            } catch (Exception ex) {
                errorMsg = ex.getMessage();
            }

            if (null == errorMsg) {
                for (WPS wps : wpss) {
                    try {
                        servers.add(wps.getEndpoint());
                    } catch (RDFException ex) {
                        errorMsg = ex.getMessage();
                    }
                }
            }
        }

        // handle occured errors
        if (null != errorMsg) {
            String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, "Exception", errorMsg);
            AppEventService.getInstance().fireAppEvent(msg, this);
        }

        return servers;
    }

    public String[] getPersistedRemotes() {

        // TODO let app set these values !!!
        Preferences preferences = AppConfig.getConfig();
        String persistKeyBase = AppConfig.CONFIG_KEYS.REMOTES_S_URL.name();
        String persistKeyCount = persistKeyBase + "_COUNT";
        String persistKeyFormat = persistKeyBase + "_%d";

        String currentItem = preferences.get(persistKeyBase, "");
        int count = preferences.getInt(persistKeyCount, 0);

        boolean currentItemAvailable = null != currentItem && !currentItem.isEmpty();
        boolean currentItemAdded = !currentItemAvailable;

        // load and add persisted items
        List<String> loadedItems = new LinkedList<>();
        for (int c = 0; c < count; c++) {
            String key = String.format(persistKeyFormat, c);
            String value = preferences.get(key, "").trim();

            if (null != value) {
                // avoid duplicates
                if (!loadedItems.contains(value)) {
                    loadedItems.add(value);
                }

                // remember if the currently used item has been added
                if (value.equals(currentItem)) {
                    currentItemAdded = true;
                }
            }
        }

        // assure the list contains the current item after loading
        if (!currentItemAdded) {
            loadedItems.add(currentItem);
        }

        return loadedItems.toArray(new String[]{});
    }

    public String[] getAllServers() {
        Collection<String> spServers = getAllServersFromSemanticProxy();
        String[] remotes = getPersistedRemotes();

        String[] servers = new String[spServers.size() + remotes.length];
        int i = 0;

        // add SP servers
        for (String server : spServers) {
            servers[i++] = server;
        }

        // add persisted remotes
        for (String server : remotes) {
            servers[i++] = server;
        }

        return servers;
    }

    public WPS getSpWpsByEndpoint(String serverEndpoint) throws RDFException {
        return ServerProvider.getSpWpsByEndpoint(this.wpss, serverEndpoint);
    }

    public static WPS getSpWpsByEndpoint(WPS[] wpss, String serverEndpoint) throws RDFException {
        for (WPS aWps : wpss) {
            if (aWps.getEndpoint().equals(serverEndpoint)) {
                return aWps;
            }
        }

        return null;
    }
}
