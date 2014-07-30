/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.wps.SPClient;
import de.hsos.richwps.sp.client.wps.Vocabulary;
import de.hsos.richwps.sp.client.wps.gettypes.InAndOutputForm;
import de.hsos.richwps.sp.client.wps.gettypes.Input;
import de.hsos.richwps.sp.client.wps.gettypes.Network;
import de.hsos.richwps.sp.client.wps.gettypes.Output;
import de.hsos.richwps.sp.client.wps.gettypes.WPS;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author dziegenh
 */
public class ProcessProvider implements IProcessProvider {

    private String url;
    private SPClient spClient;
    private Network net;
    private WPS[] wpss;

    public ProcessProvider() {
        spClient = SPClient.getInstance();
        this.wpss = new WPS[]{};
    }

    /**
     * Connects to the SemanticProxy using the url field.
     *
     * @param url SemanticProxy URL
     * @return
     */
    public boolean connect(String url) throws Exception {
        // init SP Client
        Vocabulary.init(new URL(url + "/resources/vocab"));
        spClient.setRootURL(url + "/resources");
        spClient.setSearchURL(url + "/search");
        spClient.setWpsListURL(url + "/resources/wpss");
        spClient.setProcessListURL(url + "/resources/processes");

        this.url = url;

        try {
            net = spClient.getNetwork();
            spClient.clearCache();
        } catch (Exception ex) {
            net = null;
            AppEventService.getInstance().fireAppEvent(AppConstants.SEMANTICPROXY_NOT_REACHABLE, this);
            return false;
        }

        return true;
    }

    public boolean isConnected() {
        return null != net;
    }

    @Override
    public ProcessEntity getProcessEntity(String server, String identifier) {
        return getProcessWithPorts(server, identifier);
    }

    private void fireSpReceiveExceptionAsAppEvent(Exception ex) {
        String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        AppEventService.getInstance().fireAppEvent(msg, this);
    }

    private ProcessEntity getProcessWithPorts(String server, String identifier) {

        ProcessEntity process = null;

        // find desired endpoint (server)
        for (WPS wps : wpss) {
            try {
                if (server.equals(wps.getEndpoint())) {

                    for (de.hsos.richwps.sp.client.wps.gettypes.Process spProcess : wps.getProcesses()) {

                        if (spProcess.getIdentifier().equals(identifier)) {
                            // Map process properties
                            process = new ProcessEntity(server, spProcess.getIdentifier());
                            process.setOwsAbstract(spProcess.getAbstract());
                            process.setTitle(spProcess.getTitle());

                            // Map input ports
                            try {
                                for (Input spInput : spProcess.getInputs()) {
                                    ProcessPort inPort = new ProcessPort(getDatatype(spInput.getInputFormChoice()));
                                    inPort.setOwsIdentifier(spInput.getIdentifier());
                                    inPort.setOwsAbstract(spInput.getAbstract());
                                    inPort.setOwsTitle(spInput.getTitle());
                                    process.addInputPort(inPort);
                                }
                            } catch (Exception ex) {
                                fireSpReceiveExceptionAsAppEvent(ex);
                            }

                            // Map output ports
                            try {
                                for (Output spOutput : spProcess.getOutputs()) {
                                    ProcessPort outPort = new ProcessPort(getDatatype(spOutput.getOutputFormChoice()));
                                    outPort.setOwsIdentifier(spOutput.getIdentifier());
                                    outPort.setOwsAbstract(spOutput.getAbstract());
                                    outPort.setOwsTitle(spOutput.getTitle());
                                    process.addOutputPort(outPort);
                                }
                            } catch (Exception ex) {
                                fireSpReceiveExceptionAsAppEvent(ex);
                            }

                            // process found, return to stop search
                            return process;
                        }
                    }

                }
            } catch (RDFException ex) {
                fireSpReceiveExceptionAsAppEvent(ex);
            }
        }
        return null;
    }

    @Override
    public Collection<ProcessEntity> getServerProcesses(String server) {
        return getServerProcesses(server, false);
    }

