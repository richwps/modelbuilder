/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
