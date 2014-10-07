package de.hsos.richwps.mb.entity;

/**
 * Getters and setters for basic attributes of an ows object (eg processes and
 * ports).
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
