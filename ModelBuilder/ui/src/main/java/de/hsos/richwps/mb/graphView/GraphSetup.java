package de.hsos.richwps.mb.graphView;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphComponent;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdgeShape;
import de.hsos.richwps.mb.graphView.mxGraph.codec.GraphEdgeCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.GraphModelCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.ProcessEntityCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.ProcessPortCodec;
import de.hsos.richwps.mb.graphView.mxGraph.layout.GraphWorkflowLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.border.EmptyBorder;

/**
 * Basic setup (constants, styles, behaviour) for the RichWPS graph.
 *
 * @author dziegenh
 */
public class GraphSetup {

    // TODO move to config/constants
    private static final int fontSize = 15;
    private static final int spacing = 4;

    final static int CELLS_VERTICAL_OFFSET = 100;

    static String STYLENAME_GLOBAL_INPUT = "PROCESS_INPUT";
    static String STYLENAME_GLOBAL_OUTPUT = "PROCESS_OUTPUT";
    static String STYLENAME_PROCESS = "PROCESS";
    static String STYLENAME_LOCAL_PORT = "PORT";

    // (auto-) Layout
    static final int LAYOUT_COMPONENT_GAP = 50;
    static final int LAYOUT_CELL_GAP = 20;

    public final static String STYLE_SHAPE = "GRAPH_EDGE";
    private static float SELECTION_BORDER_WIDTH = 1.5f;

    public final static Color GRAPH_BG_COLOR = Color.WHITE;
    public final static Color GRAPH_EDGE_SHIFTED_COLOR = new Color(240, 240, 250);
    public final static Color GRAPH_EDGE_COLOR = Color.BLACK; //(new Color(0xa0ace5)).darker().darker();
    private final static double GRAPH_EDGE_ROUNDED_SIZE = 5.;

    /**
     * Initialises graph-independent codecs, constants etc.
     */
    public static void init() {
        // register codecs for custom classes
        mxCodecRegistry.addPackage("de.hsos.richwps.mb.entity");
        mxCodecRegistry.register(new mxObjectCodec(new de.hsos.richwps.mb.entity.ComplexDataTypeFormat()));
        mxCodecRegistry.register(new mxObjectCodec(new de.hsos.richwps.mb.entity.DataTypeDescriptionComplex()));
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.ProcessPort()));
        mxCodecRegistry.register(new ProcessEntityCodec(new de.hsos.richwps.mb.entity.ProcessEntity()));
        mxCodecRegistry.register(new GraphEdgeCodec(new de.hsos.richwps.mb.graphView.mxGraph.GraphEdge()));
        mxCodecRegistry.register(new GraphModelCodec(new de.hsos.richwps.mb.graphView.mxGraph.GraphModel()));

        // style for cell selection
        mxSwingConstants.VERTEX_SELECTION_COLOR = AppConstants.SELECTION_BG_COLOR;
        mxSwingConstants.VERTEX_SELECTION_STROKE = new BasicStroke(SELECTION_BORDER_WIDTH,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new float[]{
                    3 * SELECTION_BORDER_WIDTH, 3 * SELECTION_BORDER_WIDTH}, 0.0f);

        // custom edge shape for rendering
        mxGraphics2DCanvas.putShape(STYLE_SHAPE, new GraphEdgeShape());

        // Round edge size
        mxConstants.LINE_ARCSIZE = GRAPH_EDGE_ROUNDED_SIZE;
    }

    /**
     * Sets up a specific graph (styles, behaviour).
     *
     * @param graph
     * @return
     */
    public static Graph setup(Graph graph) {
        // graph setup
        graph.setCellsDisconnectable(true);
        graph.setAllowDanglingEdges(false);
        graph.setEdgeLabelsMovable(false);
        graph.setConnectableEdges(false);
        graph.setCellsResizable(false);
        graph.setCellsDeletable(true);
        graph.setCellsEditable(false);
        graph.setPortsEnabled(true);
        graph.setDropEnabled(false);
        graph.setAllowLoops(AppConstants.GRAPH_ALLOW_FEEDBACK_LOOPS);
        graph.setMultigraph(false);
        graph.setAutoLayout(AppConstants.GRAPH_AUTOLAYOUT);
        graph.setCellsMovable(!graph.isAutoLayout());

        graph.setGridSize(10);
        GraphWorkflowLayout graphWorkflowLayout = graph.getGraphWorkflowLayout();
        graphWorkflowLayout.setCellGap(LAYOUT_CELL_GAP);
        graphWorkflowLayout.setGraphComponentGap(LAYOUT_COMPONENT_GAP);

        // size (ratio) of the connectable port part (in percent relative to port size)
        mxConstants.DEFAULT_HOTSPOT = graph.isAutoLayout() ? 1 : .5;

        // EDGE STYLE
        mxStylesheet stylesheet = graph.getStylesheet();
        Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "000000");
        Object styleEdge = mxConstants.EDGESTYLE_TOPTOBOTTOM;
