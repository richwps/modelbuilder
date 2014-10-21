package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Exporter;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
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
import javax.swing.JOptionPane;

/**
 * Manages script creation, processdescription creation and the deployment.
 *
 * @author dziegenh
 * @author dalcacer
 * @version 0.0.4
 * @TODO Refactor source.
 */
public class AppDeployManager {

    /**
     * The overall app.
     */
    private App app;
    /**
     * The given graph, that shall be exported.
     */
    private Graph graph;

    /**
     * Indicates deployment error.
     */
    private boolean error = false;

    /**
     * Constructs a new AppDeployManager.
     *
     * @param app the overall app.
     */
    public AppDeployManager(App app) {
        this.app = app;
        this.graph = app.getGraphView().getGraph();
        this.error = false;
    }

    /**
     * Performs the deployment.
     */
    void deploy() {

        //load information from model.
        final GraphModel model = this.graph.getGraphModel();
        final String auri = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_IDENTIFIER);
        final String title = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_TITLE);
        final String version = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_VERSION);
        final String theabstract = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ABSTRACT);

        //verify information
        if (identifier.isEmpty()) {
            this.error = true;
            this.processingFailed(AppConstants.DEPLOY_ID_MISSING);
            return;
        } else if (title.isEmpty()) {
            this.error = true;
            this.processingFailed(AppConstants.DEPLOY_TITLE_MISSING);
            return;
        } else if (version.isEmpty()) {
            this.error = true;
            this.processingFailed(AppConstants.DEPLOY_VERSION_MISSING);
            return;
        }

        String wpsendpoint = "";
        String wpstendpoint = "";
        if (RichWPSProvider.isWPSEndpoint(auri)) {
            wpsendpoint = auri;
            wpstendpoint = wpsendpoint.replace(IRichWPSProvider.DEFAULT_WPS_ENDPOINT, IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
        } else if (RichWPSProvider.isWPSTEndpoint(auri)) {
            wpstendpoint = auri;
            wpsendpoint = wpstendpoint.replace(IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT, IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
        }

        if (!RichWPSProvider.checkWPSTEndpoint(wpstendpoint)) {
            this.error = true;
            this.deploymentFailed(AppConstants.DEPLOY_CONNECT_FAILED + " "
                    + wpstendpoint);
            return;
        }

        //generate rola
        final String rola = this.generateROLA();
        if (null == rola) {
            this.error = true;
            this.processingFailed(AppConstants.DEPLOY_ROLA_FAILED);
            return;
        }

        //generate processdescription
        DeployRequest request = new DeployRequest(wpsendpoint, wpstendpoint,
                identifier, title, version, RichWPSProvider.deploymentProfile);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.defineInOut(request);
        } catch (GraphToRequestTransformationException ex) {
            this.error = true;
            this.processingFailed(AppConstants.DEPLOY_DESC_FAILED);
            Logger.log("Debug:\n" + ex.getLocalizedMessage());
            return;
        }

        //perform request
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsendpoint, wpstendpoint);
            instance.deployProcess(request);

            if (request.isException()) {
                this.error = true;
                this.deploymentFailed(AppConstants.DEPLOY_SERVERSIDE_ERROR);
                String msg = AppConstants.DEPLOY_SERVERSIDE_ERROR + "\n" + request.getException();
                JOptionPane.showMessageDialog(null, msg);
                Logger.log("Debug:\n" + request.getException());
                return;
            }

            AppEventService service = AppEventService.getInstance();
            service.fireAppEvent("Process deployed.", AppConstants.INFOTAB_ID_SERVER);

        } catch (Exception ex) {
            this.error = true;
            this.deploymentFailed("An error occured while deployment.");
            Logger.log("Debug:\n" + ex.getLocalizedMessage());
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
                this.error = true;
                this.processingFailed(AppConstants.TMP_FILE_FAILED);
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
                this.error = true;
                this.processingFailed("");
            } finally {
                reader.close();
            }
            return content;

        } catch (Exception ex) {
            this.error = true;
            this.processingFailed("Unable to create underlying workflow"
                    + " description (ROLA).");
            Logger.log("Debug:\n" + ex.getLocalizedMessage());
        }
        return null;
    }

    private void defineInOut(DeployRequest request) throws GraphToRequestTransformationException {

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputSpecifier specifier = this.createInputPortSpecifier(port);
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(specifier);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputSpecifier specifier = null;
            specifier = this.createOutputPortSpecifier(port);

            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(specifier);
        }
    }

    public IInputSpecifier createInputPortSpecifier(ProcessPort port) {
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
                        this.error = true;
                        this.processingFailed("Supported types for input "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }
                    if (supportedType.isEmpty()) {
                        this.error = true;
                        this.processingFailed("Default type for input "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }

                    complexSpecifier.setTypes(supportedTypes);
                    complexSpecifier.setDefaulttype(supportedType);
                } catch (Exception ex) {
                    this.error = true;
                    this.processingFailed("Definition of supported types/default type for input "
                            + complexSpecifier.getIdentifier() + " is invalid.");
                    Logger.log("Debug:\n" + ex.getLocalizedMessage());
                }

                specifier = complexSpecifier;
                break;

            case BOUNDING_BOX:
                //TODO
                break;
        }
        return specifier;
    }

    public IOutputSpecifier createOutputPortSpecifier(ProcessPort port) {
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
                        this.error = true;
                        this.processingFailed("Supported types for output "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }
                    if (supportedType.isEmpty()) {
                        this.error = true;
                        this.processingFailed("Default type for output "
                                + complexSpecifier.getIdentifier() + " can not be empty.");
                    }

                    complexSpecifier.setTypes(supportedTypes);
                    complexSpecifier.setDefaulttype(supportedType);
                } catch (Exception ex) {
                    this.error = true;
                    this.processingFailed("Definition of supported types/default type for output "
                            + complexSpecifier.getIdentifier() + " is invalid.");
                    Logger.log("Debug:\n" + ex.getLocalizedMessage());
                }

                specifier = complexSpecifier;

                break;

            case BOUNDING_BOX:
                break;
        }

        return specifier;
    }

    /**
     * Logs information that deployment failed.
     *
     * @param reason The reason for failing.
     */
    private void deploymentFailed(final String reason) {
        AppEventService.getInstance().fireAppEvent(reason,
                AppConstants.INFOTAB_ID_SERVER);
    }

    /**
     * Logs information that processing of model failed.
     *
     * @param reason The reason for failing.
     */
    private void processingFailed(final String reason) {
        AppEventService.getInstance().fireAppEvent(reason,
                AppConstants.INFOTAB_ID_EDITOR);
    }

    public boolean isError() {
        return this.error;
    }
}
