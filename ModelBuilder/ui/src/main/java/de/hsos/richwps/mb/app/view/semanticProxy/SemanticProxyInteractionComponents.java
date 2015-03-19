package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.TransferHandler;

/**
 *
 * @author dziegenh
 */
public class SemanticProxyInteractionComponents {

    public final TransferHandler processTransferHandler;
    public final Component graphDndProxy;
    public final ProcessProvider processProvider;
    public final GraphView graphView;
    public final JFrame parent;

    public SemanticProxyInteractionComponents(JFrame parent, GraphView graphView, ProcessProvider processProvider, Component graphDndProxy, TransferHandler processTransferHandler) {
        this.parent = parent;
        this.graphView = graphView;
        this.processProvider = processProvider;
        this.graphDndProxy = graphDndProxy;
        this.processTransferHandler = processTransferHandler;
    }
}
