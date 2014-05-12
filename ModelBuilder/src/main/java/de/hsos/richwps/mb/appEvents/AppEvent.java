/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appEvents;

/**
 * Model class used by the AppEvent-Service.
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

    /**
     * Shortcut constructor, sets command to null.
     * @param message
     * @param source
     */
    public AppEvent(String message, Object source) {
        this(message, source, null);
    }

    /**
     * Main constructor.
     * @param message
     * @param source
     * @param command
     */
    public AppEvent(String message, Object source, String command) {
        if (null == source) {
            throw new IllegalArgumentException("source must not be null");
        }
        if (null == message) {
            throw new IllegalArgumentException("message must not be null");
        }

        this.message = message;
        this.command = command;
        this.source = source;
    }

    /**
     * The AppEvent message.
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Internal event command.
     *
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * Source of the event.
     *
     * @return
     */
    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "AppEvent[source=" + source + ", command="+command+", message="+message+"]";
    }



}
