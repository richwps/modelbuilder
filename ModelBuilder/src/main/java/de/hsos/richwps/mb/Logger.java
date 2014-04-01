/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb;

/**
 * Simple output to System.err.
 * Used for development issues (no need for a "real" logger).
 * @author dziegenh
 */
public class Logger {

    /**
     * Uses Object.toString() for output to System.err. Prepends time information (µs).
     * @param o
     */
    public static void log(Object o) {
        String oString = (null==o) ? "null" : o.toString();
        String micro = (new Long(System.nanoTime())).toString();
        System.err.println(micro.substring(0, 6) + ": " + oString);
    }
}