    public Collection<ProcessEntity> getServerProcesses(String server, boolean withPorts) {

        // find desired endpoint (server)
        for (WPS wps : wpss) {
            try {
                if (server.equals(wps.getEndpoint())) {
                    LinkedList<ProcessEntity> serverProcesses = new LinkedList<ProcessEntity>();
                    ProcessEntity process = null;

                    for (de.hsos.richwps.sp.client.wps.gettypes.Process spProcess : wps.getProcesses()) {

                        // Map process properties
                        process = new ProcessEntity(server, spProcess.getIdentifier());
                        process.setOwsAbstract(spProcess.getAbstract());
                        process.setTitle(spProcess.getTitle());

//                        if (withPorts) {
//                            // Map input ports
//                            try {
//                                for (Input spInput : spProcess.getInputs()) {
//                                    ProcessPort inPort = new ProcessPort(getDatatype(spInput.getInputFormChoice()));
//                                    inPort.setOwsIdentifier(spInput.getIdentifier());
//                                    inPort.setOwsAbstract(spInput.getAbstract());
//                                    inPort.setOwsTitle(spInput.getTitle());
//                                    process.addInputPort(inPort);
//                                }
//                            } catch (Exception ex) {
//                                fireSpReceiveExceptionAsAppEvent(ex);
//                            }
//
//                            // Map output ports
//                            try {
//                                for (Output spOutput : spProcess.getOutputs()) {
//                                    ProcessPort outPort = new ProcessPort(getDatatype(spOutput.getOutputFormChoice()));
//                                    outPort.setOwsIdentifier(spOutput.getIdentifier());
//                                    outPort.setOwsAbstract(spOutput.getAbstract());
//                                    outPort.setOwsTitle(spOutput.getTitle());
//                                    process.addOutputPort(outPort);
//                                }
//                            } catch (Exception ex) {
//                                fireSpReceiveExceptionAsAppEvent(ex);
//                            }
//                        }
                        // add process to current server's list
                        serverProcesses.add(process);
                    }

                    return serverProcesses;
                }
            } catch (RDFException ex) {
                fireSpReceiveExceptionAsAppEvent(ex);
            }
        }

        LinkedList<ProcessEntity> ps = new LinkedList<ProcessEntity>();

        ProcessEntity process;
        ProcessPort port;

        if (server.equals(mockServer1)) {

            // SelectReportingArea
            {
                process = new ProcessEntity(server, "net.disy.wps.lkn.processes.SelectReportingArea");
                process.setTitle("SelectReportingArea");
                process.setOwsAbstract(".");

                // INPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("reportingareas");
                port.setOwsTitle("reporting areas.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.LITERAL);
                port.setOwsIdentifier("area");
                port.setOwsTitle("area identifier {NF/DI}.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                // OUTPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("reportingarea");
                port.setOwsTitle("reportingarea.");
                port.setOwsAbstract("None.");
                process.addOutputPort(port);

                ps.add(process);
            }

            // MSRLD5selection
            {
                process = new ProcessEntity(server, "net.disy.wps.lkn.mpa.processes.MSRLD5selection");
                process.setTitle("MSRLD5selection");
                process.setOwsAbstract("MSRLD5selection. ");

                // INPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("msrl-d5");
                port.setOwsTitle("MSRL D5 Daten");
                port.setOwsAbstract("MSRL D5 Daten, die Algen- und Seegras- Polygone enthalten.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.LITERAL);
                port.setOwsIdentifier("bewertungsjahr");
                port.setOwsTitle("Bewertungsjahr");
                port.setOwsAbstract("Bewertungsjahr, von dem die durchzufuehrende Bewertung ausgeht.");
                process.addInputPort(port);

                // OUTPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantAlgea");
                port.setOwsTitle("XML-Rohdaten Datei");
                port.setOwsAbstract("XML-Datei mit Rohdaten der Bewertung");
                process.addOutputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantSeagras");
                port.setOwsTitle("XML-Rohdaten Datei");
                port.setOwsAbstract("XML-Datei mit Rohdaten der Bewertung");
                process.addOutputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantYears");
                port.setOwsTitle("XML-Rohdaten Datei");
                port.setOwsAbstract("XML-Datei mit Rohdaten der Bewertung");
                process.addOutputPort(port);

                ps.add(process);
            }

            // SelectTopography
            {
                process = new ProcessEntity(server, "net.disy.wps.lkn.mpa.processes.SelectTopography");
                process.setTitle("SelectTopography");
                process.setOwsAbstract(".");

                // INPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("topography");
                port.setOwsTitle("ingoing topography.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantYears");
                port.setOwsTitle("relevantYears.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                // OUTPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantTopographies");
                port.setOwsTitle(".");
                port.setOwsAbstract("None.");
                process.addOutputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantTopographyYears");
                port.setOwsTitle(".");
                port.setOwsAbstract("None.");
                process.addOutputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("existingTopographyYears");
                port.setOwsTitle(".");
                port.setOwsAbstract("None.");
                process.addOutputPort(port);

                ps.add(process);
            }

            // Characteristics
            {
                process = new ProcessEntity(server, "net.disy.wps.lkn.mpa.processes.Characteristics");
                process.setTitle("Characteristics");
                process.setOwsAbstract(".");

                // INPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantYears");
                port.setOwsTitle("relevantYears.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("existingTopographyYears");
                port.setOwsTitle("existingTopographyYears.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("intersectionTidelandsReportingAreas");
                port.setOwsTitle("intersectionTidelandsReportingAreas.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantSeagras");
                port.setOwsTitle("relevantSeagras.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantAlgea");
                port.setOwsTitle("relevantAlgea.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("reportingAreas");
                port.setOwsTitle("reportingAreas.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                // OUTPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("mpbResultGml");
                port.setOwsTitle("Bewertete Berichtsgebiete");
                port.setOwsAbstract("FeatureCollection der bewerteten Berichtsgebiete");
                process.addOutputPort(port);

                ps.add(process);
            }

            // Intersect
            {
                process = new ProcessEntity(server, "net.disy.wps.lkn.mpa.processes.Intersect");
                process.setTitle("Intersect");
                process.setOwsAbstract(".");

                // INPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("reportingAreas");
                port.setOwsTitle("ingoing reportingareas.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("topography");
                port.setOwsTitle("ingoing topography.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("relevantTopographyYears");
                port.setOwsTitle("relevantTopographyYears.");
                port.setOwsAbstract("None.");
                process.addInputPort(port);

                // OUTPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("intersections");
                port.setOwsTitle(".");
                port.setOwsAbstract("None");
                process.addOutputPort(port);

                ps.add(process);
            }

        } else if (server.equals(mockServer2)) {
            // put server 2 processes here...

            // Makrophyten
            {
                process = new ProcessEntity(server, "net.disy.wps.lkn.mpa.processes.MacrophyteAssesment");
                process.setTitle("Makrophytenbewertung");
                process.setOwsAbstract("Prozess zur Bewertung der Berichtsgebiete Nordfriesland und Dithmarschen anhand von MSRL-D5 Daten");

                // INPUTS
                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("topographie");
                port.setOwsTitle("Topographie");
                port.setOwsAbstract("Topographie");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("msrl-d5");
                port.setOwsTitle("MSRL D5 Daten");
                port.setOwsAbstract("MSRL D5 Daten, die Algen- und Seegras- Polygone enthalten.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.LITERAL);
                port.setOwsIdentifier("bewertungsjahr");
                port.setOwsTitle("Bewertungsjahr");
                port.setOwsAbstract("Bewertungsjahr, von dem die durchzufuehrende Bewertung ausgeht.");
                process.addInputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("berichtsgebiete");
                port.setOwsTitle("Berichtsgebiete");
                port.setOwsAbstract("Berichtsgebiete die die Werte 'DI' und 'NF' im Attribut 'DISTR' enthalten.");
                process.addInputPort(port);

                // OUTPUTS
                port = new ProcessPort(ProcessPortDatatype.LITERAL);
                port.setOwsIdentifier("rawValues");
                port.setOwsTitle("Rohdaten");
                port.setOwsAbstract("CSV-Tabelle mit Rohdaten der Verschneidungsergebnisse (Flaechen in Quadratkilometer).");
                process.addOutputPort(port);

                port = new ProcessPort(ProcessPortDatatype.LITERAL);
                port.setOwsIdentifier("evalValues");
                port.setOwsTitle("Bewertungsergebnisse");
                port.setOwsAbstract("CSV-Tabelle mit bewerteten Flaechenverhaeltnissen und EQR-Werten.");
                process.addOutputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("mpbResultGml");
                port.setOwsTitle("Bewertete Berichtsgebiete");
                port.setOwsAbstract("FeatureCollection der bewerteten Berichtsgebiete");
                process.addOutputPort(port);

                port = new ProcessPort(ProcessPortDatatype.COMPLEX);
                port.setOwsIdentifier("mpbResultXml");
                port.setOwsTitle("XML-Rohdaten Datei");
                port.setOwsAbstract("XML-Datei mit Rohdaten der Bewertung");
                process.addOutputPort(port);

                ps.add(process);
            }
        }

        return ps;

    }

    @Override
    public Collection<String> getAllServer() {
        LinkedList<String> l = new LinkedList<String>();

        boolean rdfError = false;

        if (null != net) {
            try {
                wpss = net.getWPSs();
            } catch (Exception ex) {
                rdfError = true;
            }

            if (!rdfError) {
                for (WPS wps : wpss) {
                    try {
                        l.add(wps.getEndpoint());
                    } catch (RDFException ex) {
                        rdfError = true;
                    }
                }
            }
        }

        if (rdfError) {
            AppEventService.getInstance().fireAppEvent(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, this);
        }

        // TODO replace String with formatable AppConstant
        AppEventService.getInstance().fireAppEvent("Received " + l.size() + " servers.", this);

        return l;
    }

    public String mockServer1 = "Server 1";
    public String mockServer2 = "Server 2";

    private ProcessPortDatatype getDatatype(InAndOutputForm inputFormChoice) throws UnsupportedWpsDatatypeException {
        switch (inputFormChoice.getDataType()) {
            case InAndOutputForm.LITERAL_TYPE:
                return ProcessPortDatatype.LITERAL;
            case InAndOutputForm.COMPLEX_TYPE:
                return ProcessPortDatatype.COMPLEX;
            case InAndOutputForm.BOUNDING_BOX_TYPE:
                return ProcessPortDatatype.BOUNDING_BOX;
        }

        throw new UnsupportedWpsDatatypeException(inputFormChoice.getDataType());
    }

}