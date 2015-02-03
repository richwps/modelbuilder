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
     * Commands contained in this list will not fire events.
     */
    private LinkedList<String> disabledCommands;

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
        observers = new LinkedList<>();
        disabledCommands = new LinkedList<>();
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
     */
    public void fireAppEvent(String message, Object source) {
        String command = getSourceCommands().get(source);
        if (null != command) {
            fireAppEvent(new AppEvent(message, source, command));
        }
    }

    /**
     * Shortcut method.
     *
     * @param message
     * @param source
     * @param priority
     */
    public void fireAppEvent(String message, Object source, AppEvent.PRIORITY priority) {
        String command = getSourceCommands().get(source);
        if (null != command) {
            fireAppEvent(new AppEvent(message, source, command, priority));
        }
    }

    /**
     * Inform observers about an AppEvent.
     *
     * @param e
     */
    public void fireAppEvent(AppEvent e) {
        if (!isCommandEnabled(e.getCommand())) {
            return;
        }

        for (IAppEventObserver observer : observers) {
            observer.eventOccured(e);
        }
    }

    public boolean isCommandEnabled(String command) {
        return !this.disabledCommands.contains(command);
    }

    /**
     * Only enabled commands can fire app events. Initially, commands are
     * enabled.
     *
     * @param command
     * @param enabled
     */
    public void setCommandEnabled(String command, boolean enabled) {

        if (enabled) {

            if (!this.disabledCommands.contains(command)) {
                this.disabledCommands.add(command);
            }

        } else {
            this.disabledCommands.remove(command);
        }
    }
}
