package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.WpsServer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Controlls the main tree view component and it's interaction with the
 * ModelBuilder.
 *
 * @author dziegenh
 * @author dalcacer
 */
public class MainTreeViewController extends AbstractTreeViewController {

    private DefaultMutableTreeNode processesNode;

    private HashMap<String, DefaultMutableTreeNode> remoteNodes = new HashMap<>();

    public MainTreeViewController(final App app) {
        super(app);

        // handle changes of the SP url config
        app.getPreferencesDialog().addWindowListener(new WindowAdapter() {
            
            private boolean urlHasChanged() {
                ProcessProvider processProvider = getProcessProvider();
                String spUrl = getSpUrlFromConfig();
                return !processProvider.getUrl().equals(spUrl);
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
                if (urlHasChanged()) {
                    fillTree();
                }
            }
        });
    }

    /**
     * Loads and return the configured SP url or the default url.
     *
     * @return
     */
    protected String getSpUrlFromConfig() {
        String key = AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name();
        String defaultValue = AppConstants.SEMANTICPROXY_DEFAULT_URL;
        return AppConfig.getConfig().get(key, defaultValue);
    }
    
    @Override
    void fillTree() {
        
        ProcessProvider processProvider = getProcessProvider();

        // Remove existing child-nodes from root
        DefaultMutableTreeNode root = getRoot();
        root.removeAllChildren();

        // Create and fill Process node
        processesNode = new DefaultMutableTreeNode(AppConstants.TREE_PROCESSES_NAME);
        if (processProvider != null) {
            try {
                String url = getSpUrlFromConfig();
                
                boolean connected = processProvider.isConnected() && processProvider.getUrl().equals(url);
                
                if (connected || processProvider.connect(url)) {
                    for (WpsServer server : processProvider.getAllServerWithProcesses()) {
                        // TODO check if it is useful to set the server entity as user object (instead of the endpoint string)
                        DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server.getEndpoint());
                        
                        for (ProcessEntity process : server.getProcesses()) {
                            serverNode.add(new DefaultMutableTreeNode(process));
                        }
                        processesNode.add(serverNode);
                    }
                }
                
            } catch (Exception ex) {
                // Inform user when SP client can't be created
                AppEvent event = new AppEvent(AppConstants.SEMANTICPROXY_CANNOT_CREATE_CLIENT, this, AppConstants.INFOTAB_ID_SEMANTICPROXY);
                AppEventService.getInstance().fireAppEvent(event);

                // Append exception message if available
                String exMsg = ex.getMessage();
                if (null != exMsg && !exMsg.isEmpty()) {
                    StringBuilder sb = new StringBuilder(200);
                    sb.append(AppConstants.ERROR_MSG_IS);
                    sb.append("\"");
                    sb.append(ex.getMessage());
                    sb.append("\"");
                    event.setMessage(sb.toString());
                    AppEventService.getInstance().fireAppEvent(event);
                }
            }
        }

