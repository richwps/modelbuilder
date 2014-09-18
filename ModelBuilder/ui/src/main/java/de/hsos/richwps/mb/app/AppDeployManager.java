package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Exporter;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import de.hsos.richwps.mb.server.boundary.DeployView;
import de.hsos.richwps.mb.server.entity.DeployConfig;
import de.hsos.richwps.mb.server.entity.DeployConfigField;
import de.hsos.richwps.mb.server.exception.GraphToRequestTransformationException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author dziegenh
 * @author dalcacer
 */
public class AppDeployManager {

    
    private App app;

    public AppDeployManager(App app) {
        this.app = app;
    }

    private GraphView getGraphView() {
        return app.getGraphView();
    }

    private JFrame getFrame() {
        return app.getFrame();
    }

    /**
     * Starts the Deploy-Dialog.
     */
    boolean deploy() {
        //Start a Dialog.

        final DeployView deployView = new DeployView(getFrame(), AppConstants.DEPLOY_DIALOG_TITLE);

        // TODO persist model's configs and get persisted configs
        LinkedList<DeployConfig> configs = new LinkedList<>();
        {
            DeployConfig mockConfig = new DeployConfig();
            mockConfig.setValue(DeployConfigField.ENDPOINT, "http://richwps.edvsz.hs-osnabrueck.de/lkn/WPS-T");
            mockConfig.setValue(DeployConfigField.ABSTRACT, "No Abstract");
            mockConfig.setValue(DeployConfigField.IDENTIFIER, "rola.SelectRepArea.Wrapper");
            mockConfig.setValue(DeployConfigField.TITLE, "rola.SelectRepArea.Wrapper");
            mockConfig.setValue(DeployConfigField.VERSION, "1");
            configs.add(mockConfig);
        }

        deployView.init(configs);

        deployView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                DeployConfig wpstconfig = deployView.getConfig();
                final String rola = _generateRola(wpstconfig);
                if (null == rola) {
                    String message = "Unable to generate ROLA script.";
                    AppEventService service = AppEventService.getInstance();
                    service.fireAppEvent(message, AppConstants.INFOTAB_ID_SERVER);
                }
                _performDeployment(wpstconfig, rola);
            }
        });
        deployView.setVisible(true);
        return true;
    }

    private String _generateRola(DeployConfig config) {
        String wpstendpoint = config.getValue(DeployConfigField.ENDPOINT);

        final String rola = this.generateDSL(wpstendpoint);
        if (null == rola) {
            return null;
        }
        return rola;
    }

    private boolean _performDeployment(DeployConfig config, String rola) {
        String wpstendpoint = config.getValue(DeployConfigField.ENDPOINT);
        String identifier = config.getValue(DeployConfigField.IDENTIFIER);
        String title = config.getValue(DeployConfigField.TITLE);
        String processversion = config.getValue(DeployConfigField.VERSION);
        String wpsAbstract = config.getValue(DeployConfigField.ABSTRACT);

        DeployRequest request = new DeployRequest(wpstendpoint, identifier, title, processversion, RichWPSProvider.deploymentProfile);
        request.setExecutionUnit(rola);
        request.setAbstract(wpsAbstract);
        try {
            this.assembleDeployRequest(request);
        } catch (GraphToRequestTransformationException ex) {
            // TODO create msg dialog
            Logger.getLogger(AppDeployManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        //FIXME
        String wpsurl = wpstendpoint.split(RichWPSProvider.DEFAULT_WPST_ENDPOINT)[0]+RichWPSProvider.DEFAULT_WPS_ENDPOINT;
        de.hsos.richwps.mb.Logger.log("Debug::AppDeploymanager::_performDeployment(): "+wpsurl);

        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl, wpstendpoint);
            instance.deployProcess(request);
            if (request.isException()) {
                AppEventService service = AppEventService.getInstance();
                service.fireAppEvent("Error. unable to deploy process: " + request.getException(), AppConstants.INFOTAB_ID_SERVER);
                return false;
            }
            AppEventService service = AppEventService.getInstance();
            service.fireAppEvent("Process deployed.", AppConstants.INFOTAB_ID_SERVER);

        } catch (Exception ex) {
            AppEventService service = AppEventService.getInstance();
            service.fireAppEvent("Error. unable to deploy process: " + ex.getLocalizedMessage(), AppConstants.INFOTAB_ID_SERVER);
            return false;
        }

        return true;
    }

    /**
     *
     * @param wpstendpoint WPST-endpoint for automatic detection of local
     * bindings.
     * @return
     */
    private String generateDSL(String wpstendpoint) {
        try {
            String dslFile = "generated.rola";
            //FIXME mv to tmp System.getProperty("java.io.tmpdir")

            new Exporter(getGraphView().getGraph()).export(dslFile, wpstendpoint);

            String content = null;
            File file = new File(dslFile); //for ex foo.txt
            FileReader reader = new FileReader(file);
            try {
                char[] chars = new char[(int) file.length()];
                reader.read(chars);
                content = new String(chars);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
            //JOptionPane.showMessageDialog(getFrame(), content, "Generated ROLA", JOptionPane.PLAIN_MESSAGE);
            return content;

        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(200);
            sb.append(AppConstants.DEPLOYMENT_FAILED);
            sb.append('\n');
            sb.append(AppConstants.SEE_LOGGING_TABS);

            JOptionPane.showMessageDialog(getFrame(), sb.toString());

            String exMsg;
            if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
                exMsg = ex.getClass().getSimpleName();
            } else {
                exMsg = ex.getMessage();
            }

            sb = new StringBuilder(200);
            sb.append(AppConstants.DEPLOYMENT_FAILED);
            sb.append('\n');
            sb.append(String.format(AppConstants.ERROR_MSG_IS_FORMAT, exMsg));

            AppEventService.getInstance().fireAppEvent(sb.toString(), AppConstants.INFOTAB_ID_SERVER);
            Logger
                    .getLogger(App.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void assembleDeployRequest(DeployRequest request) throws GraphToRequestTransformationException {
        Graph graph = getGraphView().getGraph();

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputSpecifier specifier = AppDeployManager.createInputPortSpecifier(port);
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(specifier);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputSpecifier specifier = null;
            specifier = AppDeployManager.createOutputPortSpecifier(port);

            /* if (AppDeployManager.createOutputPortSpecifier(port) instanceof OutputComplexDataSpecifier) {
             specifier = AppDeployManager.createOutputPortSpecifier(port);

             } else if (AppDeployManager.createOutputPortSpecifier(port) instanceof OutputLiteralDataSpecifier) {
                
             }*/
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(specifier);
        }
    }

    public static IInputSpecifier createInputPortSpecifier(ProcessPort port) {
        if (null == port || !port.isGlobalInput()) {
            throw new IllegalArgumentException("invalid port (null or not an input)");
        }

        IInputSpecifier specifier = null;

        switch (port.getDatatype()) {
            case LITERAL:
                InputLiteralDataSpecifier literalSpecifier = new InputLiteralDataSpecifier();
                literalSpecifier.setIdentifier(port.getOwsIdentifier());
                literalSpecifier.setAbstract(port.getOwsAbstract());
                literalSpecifier.setTitle(port.getOwsTitle());
                if (literalSpecifier.getTitle().equals("")) {
                    literalSpecifier.setTitle(literalSpecifier.getIdentifier());
                }
                literalSpecifier.setMinOccur(0);
                literalSpecifier.setMaxOccur(1);
                literalSpecifier.setType(("xs:string"));
                literalSpecifier.setDefaultvalue("");
                specifier = literalSpecifier;
                break;

            case COMPLEX:
                InputComplexDataSpecifier complexSpecifier = new InputComplexDataSpecifier();
                complexSpecifier.setIdentifier(port.getOwsIdentifier());
                complexSpecifier.setAbstract(port.getOwsAbstract());
                complexSpecifier.setTitle(port.getOwsTitle());
                if (complexSpecifier.getTitle().equals("")) {
                    complexSpecifier.setTitle(complexSpecifier.getIdentifier());
                }
                complexSpecifier.setMinOccur(0);
                complexSpecifier.setMaxOccur(1);
                complexSpecifier.setMaximumMegabytes(50);//FIXME

                List<List> supportedTypes = new ArrayList<>();
                List<String> supportedType = new ArrayList<>();
                IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();

                if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
                    DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) dataTypeDescription;
                    ComplexDataTypeFormat format = description.getFormat();
                    supportedType.add(format.getMimeType());
                    supportedType.add(format.getSchema());
                    supportedType.add(format.getEncoding());
                }
                supportedTypes.add(supportedType);
                complexSpecifier.setTypes(supportedTypes);
                complexSpecifier.setDefaulttype(supportedType);

                specifier = complexSpecifier;
                break;

            case BOUNDING_BOX:
                break;
        }

//        if (null != specifier) {
//            specifier.setIdentifier(port.getOwsIdentifier());
//            specifier.setAbstract(port.getOwsAbstract());
//            specifier.setTitle(port.getOwsTitle());
//            specifier.setMinOccur(0);
//            specifier.setMaxOccur(1);
//        }
        return specifier;
    }

    public static IOutputSpecifier createOutputPortSpecifier(ProcessPort port) {
        if (null == port || !port.isGlobalOutput()) {
            throw new IllegalArgumentException("invalid port (null or not an output)");
        }

        IOutputSpecifier specifier = null;

        switch (port.getDatatype()) {
            case LITERAL:
                OutputLiteralDataSpecifier literalSpecifier = new OutputLiteralDataSpecifier();
                literalSpecifier.setIdentifier(port.getOwsIdentifier());
                literalSpecifier.setAbstract(port.getOwsAbstract());
                literalSpecifier.setTitle(port.getOwsTitle());
                if (literalSpecifier.getTitle().equals("")) {
                    literalSpecifier.setTitle(literalSpecifier.getIdentifier());
                }
                literalSpecifier.setType(("xs:string"));
                specifier = literalSpecifier;
                break;

            case COMPLEX:
                OutputComplexDataSpecifier complexSpecifier = new OutputComplexDataSpecifier();
                complexSpecifier.setIdentifier(port.getOwsIdentifier());
                complexSpecifier.setTheabstract(port.getOwsAbstract());
                complexSpecifier.setTitle(port.getOwsTitle());
                if (complexSpecifier.getTitle().equals("")) {
                    complexSpecifier.setTitle(complexSpecifier.getIdentifier());
                }

                List<List> supportedTypes = new ArrayList<>();
                List<String> supportedType = new ArrayList();

                List<String> atype = new ArrayList<>();
                atype.add("text/xml");   // mimetype
                atype.add("http://schemas.opengis.net/gml/3.2.1/base/feature.xsd");  // schema
                atype.add("");  // encoding

                List<List> types = new ArrayList<>();
                types.add(atype);

                complexSpecifier.setTypes(types);
                complexSpecifier.setDefaulttype(atype);


                /*
                 FIXME resolve the workaround below.
                 IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();
                 if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
                 DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) dataTypeDescription;
                 ComplexDataTypeFormat format = description.getFormat();
                 supportedType.add(format.getMimeType());
                 supportedType.add(format.getSchema());
                 supportedType.add(format.getEncoding());
                 }*/
                specifier = complexSpecifier;
                break;

            case BOUNDING_BOX:
                break;
        }

//        if (null != specifier) {
//            specifier.setIdentifier(port.getOwsIdentifier());
//            specifier.setAbstract(port.getOwsAbstract());
//            specifier.setTitle(port.getOwsTitle());
//            specifier.setMinOccur(0);
//            specifier.setMaxOccur(1);
//        }
        return specifier;
    }
}
