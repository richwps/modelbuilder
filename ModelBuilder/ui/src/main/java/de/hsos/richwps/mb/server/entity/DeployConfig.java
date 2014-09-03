/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.server.entity;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author dziegenh
 */
public class DeployConfig implements Serializable {

    private HashMap<DeployConfigField, String> fields;

    public DeployConfig() {
        fields = new HashMap<>(DeployConfigField.values().length);

        for(DeployConfigField field : DeployConfigField.values()) {
            fields.put(field, "");
        }
    }

    public void setValue(DeployConfigField field, String value) {
        fields.put(field, value);
    }

    public String getValue(DeployConfigField field) {
        return fields.get(field);
    }

    @Override
    public String toString() {
        // TODO return something that identifies the configuration
        return fields.get(DeployConfigField.ENDPOINT);
    }

}
