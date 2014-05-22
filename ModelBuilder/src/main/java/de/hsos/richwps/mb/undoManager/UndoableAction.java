/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.undoManager;

/**
 *
 * @author dziegenh
 */
public class UndoableAction {

    /**
     * The source object that can undo the action.
     */
    private Object source;

    /**
     * The action which can be undone.
     */
    private Object action;

    /**
     * A readable name/description for the GUI.
     */
    private String name;

    /**
     *
     * @param source The source object that can undo the action.
     * @param action The action which can be undone.
     * @param name A readable name/description for the GUI.
     */
    public UndoableAction(Object source, Object action, String name) {
        this.source = source;
        this.action = action;
        this.name = name;
    }

    public Object getSource() {
        return source;
    }

    public Object getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


}
