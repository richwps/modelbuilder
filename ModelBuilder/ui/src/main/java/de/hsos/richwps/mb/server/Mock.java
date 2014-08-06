/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.server;

/**
 *
 * @author dziegenh
 */
public class Mock {

    private static Mock instance;
    
    public static Mock getInstance() {
        if(null == instance) {
            instance = new Mock();
        }
        
        return instance;
    }

    // hide
    private Mock() {
    }


}
