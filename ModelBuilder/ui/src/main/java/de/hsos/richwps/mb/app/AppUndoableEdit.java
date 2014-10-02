package de.hsos.richwps.mb.app;

import com.mxgraph.util.mxUndoableEdit;
import de.hsos.richwps.mb.propertiesView.propertyChange.UndoablePropertyChangeAction;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 * Edits which can be undone/redone via the AppUndoManager.
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

        if (action instanceof mxUndoableEdit) {
            mxUndoableEdit edit = (mxUndoableEdit) action;
            edit.undo();

        } else if (action instanceof UndoablePropertyChangeAction) {
            UndoablePropertyChangeAction propertyAction = (UndoablePropertyChangeAction) action;
            propertyAction.undo();
        }

    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

        if (action instanceof mxUndoableEdit) {
            mxUndoableEdit edit = (mxUndoableEdit) action;
            edit.redo();

        } else if (action instanceof UndoablePropertyChangeAction) {
            UndoablePropertyChangeAction propertyAction = (UndoablePropertyChangeAction) action;
            propertyAction.redo();
        }

    }

    public Object getAction() {
        return action;
    }

    @Override
    public void die() {
        source = null;
        action = null;
        super.die();
    }

}
