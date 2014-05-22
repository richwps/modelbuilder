/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.undoManager;

import junit.framework.TestCase;

/**
 *
 * @author dziegenh
 */
public class UndoManagerTest extends TestCase {
    private UndoManager instance;
    private int actionId;

    public UndoManagerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        instance = new UndoManager();
    }

    private UndoableAction getNextAction() {
        return new UndoableAction(this, null, "action "+ (actionId++));
    }

    /**
     * Test of addAction method, of class UndoManager.
     */
    public void testAddAction() {
        System.out.println("addAction");

        instance.addAction(getNextAction());
    }

    /**
     * Test of canUndo method, of class UndoManager.
     */
    public void testCanUndo() {
        System.out.println("canUndo");

        // test empty manager.
        boolean result = instance.canUndo();
        assertEquals(false, result);

        // test filled manager
        instance.addAction(getNextAction());
        result = instance.canUndo();
        assertEquals(true, result);
    }

    /**
     * Test of undo method, of class UndoManager.
     */
    public void testUndo() {
        System.out.println("undo");
        UndoableAction expResult = getNextAction();
        instance.addAction(expResult);

        UndoableAction result = instance.undo();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentUndoName method, of class UndoManager.
     */
    public void testGetCurrentUndoName() {
        System.out.println("getCurrentUndoName");
        
        UndoableAction action = getNextAction();
        String expResult = action.getName();

        instance.addAction(action);
        String result = instance.getCurrentUndoName();

        assertEquals(expResult, result);
    }

}
