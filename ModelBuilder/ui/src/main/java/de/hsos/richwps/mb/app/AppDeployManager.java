/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Export;
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
            mockConfig.setValue(DeployConfigField.ENDPOINT, "http://richwps.edvsz.hs-osnabrueck.de/lkn/WPST");
            mockConfig.setValue(DeployConfigField.ABSTRACT, "ABSTRACT");
            mockConfig.setValue(DeployConfigField.IDENTIFIER, "IDENTIFIER");
            mockConfig.setValue(DeployConfigField.TITLE, "TITLE");
            mockConfig.setValue(DeployConfigField.VERSION, "VERSION");
            configs.add(mockConfig);
        }

        deployView.init(configs);

        deployView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                DeployConfig wpstconfig = deployView.getConfig();
                final String rola = _generateRola(wpstconfig);
                if (null == rola) {
                    de.hsos.richwps.mb.Logger.log(("No rola at all :("));
                }
                _performDeployment(wpstconfig, rola);
            }
        });
        deployView.setVisible(true);
        return true;
    }

    private String _generateRola(DeployConfig config) {
        String wpstendpoint = config.getValue(DeployConfigField.ENDPOINT);

//        instance.deploy(dto);
//        instance.disconnect(wpsurl, wpsturl);
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

        DeployRequest dto = new DeployRequest(wpstendpoint, identifier, title, processversion, rola);
        dto.setAbstract(wpsAbstract);
        try {
            assembleDeployRequest(dto);
        } catch (GraphToRequestTransformationException ex) {
            // TODO create msg dialog
            Logger.getLogger(AppDeployManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // TODO create gui for wpsurl etc. !!
        String wpsurl = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String wpsturl = wpsurl;

        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl, wpsturl);
        } catch (Exception ex) {
            getLogger(AppDeployManager.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    /**
     * 
     * @param wpstendpoint WPST-endpoint for automatic detection of local bindings.
     * @return 
     */
    private String generateDSL(String wpstendpoint) {
        try {
            String dslFile = "generated.rola";
            //FIXME mv to tmp System.getProperty("java.io.tmpdir")
//            new Export(getGraphView().getGraph().clone()).export(dslFile);
            new Export(getGraphView().getGraph()).export(dslFile, wpstendpoint);

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
            JOptionPane.showMessageDialog(getFrame(), content, "Generated ROLA", JOptionPane.PLAIN_MESSAGE);
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

    private void assembleDeployRequest(DeployRequest dto) throws GraphToRequestTransformationException {
        Graph graph = getGraphView().getGraph();

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputSpecifier specifier = AppDeployManager.createInputPortSpecifier(port);
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            dto.addInput(specifier);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputSpecifier specifier = AppDeployManager.createOutputPortSpecifier(port);
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            dto.addOutput(specifier);
        }

        // TODO set execution unit
        dto.setExecutionUnit("Execunit.");
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
                complexSpecifier.setMinOccur(0);
                complexSpecifier.setMaxOccur(1);

                List<List> listOfFormatLists = new LinkedList<>();
                List<String> formatList = new LinkedList<>();
                IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();
                if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
                    DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) dataTypeDescription;
                    ComplexDataTypeFormat format = description.getFormat();
                    formatList.add(format.getMimeType());
                    formatList.add(format.getSchema());
                    formatList.add(format.getEncoding());
                }
                listOfFormatLists.add(formatList);
                complexSpecifier.setTypes(listOfFormatLists);
                complexSpecifier.setDefaulttype(formatList);

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
                literalSpecifier.setType(("xs:string"));
                specifier = literalSpecifier;
                break;

            case COMPLEX:
                OutputComplexDataSpecifier complexSpecifier = new OutputComplexDataSpecifier();
                complexSpecifier.setIdentifier(port.getOwsIdentifier());
                complexSpecifier.setTheabstract(port.getOwsAbstract());
                complexSpecifier.setTitle(port.getOwsTitle());

                List<List> listOfFormatLists = new LinkedList<>();
                List<String> formatList = new LinkedList<>();
                IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();
                if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
                    DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) dataTypeDescription;
                    ComplexDataTypeFormat format = description.getFormat();
                    formatList.add(format.getMimeType());
                    formatList.add(format.getSchema());
                    formatList.add(format.getEncoding());
                }
                listOfFormatLists.add(formatList);
                complexSpecifier.setTypes(listOfFormatLists);
                complexSpecifier.setDefaulttype(formatList);

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
