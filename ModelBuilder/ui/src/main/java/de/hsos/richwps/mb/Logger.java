package de.hsos.richwps.mb;

import java.util.logging.FileHandler;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

/**
 * Simple output to System.err. Used for development issues (no need for a
 * "real" logger).
 *
 * @author dziegenh
 * @author dalcacer
 * @deprecated just for dev!!
 */
public class Logger {

    static org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(Logger.class.getName());

    /**
     * Uses Object.toString() for output to System.err. Prepends time
     * information (µs).
     *
     * @param o
     */
    public static void log(Object o) {
        String oString = (null == o) ? "null" : o.toString();
        /*String micro = (new Long(System.nanoTime())).toString();
         System.err.println(micro.substring(0, 6) + ": " + oString*/

        try {
            PatternLayout layout = new PatternLayout("%d %p (%t) [%c] - %m%n");
            /*ConsoleAppender consoleAppender = new ConsoleAppender(layout);
            log4j.addAppender(consoleAppender);*/
            FileAppender fileAppender = new FileAppender(layout, "logs/ModelBuilder.log", false);
            log4j.addAppender(fileAppender);
            // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
            log4j.setLevel(org.apache.log4j.Level.ALL);
        } catch (Exception e) {
            //noop
        }
        log4j.info(oString);
        /*log.info(oString);*/
    }

    /**
     *
     * @param source. e.g. this.getClass()
     * @param locator e.g. method()
     * @param o the log message.
     */
    public static void log(Class source, String locator, Object o) {
        String oString = (null == o) ? "null" : source.getSimpleName() + "::" + locator + "\t" + o.toString();
        Logger.log(oString);
    }
}
