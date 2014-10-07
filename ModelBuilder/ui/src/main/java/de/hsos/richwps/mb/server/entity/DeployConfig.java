package de.hsos.richwps.mb.server.entity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Holds a deployment configuration (endpoint, OWS description etc).
 *
 * @author dziegenh
 */
public class DeployConfig implements Serializable {

    private HashMap<DeployConfigField, String> fields;

    public DeployConfig() {
        fields = new HashMap<>(DeployConfigField.values().length);

        for (DeployConfigField field : DeployConfigField.values()) {
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

    public DeployConfig clone() {
        DeployConfig clone = new DeployConfig();
        for (DeployConfigField field : DeployConfigField.values()) {
            clone.setValue(field, getValue(field));
        }

        return clone;
    }

}
