/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;

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
     *
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
     *
     * @param string
     * @return
     */
    public static String upperFirst(String string) {
        return new StringBuilder(string.length())
                .append(string.substring(0, 1).toUpperCase()) // upper first
                .append(string.substring(1).toLowerCase()).toString();  // lower rest
    }

    /**
     * Mixes color2 to color1.
     *
     * @param color1
     * @param color2
     * @param ratio the amount of color2.
     * @return
     */
    public static Color mixColors(Color color1, Color color2, float ratio) {
        ratio = Math.min(1f, ratio);
        ratio = Math.max(0f, ratio);

        float invRatio = 1 - ratio;
        float[] c1Comps = color1.getRGBComponents(null);
        float[] c2Comps = color2.getRGBComponents(null);

        for (int i = 0; i < c1Comps.length; i++) {
            c1Comps[i] = invRatio * c1Comps[i] + ratio * c2Comps[i];
        }

        return new Color(c1Comps[0], c1Comps[1], c1Comps[2], c1Comps[3]);
    }

    /**
     * Limits the String's length by removing characters from its center and
     * adding placeholder characters.
     *
     * @param string
     * @param maxLength
     * @return
     */
    public static String limitString(String string, int maxLength) {
        final int length = string.length();

        if (null == string || length <= maxLength) {
            return string;
        }

        String placeholder = " ... ";
        maxLength += placeholder.length();

        int tooMuch = (length - maxLength) / 2;
        String part1 = string.substring(0, length / 2 - tooMuch);
        String part2 = string.substring(length / 2 + tooMuch);
        StringBuilder sb = new StringBuilder(maxLength);
        sb.append(part1);
        sb.append(placeholder);
        sb.append(part2);

        return sb.toString();
    }

    /**
     * Sets a window's location so that the window will be centered relative to
     * another window.
     *
     * @param centerMe
     * @param window
     */
    public static void centerToWindow(Window centerMe, Window window) {
        Point wLoc = window.getLocation();
        Dimension wSize = window.getSize();
        Dimension cSize = centerMe.getSize();

        int x = wLoc.x + (wSize.width - cSize.width) / 2;
        int y = wLoc.y + wSize.height / 2 - cSize.height / 3 * 2;
        centerMe.setLocation(x, y);
    }

    public static int getExponent(int baseTwoInt) {
        int exp = 0;

        while ((1<<exp) < (Integer.MAX_VALUE ) && (1<<exp) > 0) {
            if (((baseTwoInt >> exp) & 1) == 1) {
                return exp;
            }
            exp++;
        }

        return 0;
    }

    public static boolean is32BitVM = System.getProperty("os.arch").contains("x86");

}
