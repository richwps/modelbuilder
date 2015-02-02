package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.ui.UiHelper;

/**
 *
 * @author dziegenh
 */
public enum WpsServerSource {

    UNKNOWN, SEMANTIC_PROXY, MANAGED_REMOTE, MODEL_FILE, MIXED;

    @Override
    public String toString() {
        return UiHelper.createStringForViews(this.name());
    }
    
   
}
