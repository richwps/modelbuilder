/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appActions;

import de.hsos.richwps.mb.App;
import de.hsos.richwps.mb.AppActionHandler;
import de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS;
import junit.framework.TestCase;

/**
 *
 * @author dziegenh
 */
public class AppActionProviderTest extends TestCase {
    private App app;
    private IAppActionHandler appActionHandler;
    private AppActionProvider instance;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.app = new App(new String[] {""});
        this.appActionHandler = new AppActionHandler(app);
        this.instance = new AppActionProvider(appActionHandler);
    }



    public AppActionProviderTest(String testName) {
        super(testName);
    }


    /**
     * Test of getAction method, of class AppActionProvider.
     */
    public void testGetAction() {
        System.out.println("getAction");
        AppAbstractAction result = instance.getAction(APP_ACTIONS.NEW_MODEL);
        assertNotNull(result);
        assertTrue(result instanceof NewModelAction);
    }

    /**
     * Test of getActionClassName method, of class AppActionProvider.
     */
    public void testGetActionClassName() {
        System.out.println("getActionClassName");
        String expResult = "NewModelAction";
        String result = instance.getActionClassName(APP_ACTIONS.NEW_MODEL);
        assertEquals(expResult, result);
    }

}
