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
public class IdentifierDuplicatedException extends Exception {

    private final static String msg = "%s port identifier '%s' is used %d times";

    public IdentifierDuplicatedException(String identifier, boolean isInput, int times) {
        super(String.format(msg, (isInput ? "Input" : "Output"), identifier, times));
    }
}
