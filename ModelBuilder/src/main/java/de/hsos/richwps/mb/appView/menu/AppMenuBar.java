/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appView.menu;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.appView.IAppActionHandler;
import de.hsos.richwps.mb.appView.action.AppAbstractAction;
import de.hsos.richwps.mb.appView.action.ExitAction;
import de.hsos.richwps.mb.appView.action.LoadAction;
import de.hsos.richwps.mb.appView.action.NewAction;
import de.hsos.richwps.mb.appView.action.PreferencesAction;
import de.hsos.richwps.mb.appView.action.RedoAction;
import de.hsos.richwps.mb.appView.action.SaveAction;
import de.hsos.richwps.mb.appView.action.UndoAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author dziegenh
 */
public class AppMenuBar extends JMenuBar {

    public static enum MENUS {FILE, EDIT}
    public static enum MENU_ITEMS {
        FILE_NEW, FILE_LOAD, FILE_SAVE, FILE_SAVEAS, FILE_PREFERENCES, FILE_EXIT,
        EDIT_UNDO, EDIT_REDO, EDIT_LAYOUT
    }

    private final IAppActionHandler actionHandler;

    public AppMenuBar(IAppActionHandler actionHandler) {
        super();

        this.actionHandler = actionHandler;
        
        add(getFileMenu());
        add(getEditMenu());
    }

    public boolean isMenuItem(Object o) {
        for(MENU_ITEMS item : MENU_ITEMS.values()) {
            if(o.equals(item))
                return true;
        }

        return false;
    }

    /**
     * Return the visible caption String for the given item.
     * @param item
     * @return
     */
    public static String getMenuItemCaption(MENU_ITEMS item) {
        String itemString = item.name();
        for(String[] itemCaption : AppConstants.MENU_ITEM_CAPTIONS)
            if(itemCaption[0].equals(itemString))
                return itemCaption[1];
        return "";
    }

    /**
     * Return the visible caption String for the given item.
     * @param menu
     * @return
     */
    public static String getMenuItemCaption(MENUS menu) {
        String menuString = menu.name();
        for(String[] menuCaption : AppConstants.MENU_CAPTIONS)
            if(menuCaption[0].equals(menuString))
                return menuCaption[1];
        return "";
    }

    private JMenuItem createAndAddMenuItem(JMenu menu, final MENU_ITEMS item, AppAbstractAction action) {
        JMenuItem menuItem = new JMenuItem(getMenuItemCaption(item));
        menuItem.setAction(action);
        menu.add(menuItem);

        return menuItem;
    }

    private JMenu getFileMenu() {
        JMenu mFile = new JMenu(getMenuItemCaption(MENUS.FILE));

        createAndAddMenuItem(mFile, MENU_ITEMS.FILE_NEW, new NewAction(actionHandler));
        createAndAddMenuItem(mFile, MENU_ITEMS.FILE_LOAD, new LoadAction(actionHandler));
        createAndAddMenuItem(mFile, MENU_ITEMS.FILE_SAVE, new SaveAction(actionHandler));
        mFile.addSeparator();
        createAndAddMenuItem(mFile, MENU_ITEMS.FILE_PREFERENCES, new PreferencesAction(actionHandler));
        mFile.addSeparator();
        createAndAddMenuItem(mFile, MENU_ITEMS.FILE_EXIT, new ExitAction(actionHandler));
        
        return mFile;
    }

    private JMenu getEditMenu() {
        JMenu mEdit = new JMenu(getMenuItemCaption(MENUS.EDIT));

        createAndAddMenuItem(mEdit, MENU_ITEMS.EDIT_UNDO, new UndoAction(actionHandler));
        createAndAddMenuItem(mEdit, MENU_ITEMS.EDIT_REDO, new RedoAction(actionHandler));

        return mEdit;
    }

}
