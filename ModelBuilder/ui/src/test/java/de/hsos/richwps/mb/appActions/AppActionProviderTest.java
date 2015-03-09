/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appActions;

import de.hsos.richwps.mb.app.App;
import de.hsos.richwps.mb.app.AppActionHandler;
import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.app.actions.IAppActionHandler;
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

        this.app = new App();
        this.app.setup(new String[] {""});
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
        AppAction result = instance.getAction(APP_ACTIONS.NEW_MODEL);
        assertNotNull(result);
//        assertTrue(result instanceof NewModelAction);
    }


}
