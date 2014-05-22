/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.undoManager;

import java.util.Stack;

/**
 *
 * @author dziegenh
 */
public class UndoManager {

    private final Stack<UndoableAction> undoableActions;

    public UndoManager() {
        this.undoableActions = new Stack<UndoableAction>();
    }

    public void addAction(UndoableAction action) {
        undoableActions.push(action);
    }

    public boolean canUndo() {
        return !undoableActions.isEmpty();
    }

    public UndoableAction undo() {
        return undoableActions.pop();
    }

    public String getCurrentUndoName() {
        if(undoableActions.isEmpty())
            return "";

        return undoableActions.lastElement().getName();
    }
}
