package de.hsos.richwps.mb.processProvider.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    /**
     * Returns the first key which translation matches the given value.
     *
     * @param translation
     * @return the first matching key or null.
     */
    public String getKey(String translation) {
        Set<Map.Entry<String, String>> entrySet = this.translations.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (entry.getValue().equals(translation)) {
                return entry.getKey();
            }
        }
        
        return null;
    }

}
