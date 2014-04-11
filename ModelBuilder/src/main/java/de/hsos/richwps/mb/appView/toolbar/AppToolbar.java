/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appView.toolbar;

import de.hsos.richwps.mb.appView.IAppActionHandler;
import de.hsos.richwps.mb.appView.action.LayoutAction;
import de.hsos.richwps.mb.appView.action.LoadAction;
import de.hsos.richwps.mb.appView.action.NewAction;
import de.hsos.richwps.mb.appView.action.PreferencesAction;
import de.hsos.richwps.mb.appView.action.RedoAction;
import de.hsos.richwps.mb.appView.action.SaveAction;
import de.hsos.richwps.mb.appView.action.UndoAction;
import javax.swing.JToolBar;

/**
 *
 * @author dziegenh
 */
public class AppToolbar extends JToolBar {

//    private IAppActionHandler actionHandler;

    public AppToolbar(IAppActionHandler actionHandler) {
        setFloatable(false);

        add(new NewAction(actionHandler));
        add(new LoadAction(actionHandler));
        add(new SaveAction(actionHandler));

        addSeparator();

        add(new PreferencesAction(actionHandler));
//        add(new ExitAction(actionHandler));

        addSeparator();
        
        add(new UndoAction(actionHandler));
        add(new RedoAction(actionHandler));
        
        addSeparator();

        add(new LayoutAction(actionHandler));

    }


    
}
