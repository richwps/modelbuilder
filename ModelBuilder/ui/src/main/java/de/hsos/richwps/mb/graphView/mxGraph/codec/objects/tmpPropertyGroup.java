package de.hsos.richwps.mb.graphView.mxGraph.codec.objects;

import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author dziegenh
 */
public class tmpPropertyGroup implements IObjectWithProperties {

    public tmpPropertyGroup() {
    }

    public PropertyGroup[] properties;

    @Override
    public boolean isTransient() {
        return false;
    }
    
    public void setProperties(PropertyGroup[] propertyGroups) {
        this.properties = propertyGroups;
    }

    @Override
    public String getPropertiesObjectName() {
        // name is not necessary
        return "";
    }

    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        return Arrays.asList(properties);
    }

    @Override
    public void setProperty(String propertyName, IObjectWithProperties property) {
        if (property instanceof PropertyGroup) {

            int idx = 0;
            if (null == properties) {
                properties = new PropertyGroup[1];
                
            } else {
                PropertyGroup[] newArray = new PropertyGroup[properties.length + 1];
                System.arraycopy(properties, 0, newArray, 0, properties.length);
                idx = properties.length;
                properties = newArray;
            }
            
            properties[idx] = (PropertyGroup) property;
        }
    }

    @Override
    public void setPropertiesObjectName(String name) {
        // ignore, name is not necessary
    }

}
