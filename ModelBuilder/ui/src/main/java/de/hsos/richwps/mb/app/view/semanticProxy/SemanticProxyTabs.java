package de.hsos.richwps.mb.app.view.semanticProxy;

import java.util.HashMap;
import javax.swing.JTabbedPane;
import org.apache.commons.lang.Validate;

/**
 *
 * @author dziegenh
 */
class SemanticProxyTabs extends JTabbedPane {

    HashMap<String, SpSearchresultTab> searchResultTabs;
    private final SemanticProxyInteractionComponents interactionComponents;

    
    
    public SemanticProxyTabs(SemanticProxyInteractionComponents components) {
        this.interactionComponents = components;
        
        this.searchResultTabs = new HashMap<>();
    }
    
    /**
     * 
     * @param query
     * @return ID of the tab
     */
    String addTab(String query) {
        Validate.notNull(query);
        Validate.notEmpty(query);
        
        query = query.trim();
        
        if(!searchResultTabs.containsKey(query)) {
            SpSearchresultTab tab = new SpSearchresultTab(query, interactionComponents);
            searchResultTabs.put(query, tab);
            
            addTab(query, tab);
            setSelectedComponent(tab);
            
            tab.search();
        }
        
        return query;
    }
    
    SpSearchresultTab getTab(String id) {
        return searchResultTabs.get(id);
    }
    
    void removeTab(String id) {
        searchResultTabs.remove(id);
    }
    
    
}
