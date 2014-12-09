package de.hsos.richwps.mb.processProvider.control;

import java.util.HashMap;

/**
 *
 * @author dziegenh
 */
public class KeyTranslator {

    private final HashMap<String, String> translations = new HashMap<>();

    public void addTranslation(String key, String translation) {
        this.translations.put(key, translation);
    }

    /**
     * Returns the translation for the given key or the key itself if no
     * translation exists.
     *
     * @param key
     * @return
     */
    public String getTranslation(String key) {
        if (!this.translations.containsKey(key)) {
            return key;
        }

        return this.translations.get(key);
    }

}
