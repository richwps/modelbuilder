/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appEvents;

/**
 *
 * @author dziegenh
 */
public class AppEvent {

    private Object source;
    private String message;

    public AppEvent(String message, Object source) {
        if (null == source) {
            throw new IllegalArgumentException("source must not be null");
        }

        this.message = message;
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public Object getSource() {
        return source;
    }

}
