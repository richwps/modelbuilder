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

    boolean significant;

    // TODO dev!!!
    public AppUndoManager aum;

    public AppUndoableEdit(Object source, Object action) {
        this(source, action, "");
    }

    public AppUndoableEdit(Object source, Object action, String presentationName) {
        this.source = source;
        this.action = action;
        this.presentationName = presentationName;
        this.significant = true;
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

        significant = false;

//        if(action instanceof )

        // TODO dev!!!
        aum.notifyListeners();
    }

    @Override
    public boolean isSignificant() {
        return significant;
    }








}
