package de.hsos.richwps.mb.appEvents;

import jdk.nashorn.internal.parser.TokenType;

/**
 * Entity the AppEvent-Service.
 *
 * @author dziegenh
 */
public class AppEvent {

    public enum PRIORITY {

        DEFAULT, URGENT
    }

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
     * The event's priority.
     */
    private PRIORITY priority;

    /**
     * Shortcut constructor, sets command to null.
     *
     * @param message
     * @param source
     */
    public AppEvent(String message, Object source) {
        this(message, source, null);
    }

    /**
     * Main constructor.
     *
     * @param message
     * @param source
     * @param command
     */
    public AppEvent(String message, Object source, String command) {
        this(message, source, command, PRIORITY.DEFAULT);
    }

    public AppEvent(String message, Object source, String command, PRIORITY priority) {
        if (null == source) {
            throw new IllegalArgumentException("source must not be null");
        }
        if (null == message) {
            throw new IllegalArgumentException("message must not be null");
        }

        this.message = message;
        this.command = command;
        this.source = source;
        this.priority = priority;
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
        return "AppEvent[source=" + source + ", command=" + command + ", priority=" + priority.name() + ", message=" + message + "]";
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPriority(PRIORITY priority) {
        this.priority = priority;
    }

    public PRIORITY getPriority() {
        return priority;
    }

}
