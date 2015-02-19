package de.hsos.richwps.mb.control;

import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.entity.ports.BoundingBoxInput;
import de.hsos.richwps.mb.entity.ports.BoundingBoxOutput;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.ComplexDataOutput;
import de.hsos.richwps.mb.entity.ports.LiteralInput;
import de.hsos.richwps.mb.entity.ports.LiteralOutput;
import de.hsos.richwps.mb.entity.ports.ProcessInputPort;
import de.hsos.richwps.mb.entity.ports.ProcessOutputPort;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;

/**
 *
 * @author dziegenh
 */
public class ProcessPortFactory {

    public static ProcessInputPort createGlobalInputPort(ProcessPortDatatype datatype) throws LoadDataTypesException {
        return createInputPort(datatype, true);
    }

    public static ProcessOutputPort createGlobalOutputPort(ProcessPortDatatype datatype) {
        return createOutputPort(datatype, true);
    }

    public static ProcessInputPort createLocalInputPort(ProcessPortDatatype datatype) throws LoadDataTypesException {
        return createInputPort(datatype, false);
    }

    public static ProcessOutputPort createLocalOutputPort(ProcessPortDatatype datatype) {
        return createOutputPort(datatype, false);
    }

    private static ProcessInputPort createInputPort(ProcessPortDatatype datatype, boolean global) throws LoadDataTypesException {
        switch (datatype) {
            case LITERAL:
                return new LiteralInput(global);
            case COMPLEX:
                return new ComplexDataInput(global);
            case BOUNDING_BOX:
                return new BoundingBoxInput(global);
        }

        return null;
    }

    private static ProcessOutputPort createOutputPort(ProcessPortDatatype datatype, boolean global) {
        switch (datatype) {
            case LITERAL:
                return new LiteralOutput(global);
            case COMPLEX:
                return new ComplexDataOutput(global);
            case BOUNDING_BOX:
                return new BoundingBoxOutput(global);
        }

        return null;
    }

}
