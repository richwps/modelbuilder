/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appView.menu;

import de.hsos.richwps.mb.App;
import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.appActions.AppAbstractAction;
import de.hsos.richwps.mb.appActions.AppActionProvider;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author dziegenh
 */
public class AppMenuBar extends JMenuBar {

    public static enum MENUS {

        FILE, EDIT
    }

    private final AppActionProvider actionProvider;

    public AppMenuBar(AppActionProvider actionProvider) {
        super();

        this.actionProvider = actionProvider;

        add(getFileMenu());
        add(getEditMenu());
    }

    /**
     * Return the visible caption String for the given item.
     *
     * @param item
     * @return
     */
    @Deprecated
    public static String getMenuItemCaption(AppActionProvider.APP_ACTIONS item) {
        return App.getMenuItemCaption(item);
    }

    /**
     * Return the visible caption String for the given item.
     *
     * @param menu
     * @return
     */
    public static String getMenuItemCaption(MENUS menu) {
        String menuString = menu.name();
        for (String[] menuCaption : AppConstants.MENU_CAPTIONS) {
            if (menuCaption[0].equals(menuString)) {
                return menuCaption[1];
            }
        }
        return "";
    }

    private JMenuItem createAndAddMenuItem(JMenu menu, final AppActionProvider.APP_ACTIONS item, AppAbstractAction action) {
        JMenuItem menuItem = new JMenuItem(getMenuItemCaption(item));
        menuItem.setAction(action);
        menu.add(menuItem);

        return menuItem;
    }


    /**
     * Creates a menu item for the given {@link AppActionProvider.APP_ACTION} using the {@link AppActionProvider}.
     * @param menu
     * @param item
     * @return
     */
    private JMenuItem createAndAddMenuItem(JMenu menu, final AppActionProvider.APP_ACTIONS item) {
        return createAndAddMenuItem(menu, item, actionProvider.getAction(item));
    }

    private JMenu getFileMenu() {
        JMenu mFile = new JMenu(getMenuItemCaption(MENUS.FILE));

        createAndAddMenuItem(mFile, AppActionProvider.APP_ACTIONS.NEW_MODEL);
        createAndAddMenuItem(mFile, AppActionProvider.APP_ACTIONS.LOAD_MODEL);
        createAndAddMenuItem(mFile, AppActionProvider.APP_ACTIONS.SAVE_MODEL);
        createAndAddMenuItem(mFile, AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS);
        mFile.addSeparator();
        createAndAddMenuItem(mFile, AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES);
        mFile.addSeparator();
        createAndAddMenuItem(mFile, AppActionProvider.APP_ACTIONS.EXIT_APP);

        return mFile;
    }

    private JMenu getEditMenu() {
        JMenu mEdit = new JMenu(getMenuItemCaption(MENUS.EDIT));

        createAndAddMenuItem(mEdit, AppActionProvider.APP_ACTIONS.UNDO);
        createAndAddMenuItem(mEdit, AppActionProvider.APP_ACTIONS.REDO);
        mEdit.addSeparator();
        createAndAddMenuItem(mEdit, AppActionProvider.APP_ACTIONS.DO_LAYOUT);

        return mEdit;
    }

}
