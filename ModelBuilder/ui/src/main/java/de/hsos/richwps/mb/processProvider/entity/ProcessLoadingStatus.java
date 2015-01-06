package de.hsos.richwps.mb.processProvider.entity;

/**
 *
 * @author dziegenh
 */
public enum ProcessLoadingStatus {

    NOT_LOADED, // loading has not been completed
    COMPLETE, // loading has been completed
    RESET       // loading has been completed but should be redone
}
