/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.infoTabsView;

import junit.framework.TestCase;

/**
 *
 * @author dziegenh
 */
public class InfoTabsTest extends TestCase {

    public InfoTabsTest(String testName) {
        super(testName);
    }

    /**
     * Test of output method, of class InfoTabs.
     */
    public void testOutputAndGetOutput() {
        System.out.println("output");

        final int numTests = 3;

        // cancel if there's nothing to test
        if(0 >= InfoTabs.TABS.values().length)
            return;

        for(int test=0; test<numTests; test++) {
            InfoTabs.TABS tab = InfoTabs.TABS.values()[0];
            InfoTabs instance = new InfoTabs();
            String testOutput = (new Long(System.currentTimeMillis())).toString();
            instance.output(tab, testOutput);
            assert(instance.getOutput(tab).contains(testOutput));
        }
    }

    /**
     * Test of getIndex method, of class InfoTabs.
     */
    public void testGetIndex() {
        System.out.println("getIndex");

        // use last tab (index) for testing
        int testIdx = InfoTabs.TABS.values().length - 1;
        // cancel if there's nothing to test
        if(testIdx < 0) {
            return;
        }

        InfoTabs.TABS testTab = InfoTabs.TABS.values()[testIdx];
        InfoTabs instance = new InfoTabs();
        int result = instance.getIndex(testTab);
        assertEquals(testIdx, result);
    }

}
