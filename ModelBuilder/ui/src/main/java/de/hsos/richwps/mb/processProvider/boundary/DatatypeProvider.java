package de.hsos.richwps.mb.processProvider.boundary;

import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.processProvider.control.FormatProvider;
import de.hsos.richwps.mb.processProvider.control.LiteralDatatypeProvider;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class DatatypeProvider {
    
    private String complexCsvFile;
    private String literalCsvFile;
    
    public void setComplexCsvFile(String complexCsvFile) {
        this.complexCsvFile = complexCsvFile;
    }

    public void setLiteralCsvFile(String literalCsvFile) {
        this.literalCsvFile = literalCsvFile;
    }

    public List<ComplexDataTypeFormat> getComplexDatatypes() throws LoadDataTypesException {
        return new FormatProvider(complexCsvFile).getDataTypes();
    }

    public List<String> getLiteralDatatypes() throws LoadDataTypesException {
        return new LiteralDatatypeProvider(literalCsvFile).getDataTypes();
    }

}
