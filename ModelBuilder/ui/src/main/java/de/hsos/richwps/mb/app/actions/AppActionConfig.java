package de.hsos.richwps.mb.app.actions;

/**
 * Config values for an app action.
 *
 * @author dziegenh
 */
class AppActionConfig {

    private String caption = "";

    private String iconKey = null;

    AppActionConfig(String caption, String iconKey) {
        this.caption = caption;
        this.iconKey = iconKey;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getIconKey() {
        return iconKey;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

}
