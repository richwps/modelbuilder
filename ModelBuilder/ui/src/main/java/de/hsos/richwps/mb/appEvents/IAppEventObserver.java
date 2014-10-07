package de.hsos.richwps.mb.appEvents;

/**
 * Observer interface to be implemented by AppEvent-Observers.
 * @author dziegenh
 */
public interface IAppEventObserver {

    /**
     * Informs the observer about a new event occurance.
     * @param e
     */
    public void eventOccured(AppEvent e);

}
