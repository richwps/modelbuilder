package de.hsos.richwps.mb.properties;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class PropertyHelper {

    public static List<String> getPropertyNames(IObjectWithProperties object) {
        List<String> names = new LinkedList<>();
        
        Collection<? extends IObjectWithProperties> properties = object.getProperties();
        for (IObjectWithProperties property : properties) {
            String propertyName = property.getPropertiesObjectName();
            if (null != propertyName && !propertyName.isEmpty()) {
                names.add(propertyName);
            }
        }
        
        return names;
    }

}
