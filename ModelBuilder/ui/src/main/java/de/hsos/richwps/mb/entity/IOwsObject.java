/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.entity;

/**
 *
 * @author dziegenh
 */
public interface IOwsObject {

    public String getOwsIdentifier();

    public String getOwsTitle();

    public String getOwsAbstract();

    public void setOwsIdentifier(String owsIdentifier);

    public void setOwsTitle(String owsTitle);

    public void setOwsAbstract(String owsAbstract);
}
