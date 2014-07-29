/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.semanticProxy.boundary;

/**
 *
 * @author dziegenh
 */
class UnsupportedWpsDatatypeException extends Exception {

    public UnsupportedWpsDatatypeException(int dataType) {
        super("InAndOutputForm datatype '"+dataType+"' is not supported");
    }

}
