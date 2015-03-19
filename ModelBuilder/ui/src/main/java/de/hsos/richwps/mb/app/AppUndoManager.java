package de.hsos.richwps.mb.app;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxGraphModel.mxChildChange;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxUndoableEdit;
import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.treeView.SubTreeViewController;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyChange.UndoablePropertyChangeAction;
import de.hsos.richwps.mb.undoManager.MbUndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * Controlls the UndoManager integration and handles the ModelBuilder's undoable
 * edits.
 *
 * @author dziegenh
 */
public class AppUndoManager extends MbUndoManager {

    private GraphView graphView;
    private SubTreeViewController subTreeView;
    private PropertiesView propertiesView;
    private AppActionProvider actionProvider;

    public void setGraphView(GraphView graphView) {
        this.graphView = graphView;
    }

    public void setSubTreeView(SubTreeViewController subTreeView) {
        this.subTreeView = subTreeView;
    }

    public void setPropertiesView(PropertiesView propertiesView) {
        this.propertiesView = propertiesView;
    }

    public void setActionProvider(AppActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    public void init() {
        addChangeListener(new MbUndoManager.UndoManagerChangeListener() {
            @Override
            public void changed(UNDO_MANAGER_CHANGE change, UndoableEdit edit) {

                String undoName = AppConstants.UNDOMANAGER_CANT_UNDO;
                if (canUndo()) {
                    undoName = "Undo " + getUndoPresentationName();
                }
                AppAction undoAction = actionProvider.getAction(AppActionProvider.APP_ACTIONS.UNDO);
                undoAction.setName(undoName);
                undoAction.setEnabled(canUndo());

                String redoName = "Can't redo";
                if (canRedo()) {
                    redoName = "Redo " + getRedoPresentationName();
                }
                AppAction redoAction = actionProvider.getAction(AppActionProvider.APP_ACTIONS.REDO);
                redoAction.setName(redoName);
                redoAction.setEnabled(canRedo());

                // update other app components depending on the undone or redone edit
                AppUndoableEdit appEdit = null;
                mxUndoableEdit editAction = null;
                mxIGraphModel model = null;
                Object editCell = null;
                Object editCellParent = null;
                switch (change) {
                    case EDIT_UNDONE:
                    case EDIT_REDONE:
                        appEdit = (AppUndoableEdit) edit;

                        if (appEdit.getAction() instanceof mxUndoableEdit) {
                            editAction = (mxUndoableEdit) appEdit.getAction();

                            model = graphView.getGraph().getModel();

                            mxUndoableEdit.mxUndoableChange firstChange = editAction.getChanges().get(0);
                            if (firstChange instanceof mxChildChange) {
                                editCell = ((mxChildChange) editAction.getChanges().get(0)).getChild();
                            } else if (firstChange instanceof mxGraphModel.mxGeometryChange) {
                                editCell = ((mxGraphModel.mxGeometryChange) editAction.getChanges().get(0)).getCell();
                            }

                            editCellParent = model.getParent(editCell);

                            if (null != subTreeView) {
                                if (null == editCellParent) {
                                    subTreeView.removeNode(model.getValue(editCell));
                                } else {
                                    subTreeView.addNode(model.getValue(editCell));
                                }
                            }

                        } else if (appEdit.getAction() instanceof UndoablePropertyChangeAction) {

                            // select the property's parent object in GraphView & PropertiesView
                            UndoablePropertyChangeAction changeAction = (UndoablePropertyChangeAction) appEdit.getAction();
                            IObjectWithProperties parentObject = changeAction.getParentObject();
                            propertiesView.setObjectWithProperties(parentObject);
                            graphView.selectCellByValue(parentObject);
                        }

                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit edit) {
        return super.addEdit((AppUndoableEdit) edit);
    }

    @Override
    public String getUndoPresentationName() {
        return ((AppUndoableEdit) editToBeUndone()).getPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
        return ((AppUndoableEdit) editToBeRedone()).getPresentationName();
    }

}
