package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Exporter;
import de.hsos.richwps.mb.dsl.exceptions.IdentifierDuplicatedException;
import de.hsos.richwps.mb.dsl.exceptions.NoIdentifierException;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.LiteralInput;
import de.hsos.richwps.mb.entity.ports.ProcessInputPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
import de.hsos.richwps.mb.exception.GraphToRequestTransformationException;
import de.hsos.richwps.mb.richWPS.boundary.RequestFactory;
import de.hsos.richwps.mb.richWPS.entity.impl.*;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;
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
 * @author dalcacer
 * @author dziegenh
 * @version 0.0.6
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
        //final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        final String title = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_TITLE);
        final String version = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_VERSION);
        //final String theabstract = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ABSTRACT);

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

        String[] endpoints = RichWPSProvider.deliverEndpoints(auri);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];

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
        DeployRequest request
                = (DeployRequest) RequestFactory.createDeployRequest(wpsendpoint, richwpsendpoint, identifier, title, version);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.convertInputsAndOutputs(request);
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

        String[] endpoints = RichWPSProvider.deliverEndpoints(auri);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];

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
        TestRequest request
                = (TestRequest) RequestFactory.createTestRequest(wpsendpoint, richwpsendpoint, identifier, title, version);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.convertInputsAndOutputs(request);
        } catch (GraphToRequestTransformationException ex) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_DESC_FAILED);
            Logger.log(this.getClass(), "getTestRequest()", ex.getLocalizedMessage());
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

        String[] endpoints = RichWPSProvider.deliverEndpoints(auri);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];

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
        //generate request and processdescription
        ProfileRequest request = (ProfileRequest) RequestFactory.createProfileRequest(wpsendpoint, richwpsendpoint,
                identifier, title, version);
        request.setAbstract(theabstract);
        request.setExecutionUnit(rola);

        try {
            this.convertInputsAndOutputs(request);
        } catch (GraphToRequestTransformationException ex) {
            this.error = true;
            this.computingModelFailed(AppConstants.DEPLOY_DESC_FAILED);
            Logger.log(this.getClass(), "getProfileRequest()", ex.getLocalizedMessage());
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

        String[] endpoints = RichWPSProvider.deliverEndpoints(auri);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];

        if (RichWPSProvider.hasProcess(wpsendpoint, identifier)) {

            RichWPSProvider provider = new RichWPSProvider();
            try {
                //(de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest)
                UndeployRequest request
                        = (UndeployRequest) RequestFactory.createUndeployRequest(wpsendpoint, richwpsendpoint, identifier);
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

    private void convertInputsAndOutputs(
            TestRequest request)
            throws GraphToRequestTransformationException {

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputDescription description = this.inputPort2InputDescription(port);
            if (null == description) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(description);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputValue description = null;
            description = this.outputPort2OutputDescription(port);

            if (null == description) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(description);
        }
    }

    private void convertInputsAndOutputs(
            ProfileRequest request)
            throws GraphToRequestTransformationException {

        // Transform global inputs to InputDescriptions
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputDescription description = this.inputPort2InputDescription(port);
            if (null == description) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(description);
        }

        // Transform global outputs to OutputValues
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputValue description = null;
            description = this.outputPort2OutputDescription(port);

            if (null == description) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(description);
        }
    }

    private void convertInputsAndOutputs(
            DeployRequest request)
            throws GraphToRequestTransformationException {

        // Transform global inputs
        for (ProcessPort port : graph.getGlobalInputPorts()) {
            IInputDescription description = this.inputPort2InputDescription(port);
            if (null == description) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addInput(description);
        }

        // Transform global outputs
        for (ProcessPort port : graph.getGlobalOutputPorts()) {
            IOutputValue description = null;
            description = this.outputPort2OutputDescription(port);

            if (null == description) {
                throw new GraphToRequestTransformationException(port);
            }
            request.addOutput(description);
        }
    }

    /**
     * Converts an inputprocessport to an InputDescription.
     *
     * @param port input process port.
     * @return InputDescription.
     */
    private IInputDescription inputPort2InputDescription(ProcessPort port) {
        if (null == port || !port.isGlobalInput()) {
            throw new IllegalArgumentException("invalid port (null or not an input)");
        }

        IInputDescription thedescription = null;
        Integer maxOcc = (Integer) port.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MAXOCCURS);
        Integer minOcc = (Integer) port.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MINOCCURS);

        switch (port.getDatatype()) {
            case LITERAL:
                InputLiteralDataDescription literaldesc = new InputLiteralDataDescription();
                literaldesc.setIdentifier(port.getOwsIdentifier());
                literaldesc.setAbstract(port.getOwsAbstract());
                literaldesc.setTitle(port.getOwsTitle());
                if (literaldesc.getTitle().equals("")) {
                    literaldesc.setTitle(literaldesc.getIdentifier());
                }
                String defaultValue = (String) port.getPropertyValue(LiteralInput.PROPERTY_KEY_DEFAULTVALUE);
                String datatype = (String) port.getPropertyValue(LiteralInput.PROPERTY_KEY_LITERALDATATYPE);
                literaldesc.setMinOccur(minOcc);
                literaldesc.setMaxOccur(maxOcc);
                literaldesc.setType(datatype);
                literaldesc.setDefaultvalue(defaultValue);
                thedescription = literaldesc;
                break;

            case COMPLEX:
                InputComplexDataDescription complexdesc = new InputComplexDataDescription();
                complexdesc.setIdentifier(port.getOwsIdentifier());
                complexdesc.setAbstract(port.getOwsAbstract());
                complexdesc.setTitle(port.getOwsTitle());
                if (complexdesc.getTitle().equals("")) {
                    complexdesc.setTitle(complexdesc.getIdentifier());
                }

                complexdesc.setMinOccur(minOcc);
                complexdesc.setMaxOccur(maxOcc);
                Integer mb = (Integer) port.getPropertyValue(ComplexDataInput.PROPERTY_KEY_MAXMB);
                if (null != mb) {
                    complexdesc.setMaximumMegabytes(mb);
                }
                try {
                    List<List> supportedTypes = new ArrayList<>();
                    List<String> defaultType = new ArrayList<>();

                    Object descValue = port.getPropertyValue(ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION);
                    DataTypeDescriptionComplex description = (DataTypeDescriptionComplex) descValue;

                    for (ComplexDataTypeFormat aformat : description.getFormats()) {
                        List<String> supportedType = new ArrayList<>();
                        supportedType.add(aformat.getMimeType());
                        supportedType.add(aformat.getSchema());
                        supportedType.add(aformat.getEncoding());
                        supportedTypes.add(supportedType);
                    }

                    ComplexDataTypeFormat defformat = description.getDefaultFormat();
                    defaultType.add(defformat.getMimeType());
                    defaultType.add(defformat.getSchema());
                    defaultType.add(defformat.getEncoding());

                    if (supportedTypes.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Supported types for input "
                                + complexdesc.getIdentifier() + " can not be empty.");
                    }
                    if (defaultType.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Default type for input "
                                + complexdesc.getIdentifier() + " can not be empty.");
                    }

                    complexdesc.setTypes(supportedTypes);
                    complexdesc.setDefaulttype(defaultType);
                } catch (Exception ex) {
                    this.error = true;
                    this.computingModelFailed("Definition of supported types/default type for input "
                            + complexdesc.getIdentifier() + " is invalid.");
                    Logger.log(this.getClass(), "createInputPortDescription()", ex.getLocalizedMessage());
                }

                thedescription = complexdesc;
                break;

            case BOUNDING_BOX:
                InputBoundingBoxDataDescription bboxdesc = new InputBoundingBoxDataDescription();
                bboxdesc.setIdentifier(port.getOwsIdentifier());
                bboxdesc.setAbstract(port.getOwsAbstract());
                bboxdesc.setTitle(port.getOwsTitle());
                if (bboxdesc.getTitle().equals("")) {
                    bboxdesc.setTitle(bboxdesc.getIdentifier());
                }
                bboxdesc.setMinOccur(minOcc);
                bboxdesc.setMaxOccur(maxOcc);
                //TODO
                /*literaldescription.setType(("xs:string"));
                 boundingboxdescriptoin.setDefaultvalue("");*/
                thedescription = bboxdesc;
                break;
        }
        return thedescription;
    }

    /**
     * Converts an outputprocessport to an IOutputValue.
     *
     * @param port outputprocessport.
     * @return OutputValue.
     */
    private IOutputValue outputPort2OutputDescription(ProcessPort port) {
        if (null == port || !port.isGlobalOutput()) {
            throw new IllegalArgumentException("invalid port (null or not an output)");
        }

        IOutputValue thedescription = null;

        switch (port.getDatatype()) {
            case LITERAL:
                OutputLiteralDataDescription literaldesc = new OutputLiteralDataDescription();
                literaldesc.setIdentifier(port.getOwsIdentifier());
                literaldesc.setAbstract(port.getOwsAbstract());
                literaldesc.setTitle(port.getOwsTitle());
                if (literaldesc.getTitle().equals("")) {
                    literaldesc.setTitle(literaldesc.getIdentifier());
                }
                literaldesc.setType(("xs:string"));
                thedescription = literaldesc;
                break;

            case COMPLEX:
                OutputComplexDataDescription complexdesc = new OutputComplexDataDescription();
                complexdesc.setIdentifier(port.getOwsIdentifier());
                complexdesc.setTheAbstract(port.getOwsAbstract());
                complexdesc.setTitle(port.getOwsTitle());
                if (complexdesc.getTitle().equals("")) {
                    complexdesc.setTitle(complexdesc.getIdentifier());
                }

                try {
                    List<List> supportedTypes = new ArrayList<>();
                    Object descValue = port.getPropertyValue(ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION);
                    DataTypeDescriptionComplex dataTypeDescription = (DataTypeDescriptionComplex) descValue;
                    List<String> defaultType = new ArrayList<>();

                    if (null != dataTypeDescription) {

                        for (ComplexDataTypeFormat aformat : dataTypeDescription.getFormats()) {
                            List<String> supportedType = new ArrayList<>();
                            supportedType.add(aformat.getMimeType());
                            supportedType.add(aformat.getSchema());
                            supportedType.add(aformat.getEncoding());
                            supportedTypes.add(supportedType);
                        }

                        ComplexDataTypeFormat defformat = dataTypeDescription.getDefaultFormat();
                        defaultType.add(defformat.getMimeType());
                        defaultType.add(defformat.getSchema());
                        defaultType.add(defformat.getEncoding());
                    }

                    if (supportedTypes.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Supported types for output "
                                + complexdesc.getIdentifier() + " can not be empty.");
                    }
                    if (defaultType.isEmpty()) {
                        this.error = true;
                        this.computingModelFailed("Default type for output "
                                + complexdesc.getIdentifier() + " can not be empty.");
                    }

                    complexdesc.setTypes(supportedTypes);
                    complexdesc.setDefaulttype(defaultType);
                } catch (Exception ex) {
                    this.error = true;
                    this.computingModelFailed("Definition of supported types/default type for output "
                            + complexdesc.getIdentifier() + " is invalid.");
                    Logger.log(this.getClass(), "createOutputPortDescription()", ex.getLocalizedMessage());
                }

                thedescription = complexdesc;

                break;

            case BOUNDING_BOX:
                OutputBoundingBoxDataDescription bboxdescription = new OutputBoundingBoxDataDescription();
                bboxdescription.setIdentifier(port.getOwsIdentifier());
                bboxdescription.setAbstract(port.getOwsAbstract());
                bboxdescription.setTitle(port.getOwsTitle());
                if (bboxdescription.getTitle().equals("")) {
                    bboxdescription.setTitle(bboxdescription.getIdentifier());
                }
                //TODO                
                //bboxdescription.setType(("xs:string"));
                thedescription = bboxdescription;
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
