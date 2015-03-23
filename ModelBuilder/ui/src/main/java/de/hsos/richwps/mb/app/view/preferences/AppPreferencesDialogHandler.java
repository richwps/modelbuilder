package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.treeView.MainTreeViewController;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class AppPreferencesDialogHandler extends WindowAdapter {

    private ProcessProvider processProvider;
    private ProcessMetricProvider processMetricProvider;
    private MainTreeViewController mainTreeView;

    private String[] prePersistedRemotes;
    private List<String> preRemotesAsList;

    public void setProcessMetricProvider(ProcessMetricProvider processMetricProvider) {
        this.processMetricProvider = processMetricProvider;
    }

    public void setProcessProvider(ProcessProvider processProvider) {
        this.processProvider = processProvider;
    }

    public void setMainTreeView(MainTreeViewController mainTreeView) {
        this.mainTreeView = mainTreeView;
    }
    
    @Override
    public void windowOpened(WindowEvent e) {
        prePersistedRemotes = processProvider.getPersistedRemotes();
        preRemotesAsList = Arrays.asList(prePersistedRemotes);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // get persisted configuration value.
        String key = AppConfig.CONFIG_KEYS.MONITOR_S_URL.name();
        String defaultValue = AppConstants.MONITOR_DEFAULT_URL;
        String confUrl = AppConfig.getConfig().get(key, defaultValue);

        // get currently used value.
        String monitorUrl = processMetricProvider.getMonitorUrl();

        // update curently used value if the config has changed.
        boolean monitorSettingsChanged = !confUrl.equals(monitorUrl);
        if (monitorSettingsChanged) {
            processMetricProvider.setMonitorUrl(confUrl);
        }

        // check if remotes changed
        String[] persistedRemotes = processProvider.getPersistedRemotes();
        List<String> remotesAsList = Arrays.asList(persistedRemotes);
        boolean remotesChanged = !remotesAsList.containsAll(preRemotesAsList);

        // force reloading monitor data
        if (monitorSettingsChanged || remotesChanged) {
            processProvider.resetProcessLoadingStates();
            mainTreeView.fillTree();
        }
    }

}
