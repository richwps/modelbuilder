package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Exporter;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import de.hsos.richwps.mb.server.exception.GraphToRequestTransformationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages script creation, processdescription creation and the deployment.
 *
 * @author dziegenh
 * @author dalcacer
 * @version 0.0.4
 * @TODO Refactor source.
 */
public class AppDeployManager {

    private App app;
    private Graph graph;

    /**
     * Indicates deployment error.
     */
    private static boolean deploymentError = false;

    public AppDeployManager(App app) {
        this.app = app;

        AppDeployManager.deploymentError = false;
        this.graph = app.getGraphView().getGraph();
    }

    /**
     * Starts the Deploy-Dialog.
     */
    void deploy() {

        //generate rola
        String wpsturi = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ENDPOINT);
        final String rola = this.generateROLA();
        if (null == rola) {
            AppDeployManager.deploymentError = true;
        }

        //generate processdescription
        String identifier = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_IDENTIFIER);
        String title = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_TITLE);
        String version = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_VERSION);
        String theabstract = (String) this.graph.getGraphModel().getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ABSTRACT);

        //TODO remove endpoint assumption :(
        String wpsuri = wpsturi;
        wpsuri = wpsuri.replace(IRichWPSProvider.DEFAULT_WPST_ENDPOINT, IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
        DeployRequest request = new DeployRequest(wpsuri, wpsturi, identifier, title, version, RichWPSProvider.deploymentProfile);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.defineInOut(request);
        } catch (GraphToRequestTransformationException ex) {
            AppDeployManager.deploymentError = true;
            AppDeployManager.deploymentFailed("An error occured while assembling "
                    + "information for deploy request.");
        }


        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsuri, wpsturi);
            instance.deployProcess(request);

            if (request.isException()) {
                AppDeployManager.deploymentError = true;
                AppDeployManager.deploymentFailed("An error occured while deployment.");

            }

            AppEventService service = AppEventService.getInstance();
            service.fireAppEvent("Process deployed.", AppConstants.INFOTAB_ID_SERVER);

        } catch (Exception ex) {
            AppDeployManager.deploymentError = true;
            AppDeployManager.deploymentFailed("An error occured while deployment.");
        }

    }

    /**
     * Generates a ROLA-script based on the current model.
     *
     * @param wpstendpoint WPST-endpoint for automatic detection of local
     * bindings.
     * @return Emptystring in case of exception, else rola-script.
     */
    private String generateROLA() {
        try {

            File f = null;
            try {
                // creates temporary file
                f = File.createTempFile(System.currentTimeMillis() + "", ".dsl");
                f.deleteOnExit();
            } catch (Exception e) {
                AppDeployManager.deploymentError = true;
                AppDeployManager.processingFailed(AppConstants.TMP_FILE_FAILED);
                de.hsos.richwps.mb.Logger.log("Debug::AppDeployManager::generateROLA()\n "
                        + AppConstants.TMP_FILE_FAILED + " " + e.getLocalizedMessage());
                return "";
            }

            Exporter exporter = new Exporter((this.graph));
            exporter.export(f.getAbsolutePath());

            String content = null;
            FileReader reader = new FileReader(f);
            try {
                char[] chars = new char[(int) f.length()];
                reader.read(chars);
                content = new String(chars);
            } catch (IOException e) {
                AppDeployManager.deploymentError = true;
                e.printStackTrace();
            } finally {
                reader.close();
            }
            return content;

        } catch (Exception ex) {
            AppDeployManager.deploymentError = true;
            AppDeployManager.deploymentFailed("An error occured whilst generating "
                    + "ROLA-script for deploy request.");
            Logger.log("Debug:\n" + ex.getLocalizedMessage());
            /*StringBuilder sb = new StringBuilder(200);
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
             .getName()).log(Level.SEVERE, null, ex);*/
        }
        return null;
    }

    private void defineInOut(DeployRequest request) throws GraphToRequestTransformationException {

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
                try {
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

                    if (supportedTypes.isEmpty()) {
                        AppDeployManager.deploymentError = true;
                        AppDeployManager.processingFailed("Supported types for input "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }
                    if (supportedType.isEmpty()) {
                        AppDeployManager.deploymentError = true;
                        AppDeployManager.processingFailed("Default type for input "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }

                    complexSpecifier.setTypes(supportedTypes);
                    complexSpecifier.setDefaulttype(supportedType);
                } catch (Exception e) {
                    AppDeployManager.deploymentError = true;
                    AppDeployManager.processingFailed("Definition of supported typesy/default type for input "
                            + complexSpecifier.getIdentifier() + " is invalid.");
                }

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
                complexSpecifier.setTheAbstract(port.getOwsAbstract());
                complexSpecifier.setTitle(port.getOwsTitle());
                if (complexSpecifier.getTitle().equals("")) {
                    complexSpecifier.setTitle(complexSpecifier.getIdentifier());
                }

                try {
                    List<List> supportedTypes = new ArrayList<>();
                    List<String> supportedType = new ArrayList();

                    IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();

                    if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
                        DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) dataTypeDescription;
                        ComplexDataTypeFormat format = description.getFormat();
                        supportedType.add(format.getMimeType());
                        supportedType.add(format.getSchema());
                        supportedType.add(format.getEncoding());
                    }

                    supportedTypes.add(supportedType);

                    if (supportedTypes.isEmpty()) {
                        AppDeployManager.deploymentError = true;
                        AppDeployManager.processingFailed("Supported types for output "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }
                    if (supportedType.isEmpty()) {
                        AppDeployManager.deploymentError = true;
                        AppDeployManager.processingFailed("Default type for output "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }

                    complexSpecifier.setTypes(supportedTypes);
                    complexSpecifier.setDefaulttype(supportedType);
                } catch (Exception e) {
                    AppDeployManager.deploymentError = true;
                    AppDeployManager.processingFailed("Definition of supported typesy/default type for output "
                            + complexSpecifier.getIdentifier() + " is invalid.");
                }

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

    /**
     * Logs information that deployment failed.
     *
     * @param reason The reason for failing.
     */
    public static void deploymentFailed(final String reason) {
        AppEventService.getInstance().fireAppEvent(reason,
                AppConstants.INFOTAB_ID_SERVER);
    }

    /**
     * Logs information that processing of model failed.
     *
     * @param reason The reason for failing.
     */
    public static void processingFailed(final String reason) {
        AppEventService.getInstance().fireAppEvent(reason,
                AppConstants.INFOTAB_ID_EDITOR);
    }
}
