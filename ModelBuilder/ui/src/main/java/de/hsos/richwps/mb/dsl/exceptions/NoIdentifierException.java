/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.dsl.exceptions;

/**
 *
 * @author dziegenh
 */
public class NoIdentifierException extends Exception {

    private final static String msg = "At least one global %s port has no identifier";

    public NoIdentifierException(boolean isInput) {
        super(String.format(msg, (isInput ? "input" : "output") ));
    }

}
