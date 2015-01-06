package de.hsos.richwps.mb.processProvider.entity;

import de.hsos.richwps.mb.ui.UiHelper;
import java.util.Objects;

/**
 *
 * @author dziegenh
 */
public class ProcessKey {

    private String server;
    private String identifer;

    public ProcessKey() {
    }

    public ProcessKey(String server, String identifer) {
        this.server = server;
        this.identifer = identifer;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getIdentifer() {
        return identifer;
    }

    public void setIdentifer(String identifer) {
        this.identifer = identifer;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof ProcessKey)) {
            return false;
        }

        ProcessKey other = (ProcessKey) obj;

        boolean serverEqual = UiHelper.equalOrBothNull(this.getServer(), other.getServer());
        boolean idEqual = UiHelper.equalOrBothNull(this.getIdentifer(), other.getIdentifer());
        
        return serverEqual && idEqual;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 71 * hash + Objects.hashCode(UiHelper.avoidNull(this.server));
        hash = 17 * hash + Objects.hashCode(UiHelper.avoidNull(this.identifer));

        return hash;
    }

}
