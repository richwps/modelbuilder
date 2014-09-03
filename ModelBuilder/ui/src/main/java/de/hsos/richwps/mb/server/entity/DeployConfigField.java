package de.hsos.richwps.mb.server.entity;

import de.hsos.richwps.mb.ui.UiHelper;

/**
 * Field keys for deployment configurations.
 * @author dziegenh
 */
public enum DeployConfigField {

    ENDPOINT,
    IDENTIFIER,
    TITLE,
    ABSTRACT,
    VERSION,;

    @Override
    public String toString() {
        return UiHelper.createStringForViews(name());
    }
}
