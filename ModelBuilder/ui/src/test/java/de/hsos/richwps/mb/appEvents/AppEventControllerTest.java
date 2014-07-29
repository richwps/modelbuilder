/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appEvents;

import junit.framework.TestCase;

/**
 *
 * @author dziegenh
 */
public class AppEventControllerTest extends TestCase {

    private AppEventService instance;

    public AppEventControllerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.instance = AppEventService.getInstance();
    }

    /**
     * Test of getInstance method, of class AppEventController.
     */
    public void testGetInstance() {
        System.out.println("getInstance");
        AppEventService expResult = instance;
        AppEventService result = AppEventService.getInstance();
        assertEquals(expResult, result);
    }

    /**
     * Test of registerObserver method, of class AppEventController.
     */
    public void testRegisterObserver() {
        System.out.println("registerObserver");
        IAppEventObserver observer = createObserver();
        boolean result = instance.registerObserver(observer);
        assertTrue(result);
    }

    /**
     * Test of unregisterObserver method, of class AppEventController.
     */
    public void testUnregisterObserver() {
        System.out.println("unregisterObserver");
        IAppEventObserver observer = createObserver();
        instance.registerObserver(observer);
        boolean result = instance.unregisterObserver(observer);
        assertTrue(result);
    }

    /**
     * Test of fireAppEvent method, of class AppEventController.
     */
    public void testFireAppEvent_AppEvent() {
        System.out.println("fireAppEvent");
        AppEvent e = new AppEvent("Event message.", new String("_AppEvent Source"));
        IAppEventObserver observer = createObserver();
        instance.registerObserver(observer);
        instance.fireAppEvent(e);
    }

    /**
     * Test of fireAppEvent method, of class AppEventController.
     */
    public void testFireAppEvent_2args() {
        System.out.println("fireAppEvent");
        String message = "Event message 3.";
        Object source =  new String("_2args Source");
        String command = "test command";

        instance.registerObserver(createObserver());
        instance.addSourceCommand(source, command);
        instance.fireAppEvent(message, source);
    }
//
//    /**
//     * Test of fireAppEvent method, of class AppEventController.
//     */
//    public void testFireAppEvent_3args() {
//        System.out.println("fireAppEvent");
//        String message = "Event message 2.";
//        Object source =  new String("_3args Source");
//        String command = "test command";
//        instance.registerObserver(createObserver());
//        instance.fireAppEvent(message, source, command);
//    }



    private IAppEventObserver createObserver() {
        IAppEventObserver observer = new IAppEventObserver() {
            public void eventOccured(AppEvent e) {
                System.out.println("Event: " + e);
            }
        };

        return observer;
    }

}
