/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.AppFrame;
import de.hsos.richwps.mb.app.view.AppSplashScreen;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.appEvents.IAppEventObserver;
import de.hsos.richwps.mb.server.Mock;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 *
 * @author dziegenh
 */
public class AppSetup {

    public static void setup(final App app, boolean debugMode) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        {
            //
            app.frame = new AppFrame(AppConstants.FRAME_TITLE);
            AppSplashScreen splash = new AppSplashScreen();
            splash.showProgess(0);

            splash.showMessage("Loading config");
            {
                String hostKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_HOST.name();
                String portKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_PORT.name();

                String host = AppConfig.getConfig().get(hostKey, "");
                System.setProperty("http.proxyHost", host);

                String port = AppConfig.getConfig().get(portKey, "");
                System.setProperty("http.proxyPort", port);
            }
            splash.showProgess(7);

            splash.showMessage("Loading resources");

            // Load icons etc. into UIManager
            AppRessourcesLoader.loadAll();

            // Optional Debug Logging.
            if (debugMode) {
                AppConstants.DEBUG_MODE = true;
                AppEventService.getInstance().registerObserver(new IAppEventObserver() {
                    @Override
                    public void eventOccured(AppEvent e) {
                        de.hsos.richwps.mb.Logger.log(e);
                    }
                });
            }

            splash.showMessageAndProgress("Creating window", 15);

            // Create frame.
            app.getFrame().init(app);
            app.getFrame().setIconImage(((ImageIcon) UIManager.getIcon(AppConstants.ICON_MBLOGO_KEY)).getImage());

            // Delegate frame closing action
            app.getFrame().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    app.getActionProvider().fire(AppActionProvider.APP_ACTIONS.EXIT_APP);
                }
            });

            splash.showMessageAndProgress("Initialising tooltips", 30);

            // Setup ToolTip.
            ToolTipManager.sharedInstance().setInitialDelay(AppConstants.TOOLTIP_INITIAL_DELAY);
            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);

            splash.showMessageAndProgress("Initialising user interactions", 45);

            app.initDragAndDrop();

            app.getPreferencesDialog().init();
            app.getGraphView().init();
            splash.showMessageAndProgress("Requesting processes", 60);

            // connect to SP and fill tree with services etc. received from SP
            app.fillMainTree();
            // TODO mocked server class for app events -> replace when server client exists!!
            AppEventService.getInstance().addSourceCommand(Mock.getInstance(), AppConstants.INFOTAB_ID_SERVER);

            splash.showMessageAndProgress("ModelBuilder is ready!", 100);

            splash.setVisible(false);
            app.getFrame().setVisible(true);

            // Validate frame location and reset it if necessary.
            Dimension screenSize = UiHelper.getMultiMonitorScreenSize();
            if (app.getFrame().getX() > screenSize.width || app.getFrame().getY() > screenSize.height) {
                app.getFrame().setLocation(AppConstants.FRAME_DEFAULT_LOCATION);
            }

            app.modelLoaded();
        }
    }

}
