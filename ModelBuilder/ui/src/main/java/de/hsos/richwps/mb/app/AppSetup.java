package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.actions.AppAbstractAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.AppFrame;
import de.hsos.richwps.mb.app.view.AppSplashScreen;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.appEvents.IAppEventObserver;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphSetup;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 * Creates the app components and establishes connections between them.
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

        // Load colors
        String[] bgColorKeys = new String[]{
            "CheckBoxMenuItem.selectionBackground",
            "ComboBox.selectionBackground",
            "EditorPane.selectionBackground",
            "List.selectionBackground",
            "Menu.selectionBackground",
            "MenuItem.selectionBackground",
            "RadioButtonMenuItem.selectionBackground",
            "PasswordField.selectionBackground",
            "ProgressBar.foreground",
            "ProgressBar.selectionBackground",
            "Table.selectionBackground",
            "TextArea.selectionBackground",
            "TextField.selectionBackground",
            "TextPane.selectionBackground",
            "Tree.selectionBackground"
        };
        for (String key : bgColorKeys) {
            UIManager.put(key, AppConstants.SELECTION_BG_COLOR);
        }
        String[] fgColorKeys = new String[]{
            "CheckBoxMenuItem.selectionForeground",
            "ComboBox.selectionForeground",
            "EditorPane.selectionForeground",
            "List.selectionForeground",
            "Menu.selectionForeground",
            "MenuItem.selectionForeground",
            "RadioButtonMenuItem.selectionForeground",
            "PasswordField.selectionForeground",
            "ProgressBar.background",
            "ProgressBar.selectionForeground",
            "Table.selectionForeground",
            "TextArea.selectionForeground",
            "TextField.selectionForeground",
            "TextPane.selectionForeground",
            "Tree.selectionForeground"
        };
        for (String key : fgColorKeys) {
            UIManager.put(key, AppConstants.SELECTION_FG_COLOR);
        }

        // Load icons etc. into UIManager
        loadIcons();

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

        // Configure MB components before initialisation
        GraphSetup.localInputBgColor = AppConstants.INPUT_PORT_COLOR_STRING;
        GraphSetup.localOutputBgColor = AppConstants.OUTPUT_PORT_COLOR_STRING;

        // Load last used filename
        String lastFilename = AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.MODEL_S_LASTFILE.name(), "");
        File lastFile = new File(lastFilename);
        AppAbstractAction recentFileAction = app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.OPEN_RECENT_FILE);
        recentFileAction.setEnabled(lastFile.exists());
        if (lastFile.exists()) {
            recentFileAction.setName(lastFilename);
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
        UIManager.put("ToolTip.background", AppConstants.TOOLTIP_BG_COLOR);

        ProcessPort.TOOLTIP_STYLE_INPUT = AppConstants.TOOLTIP_CSS_FOR_INPUTS;
        ProcessPort.TOOLTIP_STYLE_OUTPUT = AppConstants.TOOLTIP_CSS_FOR_OUTPUTS;

        ProcessEntity.toolTipCssForMainContainer = AppConstants.TOOLTIP_CSS_FOR_MAIN_CONTAINER;

        splash.showMessageAndProgress("Initialising user interactions", 45);

        app.initDragAndDrop();
        app.getPreferencesDialog().init();
        app.getGraphView().init();

        splash.showMessageAndProgress("Requesting processes", 60);

        // connect to SP and fill tree with services etc. received from SP
        app.fillMainTree();
        AppEventService.getInstance().addSourceCommand(AppConstants.INFOTAB_ID_SERVER, AppConstants.INFOTAB_ID_SERVER);

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

    /**
     * Loads icons into UIManager.
     */
    public static void loadIcons() {
        String iconDir = AppConstants.RES_ICONS_DIR + File.separator;

        // App Icon
        UIManager.put(AppConstants.ICON_MBLOGO_KEY, new ImageIcon(iconDir + "mb_logo.png", "mb logo image"));

        // File Menu icons
        UIManager.put(AppConstants.ICON_NEW_KEY, new ImageIcon(iconDir + "document-new-6.png", "new icon"));
        UIManager.put(AppConstants.ICON_OPEN_KEY, new ImageIcon(iconDir + "document-open-2.png", "open icon"));
        UIManager.put(AppConstants.ICON_SAVE_KEY, new ImageIcon(iconDir + "document-save-5.png", "save icon"));
        UIManager.put(AppConstants.ICON_SAVEAS_KEY, new ImageIcon(iconDir + "document-save-as-4.png", "save as icon"));
        UIManager.put(AppConstants.ICON_PREFERENCES_KEY, new ImageIcon(iconDir + "system-settings.png", "prefs icon"));
        UIManager.put(AppConstants.ICON_EXIT_KEY, new ImageIcon(iconDir + "dialog-close-2.png", "exit icon"));

        // Edit Menu Icons
        UIManager.put(AppConstants.ICON_UNDO_KEY, new ImageIcon(iconDir + "arrow-undo.png", "undo icon"));
        UIManager.put(AppConstants.ICON_REDO_KEY, new ImageIcon(iconDir + "arrow-redo.png", "redo icon"));
        UIManager.put(AppConstants.ICON_LAYOUT_KEY, new ImageIcon(iconDir + "zoom-fit-best-4.png", "layout icon"));

        // Tools Menu Icons


        // Help Menu Icons
        UIManager.put(AppConstants.ICON_ABOUT_KEY, new ImageIcon(iconDir + "help-about-3.png", "about icon"));

        // (unsorted Icons)
        UIManager.put(AppConstants.ICON_DEPLOY_KEY, new ImageIcon(iconDir + "server-add.png", "deploy icon"));
        UIManager.put(AppConstants.ICON_UNDEPLOY_KEY, new ImageIcon(iconDir + "server-delete.png", "undeploy icon"));
        UIManager.put(AppConstants.ICON_EXECUTE_ANY_KEY, new ImageIcon(iconDir + "arrow-right-3.png", "execute icon"));
        UIManager.put(AppConstants.ICON_EXECUTE_KEY, new ImageIcon(iconDir + "server-go.png", "execute icon"));
        UIManager.put(AppConstants.ICON_PROFILE_KEY, new ImageIcon(iconDir + "server-chart.png", "profile icon"));
        UIManager.put(AppConstants.ICON_TEST_KEY, new ImageIcon(iconDir + "report-magnify.png", "debug icon"));
        
        UIManager.put(AppConstants.ICON_INFO_KEY, new ImageIcon(iconDir + "dialog-information-4.png", "info icon"));
        UIManager.put(AppConstants.ICON_RELOAD_KEY, new ImageIcon(iconDir + "database-refresh.png", "reload icon"));
        UIManager.put(AppConstants.ICON_REFRESH_KEY, new ImageIcon(iconDir + "view-refresh-4.png", "refresh icon"));
        UIManager.put(AppConstants.ICON_PROCESS_KEY, new ImageIcon(iconDir + "process.png", "process icon"));
        UIManager.put(AppConstants.ICON_EDIT_KEY, new ImageIcon(iconDir + "edit-3.png", "edit icon"));
        UIManager.put(AppConstants.ICON_ADD_KEY, new ImageIcon(iconDir + "list-add-6.png", "add icon"));
        UIManager.put(AppConstants.ICON_DELETE_KEY, new ImageIcon(iconDir + "edit-delete-7.png", "delete icon"));

        UIManager.put(AppConstants.ICON_LOADING_STATUS_KEY, new ImageIcon(iconDir + "ajax-loader.gif", "loading"));
    }

}
