package de.hsos.richwps.mb.undoManager;

import java.util.LinkedList;
import java.util.List;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * Undo Manager for ModelBuilder edits.
 *
 * @author dziegenh
 */
public class MbUndoManager extends UndoManager {

    public enum UNDO_MANAGER_CHANGE {

        EDIT_ADDED, EDIT_UNDONE, EDIT_REDONE, EDITS_CLEARED
    }

    public interface UndoManagerChangeListener {

        void changed(UNDO_MANAGER_CHANGE change, UndoableEdit edit);
    }

    private List<UndoManagerChangeListener> changeListeners;

    public MbUndoManager() {
        super();
    }

    public void addChangeListener(UndoManagerChangeListener listener) {
        getChangeListeners().add(listener);
    }

    public boolean removeChangeListener(UndoManagerChangeListener listener) {
        return getChangeListeners().remove(listener);
    }

    List<UndoManagerChangeListener> getChangeListeners() {
        if (null == changeListeners) {
            changeListeners = new LinkedList<>();
        }

        return changeListeners;
    }

    void notifyListeners(UNDO_MANAGER_CHANGE change, UndoableEdit edit) {
        for (UndoManagerChangeListener listener : getChangeListeners()) {
            listener.changed(change, edit);
        }
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit edit) {
        boolean result = super.addEdit(edit);
        if (result) {
            notifyListeners(UNDO_MANAGER_CHANGE.EDIT_ADDED, edit);
        }
        return result;
    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        UndoableEdit edit = editToBeUndone();
        super.undo();
        notifyListeners(UNDO_MANAGER_CHANGE.EDIT_UNDONE, edit);
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        UndoableEdit edit = editToBeRedone();
        super.redo();
        notifyListeners(UNDO_MANAGER_CHANGE.EDIT_REDONE, edit);
    }

    @Override
    public synchronized void discardAllEdits() {
        super.discardAllEdits();
        notifyListeners(UNDO_MANAGER_CHANGE.EDITS_CLEARED, null);
    }

}