        // Create and fill download services node
        DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);
        downloadServices.add(new DefaultMutableTreeNode(""));

        // TODO MOCK!! Create and fill local elements node
        DefaultMutableTreeNode local = new DefaultMutableTreeNode(AppConstants.TREE_LOCALS_NAME);
        // inputs
        ProcessPort cIn = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lIn = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cIn.setGlobalOutput(false);
        lIn.setGlobalOutput(false);
        local.add(new DefaultMutableTreeNode(cIn));
        local.add(new DefaultMutableTreeNode(lIn));
        // Outputs
        ProcessPort cOut = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lOut = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cOut.setGlobalOutput(true);
        lOut.setGlobalOutput(true);
        local.add(new DefaultMutableTreeNode(cOut));
        local.add(new DefaultMutableTreeNode(lOut));

        // add all child nodes to root
        root.add(processesNode);
        root.add(downloadServices);
        root.add(local);
        
        updateUI();
    }

    /**
     * Adds a node.
     *
     * @param uri (WPS-endpoint).
     */
    public void addNode(String uri) {
        IRichWPSProvider provider = new RichWPSProvider();
        List<ProcessEntity> pes = new ArrayList<>();
        // TODO check if it is useful to set the server entity as user object (instead of the endpoint string)
        DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(uri);
        try {
            provider.connect(uri);
            List<String> processes = provider.getAvailableProcesses(uri);
            
            for (String processid : processes) {
                
                DescribeRequest pd = new DescribeRequest();
                pd.setEndpoint(uri);
                pd.setIdentifier(processid);
                provider.describeProcess(pd);
                
                ProcessEntity pe = new ProcessEntity(uri, pd.getIdentifier());
                pe.setOwsAbstract(pd.getAbstract());
                //TRICKY
                if (pd.getTitle() != null) {
                    if (!pd.getTitle().contains("xml-fragment")) {
                        pe.setOwsTitle(pd.getTitle());
                    } else {
                        pe.setOwsTitle(pd.getIdentifier());
                    }
                }
                //FIXME pe.setOwsVersion
                this.transformInputs(pd, pe);
                this.transformOutputs(pd, pe);
                serverNode.add(new DefaultMutableTreeNode(pe));
            }
            processesNode.add(serverNode);
            this.updateUI();
        } catch (Exception e) {
            Logger.log("Debug:\n error occured " + e);
        }
    }

    /**
     * Transforms DescribeRequest Inputs to ProcessEntity ProcessPorts.
     *
     * @param pd ProcessDescription with IInputSpecifier.
     * @param pe ProcessEntity with ProcessPorts.
     */
    private void transformInputs(DescribeRequest pd, ProcessEntity pe) {
        
        for (IInputSpecifier specifier : pd.getInputs()) {
            if (specifier instanceof InputComplexDataSpecifier) {
                InputComplexDataSpecifier complex = (InputComplexDataSpecifier) specifier;
                ProcessPort pp = new ProcessPort(ProcessPortDatatype.COMPLEX);
                pp.setOwsIdentifier(complex.getIdentifier());
                pp.setOwsTitle(complex.getTitle());
                pp.setOwsAbstract(complex.getAbstract());
                //FIXME pp.setVersion 
                List<String> defaulttype = complex.getDefaultType();
                String encoding = defaulttype.get(InputComplexDataSpecifier.encoding_IDX);
                String mimetype = defaulttype.get(InputComplexDataSpecifier.mimetype_IDX);
                String schema = defaulttype.get(InputComplexDataSpecifier.schema_IDX);
                ComplexDataTypeFormat format = new ComplexDataTypeFormat(mimetype, schema, encoding);
                IDataTypeDescription typedesc = new DataTypeDescriptionComplex(format);
                pp.setDataTypeDescription(typedesc);
                pe.addInputPort(pp);
            } else if (specifier instanceof InputLiteralDataSpecifier) {
                InputLiteralDataSpecifier literal = (InputLiteralDataSpecifier) specifier;
                ProcessPort pp = new ProcessPort(ProcessPortDatatype.LITERAL);
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());
                pe.addInputPort(pp);
            } else if (specifier instanceof InputBoundingBoxDataSpecifier) {
                //TODO
            }
        }
    }

    /**
     * Transforms DescribeRequest Outputs to ProcessEntity ProcessPorts.
     *
     * @param pd DescribeRequest with IOutputSpecifier.
     * @param pe ProcessEntity with ProcessPorts.
     */
    private void transformOutputs(DescribeRequest pd, ProcessEntity pe) {
        
        for (IOutputSpecifier specifier : pd.getOutputs()) {
            if (specifier instanceof OutputComplexDataSpecifier) {
                OutputComplexDataSpecifier complex = (OutputComplexDataSpecifier) specifier;
                ProcessPort pp = new ProcessPort(ProcessPortDatatype.COMPLEX);
                pp.setOwsIdentifier(complex.getIdentifier());
                pp.setOwsTitle(complex.getTitle());
                pp.setOwsAbstract(complex.getAbstract());
                //FIXME pp.setVersion 
                List<String> defaulttype = complex.getDefaultType();
                String encoding = defaulttype.get(OutputComplexDataSpecifier.encoding_IDX);
                String mimetype = defaulttype.get(OutputComplexDataSpecifier.mimetype_IDX);
                String schema = defaulttype.get(OutputComplexDataSpecifier.schema_IDX);
                ComplexDataTypeFormat format = new ComplexDataTypeFormat(mimetype, schema, encoding);
                IDataTypeDescription typedesc = new DataTypeDescriptionComplex(format);
                pp.setDataTypeDescription(typedesc);
                pe.addOutputPort(pp);
            } else if (specifier instanceof OutputLiteralDataSpecifier) {
                OutputLiteralDataSpecifier literal = (OutputLiteralDataSpecifier) specifier;
                ProcessPort pp = new ProcessPort(ProcessPortDatatype.LITERAL);
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());
                pe.addInputPort(pp);
            } else if (specifier instanceof OutputBoundingBoxDataSpecifier) {
                //TODO
            }
        }
    }

    void setRemotes(String[] remotes) {

        // clone currently used remote nodes to identify removed nodes
        LinkedList<String> unusedNodes = new LinkedList<>(remoteNodes.keySet());
        unusedNodes = (LinkedList<String>) unusedNodes.clone();

        for (String remote : remotes) {
            if (!remoteNodes.containsKey(remote)) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(remote);
                processesNode.add(node);
                remoteNodes.put(remote, node);
            } else {
                unusedNodes.remove(remote);
            }
        }

        for (String unusedNode : unusedNodes) {
            processesNode.remove(remoteNodes.get(unusedNode));
            remoteNodes.remove(unusedNode);
        }
    }

}