//        if (graph.isAutoLayout()) {
//            edgeStyle = mxConstants.NONE;
//        }
        edgeStyle.put(mxConstants.STYLE_EDGE, styleEdge);
        edgeStyle.put(mxConstants.STYLE_SHAPE, STYLE_SHAPE);
        edgeStyle.put(mxConstants.STYLE_ROUNDED, "1");
        graph.setStylesheet(stylesheet);

        // PROCESS STYLE
        Hashtable<String, Object> processStyle = new Hashtable<String, Object>();
        processStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        processStyle.put(mxConstants.STYLE_OPACITY, 80);
        processStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        processStyle.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
        processStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        processStyle.put(mxConstants.STYLE_FONTSIZE, fontSize);
        processStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        processStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#f6f6f6");
//            processStyle.put(mxConstants.STYLE_MOVABLE, "0");
        processStyle.put(mxConstants.STYLE_SPACING_TOP, spacing);
        stylesheet.putCellStyle("PROCESS", processStyle);

        // GLOBAL INPUT PORT STYLE
        Hashtable<String, Object> processInputStyle = (Hashtable<String, Object>) processStyle.clone();
        stylesheet.putCellStyle("PROCESS_INPUT", processInputStyle);

        // GLOBAL OUTPUT PORT STYLE
        Hashtable<String, Object> processOutputStyle = new Hashtable<String, Object>();
        processOutputStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        processOutputStyle.put(mxConstants.STYLE_OPACITY, 80);
        processOutputStyle.put(mxConstants.STYLE_FONTCOLOR, "#ffffff");
        processOutputStyle.put(mxConstants.STYLE_FILLCOLOR, "#808080");
        processOutputStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        processOutputStyle.put(mxConstants.STYLE_FONTSIZE, fontSize);
        processOutputStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        processOutputStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#000000");
//            processStyle.put(mxConstants.STYLE_MOVABLE, "0");
        processOutputStyle.put(mxConstants.STYLE_SPACING_TOP, spacing);
        stylesheet.putCellStyle("PROCESS_OUTPUT", processOutputStyle);

        // PROCESS (SUB-) PORT STYLE
        Hashtable<String, Object> portStyle = new Hashtable<String, Object>();
        portStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        portStyle.put(mxConstants.STYLE_OPACITY, 100);
        portStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        portStyle.put(mxConstants.STYLE_FILLCOLOR, "none");
//            portStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#f9f9f9"); // changed font size
        portStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        portStyle.put(mxConstants.STYLE_FONTSIZE, fontSize);
        portStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        portStyle.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
        portStyle.put(mxConstants.STYLE_SPACING_TOP, spacing);
        stylesheet.putCellStyle("PORT", portStyle);

        return graph;
    }

    static void setupGraphComponent(GraphComponent component) {
            component.setToolTips(true);
            component.setBorder(new EmptyBorder(0, 0, 0, 0));
            component.getViewport().setBackground(GRAPH_BG_COLOR);
    }

}
