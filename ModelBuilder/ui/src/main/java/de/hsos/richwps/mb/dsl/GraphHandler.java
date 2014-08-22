/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.dsl;

import de.hsos.richwps.mb.dsl.exceptions.IdentifierDuplicatedException;
import de.hsos.richwps.mb.dsl.exceptions.NoIdentifierException;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class GraphHandler {

    /**
     * Throws exceptions if the graph or its elements couldn't be translated.
     *
     * @param graph
     * @todo CREATE VALIDATOR CLASS AND MOVE METHOD
     * @throws NoIdentifierException
     * @throws IdentifierDuplicatedException
     */
    static void validate(Graph graph) throws NoIdentifierException, IdentifierDuplicatedException {
        validateGlobalPorts(graph, true);
        validateGlobalPorts(graph, false);
    }

    private static void validateGlobalPorts(Graph graph, boolean inputPorts) throws NoIdentifierException, IdentifierDuplicatedException {
        List<ProcessPort> ports;
        if (inputPorts) {
            ports = graph.getGlobalInputPorts();
        } else {
            ports = graph.getGlobalOutputPorts();
        }

        for (ProcessPort port : ports) {
            String identifier = port.getOwsIdentifier();
            if (null == identifier || identifier.isEmpty()) {
                throw new NoIdentifierException(inputPorts);
            }

            int count = countPortIdentifierOccurences(ports, identifier);
            if (1 < count) {
                throw new IdentifierDuplicatedException(identifier, inputPorts, count);
            }
        }
    }

    /**
     * Translates the unique identifier to the original OWS identifier.
     *
     * @param rawIdentifier
     * @return
     */
    static String getOwsIdentifier(String rawIdentifier) {
        return rawIdentifier.split(" ")[0];
    }

    /**
     * Translates the raw identifier to the unique identifier.
     *
     * @param rawIdentifier
     * @return
     */
    static String getUniqueIdentifier(String rawIdentifier) {
        return rawIdentifier.replace(' ', '_');
    }

    /**
     * Creates unique identifiers for port identifiers which occure multiple
     * times. Sets the port identifier in a raw format. Use getOwsIdentifier()
     * and getUniqueIdentifier() to translate the raw identifier.
     */
    static void createRawIdentifiers(List<ProcessPort> ports) {
        for (ProcessPort port : ports) {
            // find a unique identifier
            int add = 1;
            String tmpId = port.getOwsIdentifier();
            while (1 < countPortIdentifierOccurences(ports, port.getOwsIdentifier())) {
                port.setOwsIdentifier(tmpId + "_" + add++);
            }

            // build the raw identifier by replacing the unique identifier's
            // underscores with spaces in order to later
            // a) identify the original ows identifier
            // b) build the just found unique identifer
            if (!port.getOwsIdentifier().equals(tmpId)) {
                port.setOwsIdentifier(tmpId + " " + add);
            }
        }
    }

    /**
     * Counts the occurences of the specified identifier within the given list
     * of ports.
     *
     * @param ports
     * @param identifier
     * @return
     */
    static int countPortIdentifierOccurences(List<ProcessPort> ports, String identifier) {
        int count = 0;

        for (ProcessPort port : ports) {
            String portIdentifier = port.getOwsIdentifier();
            if (null != portIdentifier && portIdentifier.equals(identifier)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts the occurences of the specified identifier in all processes,
     * process ports and global ports.
     *
     * @param identifier
     * @return
     */
//    private int countIdentifierOccurences(String identifier) {
//        int count = 0;
//
//        for (ProcessEntity process : graph.getUsedProcesses()) {
//            if (process.getIdentifier().equals(identifier)) {
//                count++;
//            }
//            count += countPortIdentifierOccurences(process.getInputPorts(), identifier);
//            count += countPortIdentifierOccurences(process.getOutputPorts(), identifier);
//        }
//
//        count += countPortIdentifierOccurences(graph.getUsedGlobalPorts(), identifier);
//
//        return count;
//    }
}
