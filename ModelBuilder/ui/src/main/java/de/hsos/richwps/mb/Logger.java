package de.hsos.richwps.mb;

import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Simple output to System.err. Used for development issues (no need for a
 * "real" logger).
 *
 * @author dziegenh
 * @deprecated just for dev!!
 */
public class Logger {
    
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static FileHandler handler;

    /**
     * Uses Object.toString() for output to System.err. Prepends time
     * information (µs).
     *
     * @param o
     */
    public static void log(Object o) {
        String oString = (null == o) ? "null" : o.toString();
        String micro = (new Long(System.nanoTime())).toString();
        System.err.println(micro.substring(0, 6) + ": " + oString);
        
        /*try{
            handler = new FileHandler("modelbuilder.log", true);
        }catch(Exception ex){
        //nop.
        }
        log.info(oString);*/
    }
}
