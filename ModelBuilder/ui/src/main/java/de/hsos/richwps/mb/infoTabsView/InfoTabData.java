package de.hsos.richwps.mb.infoTabsView;

/**
 *
 * @author dziegenh
 */
public class InfoTabData {

    private String id;

    private InfoTabs.TAB_STATUS status;

    private int index;

    public InfoTabData(String id, InfoTabs.TAB_STATUS status, int index) {
        this.id = id;
        this.status = status;
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InfoTabs.TAB_STATUS getStatus() {
        return status;
    }

    public void setStatus(InfoTabs.TAB_STATUS status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
