package de.hsos.richwps.mb.graphView;

/**
 * Can be implemented to be notified about model changes (eg adding/removing
 * cells).
 *
 * @author dziegenh
 */
public abstract class ModelElementsChangedListener {

    public abstract void modelElementsChanged(Object element, GraphView.ELEMENT_TYPE type, GraphView.ELEMENT_CHANGE_TYPE changeType);

}
