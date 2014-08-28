/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

/**
 *
 * @author dziegenh
 */
public class UiHelper {

    /**
     * Gets the maximal screen height and the total screen width, assuming that
     * multiple monitors are aligned horizontally.
     *
     * @return
     */
    public static Dimension getMultiMonitorScreenSize() {
        Dimension screenSize = new Dimension();

        GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = gEnvironment.getScreenDevices();
        for (GraphicsDevice device : devices) {
            DisplayMode displayMode = device.getDisplayMode();
            screenSize.height = Math.max(displayMode.getHeight(), screenSize.height);
            screenSize.width += displayMode.getWidth();
        }

        return screenSize;
    }

    /**
     * Calculates the center point of the first available graphics device
     * (screen).
     *
     * @return
     */
    public static Point getFirstScreenCenter() {
        GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = gEnvironment.getScreenDevices();
        if (devices.length < 1) {
            return new Point(0, 0);
        }
        DisplayMode displayMode = devices[0].getDisplayMode();
        return new Point(displayMode.getWidth() / 2, displayMode.getHeight() / 2);

    }

    /**
     * Replaces underscores with spaces and applies upperFirst to all words.
     * @param string
     * @return
     */
    public static String createStringForViews(String string) {
        String[] splitted = string.split("_");
        String name = "";
        for (String part : splitted) {
            name += UiHelper.upperFirst(part);
            name += " ";
        }
        name = name.trim();
        return name;
    }

    /**
     * Sets the first character to upper case and the others to lower case.
     * @param string
     * @return
     */
    public static String upperFirst(String string) {
        return new StringBuilder(string.length())
                .append(string.substring(0, 1).toUpperCase()) // upper first
                .append(string.substring(1).toLowerCase()).toString();  // lower rest
    }

}
