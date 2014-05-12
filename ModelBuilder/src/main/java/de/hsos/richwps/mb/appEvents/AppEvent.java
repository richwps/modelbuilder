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

    /**
     * Source class/component which created the event.
     */
    private Object source;

    /**
     * Command value for internal use - for example to specify the message type.
     */
    private String command;

    /**
     * The event's payload message.
     */
    private String message;

    public AppEvent(String message, String command, Object source) {
        if (null == source) {
            throw new IllegalArgumentException("source must not be null");
        }

        this.message = message;
        this.command = command;
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public String getCommand() {
        return command;
    }

    public Object getSource() {
        return source;
    }

}
