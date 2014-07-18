/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appEvents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton class for managing AppEvents and -Observers.
 *
 * @author dziegenh
 */
public class AppEventService {

    private HashMap<Object, String> sourceCommands;

    /**
     * Singleton instance.
     */
    private static AppEventService instance;

    /**
     * Registered AppEvent-Observers.
     */
    private final List<IAppEventObserver> observers;

    /**
     * Hidden for singleton use.
     */
    private AppEventService() {
        observers = new LinkedList<IAppEventObserver>();
    }

    private HashMap<Object, String> getSourceCommands() {
        if (null == sourceCommands) {
            sourceCommands = new HashMap<Object, String>();
        }

        return sourceCommands;
    }

    public void addSourceCommand(Object source, String command) {
        getSourceCommands().put(source, command);
    }

    /**
     * Singleton access.
     *
     * @return
     */
    public static AppEventService getInstance() {
        if (null == instance) {
            instance = new AppEventService();
        }

        return instance;
    }

    /**
     * Register an observer for AppEvents.
     *
     * @param observer
     * @return
     */
    public boolean registerObserver(IAppEventObserver observer) {
        return observers.add(observer);
    }

    /**
     * Remove an AppEvent-observer.
     *
     * @param observer
     * @return
     */
    public boolean unregisterObserver(IAppEventObserver observer) {
        return observers.remove(observer);
    }

    /**
     * Shortcut method.
     *
     * @param message
     * @param source
     * @param command
     */
    public void fireAppEvent(String message, Object source) {
        String command = getSourceCommands().get(source);
        if (null != command) {
            fireAppEvent(new AppEvent(message, source, command));
        }
    }

    /**
     * Inform observers about an AppEvent.
     *
     * @param e
     */
    public void fireAppEvent(AppEvent e) {
        for (IAppEventObserver observer : observers) {
            observer.eventOccured(e);
        }
    }

}
