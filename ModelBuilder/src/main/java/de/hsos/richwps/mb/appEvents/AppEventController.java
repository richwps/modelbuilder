/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appEvents;

import java.util.LinkedList;
import java.util.List;

/**
 * Singleton class for managing AppEvents and -Observers.
 * @author dziegenh
 */
public class AppEventController {

    /**
     * Singleton instance.
     */
    private static AppEventController instance;

    /**
     * Registered AppEvent-Observers.
     */
    private final List<IAppEventObserver> observers;

    /**
     * Hidden for singleton use.
     */
    private AppEventController() {
        observers = new LinkedList<IAppEventObserver>();
    }

    /**
     * Singleton access.
     * @return
     */
    public static AppEventController getInstance() {
        if (null == instance) {
            instance = new AppEventController();
        }

        return instance;
    }

    /**
     * Register an observer for AppEvents.
     * @param observer
     * @return
     */
    public boolean registerObserver(IAppEventObserver observer) {
        return observers.add(observer);
    }

    /**
     * Remove an AppEvent-observer.
     * @param observer
     * @return
     */
    public boolean unregisterObserver(IAppEventObserver observer) {
        return observers.remove(observer);
    }

    /**
     * Shortcut method.
     * @param message
     * @param source
     * @param command
     */
    public void fireAppEvent(String message, Object source, String command) {
        fireAppEvent(new AppEvent(message, source, command));
    }

    /**
     * Inform observers about an AppEvent.
     * @param e
     */
    public void fireAppEvent(AppEvent e) {
        for(IAppEventObserver observer : observers){
            observer.eventOccured(e);
        }
    }

}
