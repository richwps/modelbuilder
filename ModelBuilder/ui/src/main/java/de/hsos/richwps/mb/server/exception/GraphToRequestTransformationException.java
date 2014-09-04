/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.server.exception;

/**
 *
 * @author dziegenh
 */
public class GraphToRequestTransformationException extends Exception {

    public GraphToRequestTransformationException(Object source) {
        super("Error transforming graph to request object caused by '"+source+"'");
    }



}
