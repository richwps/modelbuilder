package de.hsos.richwps.mb.graphView.mxGraph.codec;

import de.hsos.richwps.mb.graphView.mxGraph.codec.objects.tmpPropertyGroup;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxModelCodec;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.util.Collection;
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

//    @Override
//    public Object decode(mxCodec dec, Node node, Object into) {
//        // reset port cache for decoding the model's children
//        ProcessPortCodec.reset();
//
//        NodeList childNodes = node.getChildNodes();
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            Node item = childNodes.item(i);
//            if (null != item.getNodeName() && "Array" == item.getNodeName()) {
////                Object decode = super.decode(dec, item, null);
//                Node nameItem = item.getAttributes().getNamedItem("name");
//                if (null != nameItem) {
//                    String nameItemValue = nameItem.getNodeValue();
//                    if (nameItemValue.equals(this.propertyGroupsAttributeName)) {
//
//                        tmpPropertyGroup tmpPropertyGroup = new tmpPropertyGroup();
//
//                        decodeChild(dec, item, tmpPropertyGroup);
//
////                        Logger.log(new mxObjectCodec(tmpPropertyGroup).decode(dec, node, tmpPropertyGroup));
//                         Logger.log("properties groups: " + tmpPropertyGroup.propertyGroups.length);
//                    }
//                }
//            }
////            Logger.log(item);
//        }
//
//        return super.decode(dec, node, into);
//    }
    @Override
    public Node beforeDecode(mxCodec dec, Node node, Object into) {

        ProcessPortCodec.reset();
        
//    @Override
//    public Object decode(mxCodec dec, Node node, Object into) {
//        // reset port cache for decoding the model's children
//        ProcessPortCodec.reset();
//
//        NodeList childNodes = node.getChildNodes();
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            Node item = childNodes.item(i);
//            if (null != item.getNodeName() && "Array" == item.getNodeName()) {
////                Object decode = super.decode(dec, item, null);
//                Node nameItem = item.getAttributes().getNamedItem("name");
//                if (null != nameItem) {
//                    String nameItemValue = nameItem.getNodeValue();
//                    if (nameItemValue.equals(this.propertyGroupsAttributeName)) {
//
//                        tmpPropertyGroup tmpPropertyGroup = new tmpPropertyGroup();
//                        
//                        decodeChild(dec, item, tmpPropertyGroup);
//                        
////                        Logger.log(new mxObjectCodec(tmpPropertyGroup).decode(dec, node, tmpPropertyGroup));
//                         Logger.log("properties groups: " + tmpPropertyGroup.propertyGroups.length);
//                    }
//                }
//            }
////            Logger.log(item);
//        }
//
//        return super.decode(dec, node, into);
//    }
        if (node instanceof Element) {
            Element elt = (Element) node;

            NodeList pGroupsElements = elt.getElementsByTagName(this.propertyGroupsElement);

            if (null != pGroupsElements && pGroupsElements.getLength() > 0) {
                Node pGroups = pGroupsElements.item(0);

                if (null != into && (into instanceof GraphModel)) {
                    tmpPropertyGroup tmpGroup = new tmpPropertyGroup();
                    dec.decode(pGroups, tmpGroup);
                    ((GraphModel) into).setPropertyGroups(tmpGroup.properties);
                }
//                return dec.decode(pGroups);
//            mxICell rootCell = null;

//            if (root != null) {
//                Node tmp = root.getFirstChild();
//
//                while (tmp != null) {
//                    mxICell cell = dec.decodeCell(tmp, true);
//
//                    if (cell != null && cell.getParent() == null) {
//                        rootCell = cell;
//                    }
//
//                    tmp = tmp.getNextSibling();
//                }
//
//                root.getParentNode().removeChild(root);
//            }
                // Sets the root on the model if one has been decoded
//            if (rootCell != null) {
//                model.setRoot(rootCell);
//            }
                elt.removeChild(pGroups);
            }
        }

        return super.beforeDecode(dec, node, into);
    }

    @Override
    protected void encodeObject(mxCodec mxcdc, Object o, Node node) {
        super.encodeObject(mxcdc, o, node);

        if (o instanceof GraphModel) {

            // reset temporary port ID list etc.
            ProcessPortCodec.reset();

            // encode properties as array
            GraphModel model = (GraphModel) o;
            Collection<? extends IObjectWithProperties> properties = model.getProperties();
            PropertyGroup[] pArr = properties.toArray(new PropertyGroup<?>[]{});

            tmpPropertyGroup tmpPropertyGroup = new tmpPropertyGroup();
            tmpPropertyGroup.properties = pArr;
            Node propertiesNode = mxcdc.encode(tmpPropertyGroup);
//            Node propertiesNode = new mxObjectCodec(tmpPropertyGroup).encode(mxcdc, tmpPropertyGroup);

//            Node propertiesNode = new mxObjectCodec(pArr).encode(mxcdc, pArr);
            Element nodeEl = (Element) node;
//            ((Element) propertiesNode).setAttribute("name", this.propertyGroupsElement);
//            ((Element) propertiesNode).setAttribute("as", "propertyGroups");
            nodeEl.appendChild(propertiesNode);
        }

    }

}
