/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.graphView;

/**
 *
 * @author dziegenh
 */
public abstract class ModelElementsChangedListener {

    public abstract void modelElementsChanged(Object element, GraphView.ELEMENT_TYPE type, GraphView.ELEMENT_CHANGE_TYPE changeType);

}
