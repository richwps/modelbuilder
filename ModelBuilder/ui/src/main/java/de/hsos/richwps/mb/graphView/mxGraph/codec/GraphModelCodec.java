package de.hsos.richwps.mb.graphView.mxGraph.codec;

import de.hsos.richwps.mb.graphView.mxGraph.codec.objects.tmpPropertyGroup;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxModelCodec;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Encodes additional GraphModel attributes (e.g. the name).
 *
 * @author dziegenh
 */
public class GraphModelCodec extends mxModelCodec {

    private final String propertyGroupsElement = "tmpPropertyGroup";
    private final String OLD_IDENTIFIER_KEY = "Identifier";

    public GraphModelCodec() {
        super();
    }

    public GraphModelCodec(Object o) {
        super(o);
    }

    public GraphModelCodec(Object template, String[] exclude, String[] idrefs,
            Map<String, String> mapping) {
        super(template, exclude, idrefs, mapping);
    }

    @Override
    public Node beforeDecode(mxCodec dec, Node node, Object into) {

        CellCodec.reset();
        ProcessPortCodec.reset();

        if (node instanceof Element) {
            Element elt = (Element) node;

            NodeList pGroupsElements = elt.getElementsByTagName(this.propertyGroupsElement);

            if (null != pGroupsElements && pGroupsElements.getLength() > 0) {
                Node pGroups = pGroupsElements.item(0);

                if (null != into && (into instanceof GraphModel)) {
                    tmpPropertyGroup tmpGroup = new tmpPropertyGroup();
                    dec.decode(pGroups, tmpGroup);

                    PropertyGroup[] convertedGroup = new PropertyGroup[tmpGroup.properties.length];
                    int curGrp = 0;

                    // replace old identifier key
                    Iterator<? extends IObjectWithProperties> iterator = tmpGroup.getProperties().iterator();
                    if (iterator.hasNext()) {
                        IObjectWithProperties next = iterator.next();

                        if (next instanceof PropertyGroup) {
                            Collection properties = ((PropertyGroup) next).getProperties();

                            PropertyGroup nextGroup = new PropertyGroup(((PropertyGroup) next).getPropertiesObjectName());

                            for (Object aProperty : properties) {
                                IObjectWithProperties aPropertyObject = (IObjectWithProperties) aProperty;
                                if (aPropertyObject.getPropertiesObjectName().equals(OLD_IDENTIFIER_KEY)) {
                                    aPropertyObject.setPropertiesObjectName(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
                                    nextGroup.addObject(aPropertyObject);
                                } else {
                                    nextGroup.addObject(aPropertyObject);
                                }

                            }

                            convertedGroup[curGrp++] = nextGroup;
                        }
                    }

                    ((GraphModel) into).setPropertyGroups(convertedGroup);
                }
                elt.removeChild(pGroups);
            }
        }

        return super.beforeDecode(dec, node, into);
    }

    @Override
    public Object afterDecode(mxCodec dec, Node node, Object obj) {
        return super.afterDecode(dec, node, obj); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    protected void encodeObject(mxCodec mxcdc, Object o, Node node) {
        super.encodeObject(mxcdc, o, node);

        if (o instanceof GraphModel) {

            // reset temporary port ID list etc.
            ProcessPortCodec.reset();
            CellCodec.reset();

            // encode properties as array
            GraphModel model = (GraphModel) o;
            Collection<? extends IObjectWithProperties> properties = model.getProperties();
            PropertyGroup[] pArr = properties.toArray(new PropertyGroup<?>[]{});
            tmpPropertyGroup tmpPropertyGroup = new tmpPropertyGroup();
            tmpPropertyGroup.properties = pArr;
            Node propertiesNode = mxcdc.encode(tmpPropertyGroup);
            ((Element) node).appendChild(propertiesNode);
        }

    }

}
