/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author dziegenh
 */
public class ProcessProvider implements IProcessProvider {

    @Override
    public ProcessEntity getProcessEntity(String server, String identifier) {
        for (ProcessEntity process : getServerProcesses(server)) {
            if (process.getIdentifier().equals(identifier)) {
                return process;
            }
        }

        return null;
    }

    @Override
    public Collection<ProcessEntity> getServerProcesses(String server) {

        LinkedList<ProcessEntity> ps = new LinkedList<ProcessEntity>();

        ProcessEntity process;
        ProcessPort port;

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

        return ps;

    }

    @Override
    public Collection<String> getAllServer() {
        // TODO mocked
        LinkedList<String> l = new LinkedList<String>();
        l.add("Server 1");
        return l;
    }

}