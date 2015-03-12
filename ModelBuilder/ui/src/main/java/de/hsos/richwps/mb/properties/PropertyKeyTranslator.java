package de.hsos.richwps.mb.properties;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.ui.UiHelper;
import java.util.HashMap;
import org.apache.commons.lang.Validate;

/**
 * Handles the translation of property keys to readable UI captions.
 *
 * @author dziegenh
 */
public class PropertyKeyTranslator {

    /**
     * Key-Translation pairs.
     */
    private final HashMap<String, String> translations;

    public PropertyKeyTranslator() {
        this.translations = new HashMap<>();
    }

    /**
     * Adds or updates a key-to-caption translation.
     *
     * @param propertyKey
     * @param translation
     */
    public void addTranslation(String propertyKey, String translation) {
        Validate.notNull(propertyKey);

        this.translations.put(propertyKey, translation);
    }

    /**
     * Gets a property key translation.
     *
     * @param propertyKey the key which should be translated.
     * @return the translation if it exists or the key (modified).
     */
    public String translate(String propertyKey) {
        String translated = this.translations.get(propertyKey);

        if (null == translated) {
            Logger.log("No translation for key '" + propertyKey + "' !");
            translated = UiHelper.createStringForViews(propertyKey);
        }

        return translated;
    }

}
