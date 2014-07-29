/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb;

import java.awt.event.ActionEvent;

/**
 *
 * @author dziegenh
 */
public class AppActionEvent extends ActionEvent {

    private Object data;

    public AppActionEvent() {
        super(null, 0, null);
    }
    
    public AppActionEvent(Object data) {
        super(null, 0, null);
        this.data = data;
    }

    public AppActionEvent(ActionEvent e, Object data) {
        super(e.getSource(), e.getID(), e.getActionCommand());
        this.data = data;
    }

    public AppActionEvent(Object data, Object source, int id, String command) {
        super(source, id, command);
        this.data = data;
    }

    public AppActionEvent(Object data, Object source, int id, String command, int modifiers) {
        super(source, id, command, modifiers);
        this.data = data;
    }

    public AppActionEvent(Object data, Object source, int id, String command, long when, int modifiers) {
        super(source, id, command, when, modifiers);
        this.data = data;
    }

    public AppActionEvent(Object source, int id, String command, Object data) {
        super(source, id, command);
        this.data = data;
    }

    public Object getData() {
        return data;
    }



}
