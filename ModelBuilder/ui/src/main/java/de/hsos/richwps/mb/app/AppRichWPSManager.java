package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Exporter;
import de.hsos.richwps.mb.dsl.exceptions.IdentifierDuplicatedException;
import de.hsos.richwps.mb.dsl.exceptions.NoIdentifierException;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.ProcessInputPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
import de.hsos.richwps.mb.exception.GraphToRequestTransformationException;
import de.hsos.richwps.mb.richWPS.entity.impl.ProfileRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Manages script creation, processdescription creation and the deployment.
 *
 *
 * @author dziegenh
 * @author dalcacer
 * @version 0.0.4
 */
public class AppRichWPSManager {

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
     * An exporters that anaylses the given graph and transforms it to a
     * ROLA-script.
     */
    private Exporter exporter;

    /**
     * Constructs a new AppDeployManager.
     *
     * @param app the overall app.
     */
    public AppRichWPSManager(App app) {
        try {
            this.app = app;
            this.graph = app.getGraphView().getGraph();
            this.error = false;
            this.exporter = new Exporter(this.graph.clone());
        } catch (NoIdentifierException | IdentifierDuplicatedException ex) {
            java.util.logging.Logger.getLogger(AppRichWPSManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            java.util.logging.Logger.getLogger(AppRichWPSManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Previews the ROLA.
     *
     * @return rola script.
     */
    //TODO split in two prepareROLA. getROLA
    public String getROLA() {

        //load information from model.
        final GraphModel model = this.graph.getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        final String title = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_TITLE);
        final String version = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_VERSION);
        final String theabstract = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ABSTRACT);

        //verify information
        if (identifier.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ID_MISSING);
            return "";
        } else if (title.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_TITLE_MISSING);
            return "";
        } else if (version.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_VERSION_MISSING);
            return "";
        }
        //generate rola
        final String rola = this.generateROLA();
        if (null == rola) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ROLA_FAILED);
            return "";
        }
        return rola;
    }

    /**
     * Delivers the transitions.
     *
     * @return transitions
     */
    public Map<String, String> getEdges() {
        return exporter.getEdges();
    }

    /**
     * Performs the deployment.
     */
    void deploy() {

        //load information from model.
        final GraphModel model = this.graph.getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        final String title = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_TITLE);
        final String version = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_VERSION);
        final String theabstract = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ABSTRACT);

        //verify information
        if (identifier.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ID_MISSING);
            return;
        } else if (title.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_TITLE_MISSING);
            return;
        } else if (version.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_VERSION_MISSING);
            return;
        }

        String wpsendpoint = "";
        String richwpsendpoint = "";
        if (RichWPSProvider.isWPSEndpoint(auri)) {
            wpsendpoint = auri;
            richwpsendpoint = wpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT,
                    RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
        } else if (RichWPSProvider.isRichWPSEndpoint(auri)) {
            richwpsendpoint = auri;
            wpsendpoint = richwpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT,
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
        }

        if (!RichWPSProvider.checkRichWPSEndpoint(richwpsendpoint)) {
            this.error = true;
            this.deployingModelFailed(AppConstants.DEPLOY_CONNECT_FAILED + " "
                    + richwpsendpoint);
            return;
        }

        //generate rola
        final String rola = this.generateROLA();
        if (null == rola) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ROLA_FAILED);
            return;
        }

        //generate processdescription
        DeployRequest request = new DeployRequest(wpsendpoint, richwpsendpoint,
                identifier, title, version, RichWPSProvider.deploymentProfile);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.defineInputsOutputs(request);
        } catch (GraphToRequestTransformationException ex) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_DESC_FAILED);
            Logger.log(this.getClass(), "deploy()", ex.getLocalizedMessage());
            return;
        }

        //perform perform
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.perform(request);

            if (request.isException()) {
                this.error = true;
                this.deployingModelFailed(AppConstants.DEPLOY_SERVERSIDE_ERROR);
                String msg = AppConstants.DEPLOY_SERVERSIDE_ERROR + "\n"
                        + request.getException();
                JOptionPane.showMessageDialog(null, msg);
                Logger.log(this.getClass(), "deploy()", request.getException());
                return;
            }

            AppEventService service = AppEventService.getInstance();
            service.fireAppEvent(AppConstants.DEPLOY_SUCCESS, AppConstants.INFOTAB_ID_SERVER);
            JOptionPane.showMessageDialog(null,
                    AppConstants.DEPLOY_SUCCESS,
                    AppConstants.DEPLOY_SUCCESS,
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            this.error = true;
            this.deployingModelFailed("An error occured while deployment.");
            Logger.log(this.getClass(), "deploy()", ex.getLocalizedMessage());
            return;
        }
    }

    /**
     */
    public TestRequest getTestRequest() {
        //load information from model.
        final GraphModel model = this.graph.getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        final String title = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_TITLE);
        final String version = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_VERSION);
        final String theabstract = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ABSTRACT);

        //verify information
        if (identifier.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ID_MISSING);
            return null;
        } else if (title.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_TITLE_MISSING);
            return null;
        } else if (version.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_VERSION_MISSING);
            return null;
        }

        String wpsendpoint = "";
        String richwpsendpoint = "";
        if (RichWPSProvider.isWPSEndpoint(auri)) {
            wpsendpoint = auri;
            richwpsendpoint = wpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT,
                    RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
        } else if (RichWPSProvider.isRichWPSEndpoint(auri)) {
            richwpsendpoint = auri;
            wpsendpoint = richwpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT,
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
        }

        if (!RichWPSProvider.checkRichWPSEndpoint(richwpsendpoint)) {
            this.error = true;
            this.deployingModelFailed(AppConstants.DEPLOY_CONNECT_FAILED + " "
                    + richwpsendpoint);
            return null;
        }

        //generate rola
        final String rola = this.generateROLA();
        if (null == rola) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ROLA_FAILED);
            return null;
        }

        //generate processdescription
        TestRequest request = new TestRequest(wpsendpoint, richwpsendpoint,
                identifier, title, version, RichWPSProvider.deploymentProfile);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.defineInputsOutputs(request);
        } catch (GraphToRequestTransformationException ex) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_DESC_FAILED);
            Logger.log(this.getClass(), "deploy()", ex.getLocalizedMessage());
            return null;
        }

        return request;
    }

    public ProfileRequest getProfileRequest() {
        //load information from model.
        final GraphModel model = this.graph.getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        final String title = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_TITLE);
        final String version = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_VERSION);
        final String theabstract = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ABSTRACT);

        //verify information
        if (identifier.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ID_MISSING);
            return null;
        } else if (title.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_TITLE_MISSING);
            return null;
        } else if (version.isEmpty()) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_VERSION_MISSING);
            return null;
        }

        String wpsendpoint = "";
        String richwpsendpoint = "";
        if (RichWPSProvider.isWPSEndpoint(auri)) {
            wpsendpoint = auri;
            richwpsendpoint = wpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT,
                    RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
        } else if (RichWPSProvider.isRichWPSEndpoint(auri)) {
            richwpsendpoint = auri;
            wpsendpoint = richwpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT,
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
        }

        if (!RichWPSProvider.checkRichWPSEndpoint(richwpsendpoint)) {
            this.error = true;
            this.deployingModelFailed(AppConstants.DEPLOY_CONNECT_FAILED + " "
                    + richwpsendpoint);
            return null;
        }

        //generate rola
        final String rola = this.generateROLA();
        if (null == rola) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_ROLA_FAILED);
            return null;
        }

        //generate processdescription
        ProfileRequest request = new ProfileRequest(wpsendpoint, richwpsendpoint,
                identifier, title, version, RichWPSProvider.deploymentProfile);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.defineInputsOutputs(request);
        } catch (GraphToRequestTransformationException ex) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_DESC_FAILED);
            Logger.log(this.getClass(), "deploy()", ex.getLocalizedMessage());
            return null;
        }

        return request;
    }

    /**
     * Undeploys the opend model.
     */
    void undeploy() {

        //load information from model.
        final GraphModel model = this.graph.getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);

        if (RichWPSProvider.hasProcess(auri, identifier)) {
            String wpsendpoint = "";
            String richwpsendpoint = "";
            if (RichWPSProvider.isWPSEndpoint(auri)) {
                wpsendpoint = auri;
                richwpsendpoint = auri.replace(RichWPSProvider.DEFAULT_WPS_ENDPOINT, RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
            } else if (RichWPSProvider.isRichWPSEndpoint(auri)) {
                richwpsendpoint = auri;
                wpsendpoint = auri.replace(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT, RichWPSProvider.DEFAULT_WPS_ENDPOINT);
            }
            RichWPSProvider provider = new RichWPSProvider();
            try {
                UndeployRequest request = new UndeployRequest(wpsendpoint, richwpsendpoint, identifier);
                provider.perform(request);

                AppEventService service = AppEventService.getInstance();
                service.fireAppEvent(AppConstants.UNDEPLOY_SUCCESS, AppConstants.INFOTAB_ID_SERVER);

            } catch (Exception ex) {
                this.error = true;
                String msg = AppConstants.UNDEPLOY_FAILURE
                        + "An error occured while undeploying  " + identifier + " from"
                        + " " + auri + ". " + ex.getLocalizedMessage();
                AppEventService appservice = AppEventService.getInstance();
                appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
                return;
            }
        } else {
            this.error = true;
            String msg = AppConstants.UNDEPLOY_FAILURE
                    + "The requested process " + identifier + " was not found"
                    + " on " + auri;
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            return;
        }
    }

    /**
     * Generates a ROLA-script based on the current model.
     *
     * @return emptystring in case of exception, else rola-script.
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
                this.computingModelFailed(AppConstants.TMP_FILE_FAILED);
                Logger.log(this.getClass(), "generateRola()",
                        AppConstants.TMP_FILE_FAILED + " " + e.getLocalizedMessage());
                return "";
            }

            //Todo: Exporter is null if initialization failed due to missing 
            //      identifiers -> NoIdentifierException is thrown but only
            //      the general error (Unable to create...) is send to the user
            exporter.export(f.getAbsolutePath());

            String content = null;
            try (FileReader reader = new FileReader(f)) {
                char[] chars = new char[(int) f.length()];
                reader.read(chars);
                content = new String(chars);
            } catch (IOException ex) {
                this.error = true;
                this.computingModelFailed("");
                Logger.log(this.getClass(), "generateRola()", ex.getLocalizedMessage());
            }
            return content;

        } catch (Exception ex) {
            this.error = true;
            this.computingModelFailed("Unable to create underlying workflow"
                    + " description (ROLA).");
            Logger.log(this.getClass(), "generateRola()", ex.getLocalizedMessage());
        }
        return null;
    }

    private void defineInputsOutputs(TestRequest request) throws GraphToRequestTransformationException {

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputDescription specifier = this.createInputPortDescription(port);
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(specifier);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputValue specifier = null;
            specifier = this.createOutputPortDescription(port);

            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(specifier);
        }
    }

    private void defineInputsOutputs(ProfileRequest request) throws GraphToRequestTransformationException {

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputDescription specifier = this.createInputPortDescription(port);
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(specifier);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputValue specifier = null;
            specifier = this.createOutputPortDescription(port);

            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(specifier);
        }
    }

    private void defineInputsOutputs(DeployRequest request) throws GraphToRequestTransformationException {

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputDescription specifier = this.createInputPortDescription(port);
            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(specifier);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputValue specifier = null;
            specifier = this.createOutputPortDescription(port);

            if (null == specifier) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(specifier);
        }
    }

    private IInputDescription createInputPortDescription(ProcessPort port) {
        if (null == port || !port.isGlobalInput()) {
            throw new IllegalArgumentException("invalid port (null or not an input)");
        }

        IInputDescription thedescription = null;

        switch (port.getDatatype()) {
            case LITERAL:
                InputLiteralDataDescription literaldescription = new InputLiteralDataDescription();
                literaldescription.setIdentifier(port.getOwsIdentifier());
                literaldescription.setAbstract(port.getOwsAbstract());
                literaldescription.setTitle(port.getOwsTitle());
                if (literaldescription.getTitle().equals("")) {
                    literaldescription.setTitle(literaldescription.getIdentifier());
                }
                Integer maxl = (Integer) port.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MAXOCCURS);
                Integer minl = (Integer) port.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MINOCCURS);
                literaldescription.setMinOccur(minl);
                literaldescription.setMaxOccur(maxl);
                literaldescription.setType(("xs:string"));
                literaldescription.setDefaultvalue("");
                thedescription = literaldescription;
                break;

            case COMPLEX:
                InputComplexDataDescription complexdescription = new InputComplexDataDescription();
                complexdescription.setIdentifier(port.getOwsIdentifier());
                complexdescription.setAbstract(port.getOwsAbstract());
                complexdescription.setTitle(port.getOwsTitle());
                if (complexdescription.getTitle().equals("")) {
                    complexdescription.setTitle(complexdescription.getIdentifier());
                }

                Integer maxc = (Integer) port.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MAXOCCURS);
                Integer minc = (Integer) port.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MINOCCURS);
                complexdescription.setMinOccur(minc);
                complexdescription.setMaxOccur(maxc);
                Integer mb = (Integer) port.getPropertyValue(ComplexDataInput.PROPERTY_KEY_MAXMB);
                complexdescription.setMaximumMegabytes(mb);
                try {
                    List<List> supportedTypes = new ArrayList<>();
                    List<String> supportedType = new ArrayList<>();
                    IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();

                    if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
                        DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) dataTypeDescription;
                        ComplexDataTypeFormat format = description.getDefaultFormat();
                        supportedType.add(format.getMimeType());
                        /*if (format.getSchema().isEmpty()) {
                         supportedType.add(null);
                         } else {*/
                        supportedType.add(format.getSchema());
                        //}

                        /*if (format.getEncoding().isEmpty()) {
                         supportedType.add(null);
                         } else {*/
                        supportedType.add(format.getEncoding());
                        //}
                    }

                    supportedTypes.add(supportedType);

                    if (supportedTypes.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Supported types for input "
                                + complexdescription.getIdentifier() + " can not be empty.");
                    }
                    if (supportedType.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Default type for input "
                                + complexdescription.getIdentifier() + " can not be empty.");
                    }

                    complexdescription.setTypes(supportedTypes);
                    complexdescription.setDefaulttype(supportedType);
                } catch (Exception ex) {
                    this.error = true;
                    this.computingModelFailed("Definition of supported types/default type for input "
                            + complexdescription.getIdentifier() + " is invalid.");
                    Logger.log(this.getClass(), "createInputPortDescription()", ex.getLocalizedMessage());
                }

                thedescription = complexdescription;
                break;

            case BOUNDING_BOX:
                //TODO
                break;
        }
        return thedescription;
    }

    private IOutputValue createOutputPortDescription(ProcessPort port) {
        if (null == port || !port.isGlobalOutput()) {
            throw new IllegalArgumentException("invalid port (null or not an output)");
        }

        IOutputValue thedescription = null;

        switch (port.getDatatype()) {
            case LITERAL:
                OutputLiteralDataDescription literaldescription = new OutputLiteralDataDescription();
                literaldescription.setIdentifier(port.getOwsIdentifier());
                literaldescription.setAbstract(port.getOwsAbstract());
                literaldescription.setTitle(port.getOwsTitle());
                if (literaldescription.getTitle().equals("")) {
                    literaldescription.setTitle(literaldescription.getIdentifier());
                }
                literaldescription.setType(("xs:string"));
                thedescription = literaldescription;
                break;

            case COMPLEX:
                OutputComplexDataDescription complexdescription = new OutputComplexDataDescription();
                complexdescription.setIdentifier(port.getOwsIdentifier());
                complexdescription.setTheAbstract(port.getOwsAbstract());
                complexdescription.setTitle(port.getOwsTitle());
                if (complexdescription.getTitle().equals("")) {
                    complexdescription.setTitle(complexdescription.getIdentifier());
                }

                try {
                    List<List> supportedTypes = new ArrayList<>();
                    List<String> supportedType = new ArrayList();

                    IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();

                    if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
                        DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) dataTypeDescription;
                        ComplexDataTypeFormat format = description.getDefaultFormat();
                        supportedType.add(format.getMimeType());
                        supportedType.add(format.getSchema());
                        supportedType.add(format.getEncoding());
                    }

                    supportedTypes.add(supportedType);

                    if (supportedTypes.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Supported types for output "
                                + complexdescription.getIdentifier() + " can not be empty.");
                    }
                    if (supportedType.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Default type for output "
                                + complexdescription.getIdentifier() + " can not be empty.");
                    }

                    complexdescription.setTypes(supportedTypes);
                    complexdescription.setDefaulttype(supportedType);
                } catch (Exception ex) {
                    this.error = true;
                    this.computingModelFailed("Definition of supported types/default type for output "
                            + complexdescription.getIdentifier() + " is invalid.");
                    Logger.log(this.getClass(), "createOutputPortDescription()", ex.getLocalizedMessage());
                }

                thedescription = complexdescription;

                break;

            case BOUNDING_BOX:
                break;
        }

        return thedescription;
    }

    /**
     * Logs information that deployment failed.
     *
     * @param reason The reason for failing.
     */
    private void deployingModelFailed(final String reason) {
        AppEventService.getInstance().fireAppEvent(reason,
                AppConstants.INFOTAB_ID_SERVER);
    }

    /**
     * Logs information that processing of model failed.
     *
     * @param reason The reason for failing.
     */
    private void computingModelFailed(final String reason) {
        AppEventService.getInstance().fireAppEvent(reason,
                AppConstants.INFOTAB_ID_EDITOR);
    }

    public boolean isError() {
        return this.error;
    }
}
