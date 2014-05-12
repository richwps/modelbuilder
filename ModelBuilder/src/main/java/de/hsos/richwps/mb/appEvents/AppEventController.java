/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appEvents;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class AppEventController {

    private List<IAppEventObserver> observers;

    public AppEventController() {
        observers = new LinkedList<IAppEventObserver>();
    }

    public boolean registerObserver(IAppEventObserver observer) {
        return observers.add(observer);
    }

    public boolean unregisterObserver(IAppEventObserver observer) {
        return observers.remove(observer);
    }

    public void fireAppEvent(AppEvent e) {
        // TODO inform all observers
    }

    public void fireAppEvent(String message, Object source) {
        fireAppEvent(new AppEvent(message, source));
    }

}
