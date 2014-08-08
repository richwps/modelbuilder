package de.hsos.richwps.mb.execView.dialog.components.renderer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;

/**
 *
 * @author dalcacer
 */
public class GTVectorDataRenderer extends javax.swing.JPanel {

    private SimpleFeatureCollection featurecollection;
    private MapContent content;

// add map layers here
    /**
     * Creates new form FeatureCollectionRenderer
     */
    public GTVectorDataRenderer() {
        initComponents();
    }

    public GTVectorDataRenderer(SimpleFeatureCollection featurecollection) {
        initComponents();
    }

    /**
     * Creates new form FeatureCollectionRenderer
     */
    public GTVectorDataRenderer(String reference) {
        initComponents();
        content = new MapContent();

        java.net.URL referencehttp = null;
        try {
            referencehttp = new URL(reference);
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.util.Map map = new HashMap();
        map.put("url", referencehttp);
        map.put("DriverName", "GeoJSON");
        try {
            DataStore dataStore = DataStoreFinder.getDataStore(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*FeatureSource source = new FeatureSource
         Layer l = new FeatureLayer() ;
         
         content.addLayer(new Layer())
         // Show the Map to the user
         JMapFrame.showMap( content );*/
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(700, 300));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Soon!");
        add(jLabel1, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
