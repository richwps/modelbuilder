/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb;

import com.mxgraph.util.mxUndoableEdit;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author dziegenh
 */
public class AppUndoableEdit extends AbstractUndoableEdit {

    private String presentationName = "";

    private Object source;
    private Object action;

    public AppUndoableEdit(Object source, Object action) {
        this(source, action, "");
    }

    public AppUndoableEdit(Object source, Object action, String presentationName) {
        super();
        this.source = source;
        this.action = action;
        this.presentationName = presentationName;
    }

    public void setPresentationName(String name) {
        this.presentationName = name;
    }

    @Override
    public String getPresentationName() {
        return presentationName;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        if(action instanceof mxUndoableEdit) {
            mxUndoableEdit edit = (mxUndoableEdit) action;
            edit.undo();
        }

        // TODO ...
//        if(action instanceof ...)

    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

        if(action instanceof mxUndoableEdit) {
            mxUndoableEdit edit = (mxUndoableEdit) action;
            edit.redo();
        }

        // TODO ...
//        if(action instanceof ...)
    }

    @Override
    public void die() {
        source = null;
        action = null;
        super.die();
    }


}
