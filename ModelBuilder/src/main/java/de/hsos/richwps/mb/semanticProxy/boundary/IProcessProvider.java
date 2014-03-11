/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import java.util.Collection;

/**
 *
 * @author dziegenh
 */
public interface IProcessProvider {

    Collection<String> getAllServer();

    Collection<ProcessEntity> getServerProcesses(String server);

}
