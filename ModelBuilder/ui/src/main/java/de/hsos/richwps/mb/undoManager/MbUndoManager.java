package de.hsos.richwps.mb.undoManager;


import java.util.LinkedList;
import java.util.List;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;


/**
 *
 * @author dziegenh
 */
public class MbUndoManager extends UndoManager {

    public interface UndoManagerChangeListener {
        void changed();
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
        if(null == changeListeners) {
            changeListeners = new LinkedList<>();
        }

        return changeListeners;
    }

    void notifyListeners() {
        for(UndoManagerChangeListener listener : getChangeListeners()) {
            listener.changed();
        }
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit edit) {
        boolean result = super.addEdit(edit);
        notifyListeners();
        return result;
    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        super.undo();
        notifyListeners();
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        super.redo();
        notifyListeners();
    }

    @Override
    public synchronized void discardAllEdits() {
        super.discardAllEdits();
        notifyListeners();
    }

}
