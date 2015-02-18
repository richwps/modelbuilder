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
    
    private final FormatProvider complexFormatsProvider;
    private final LiteralDatatypeProvider literalDatatypesProvider;

    public DatatypeProvider(String complexCsvFile, String literalCsvFile) {
        this.complexFormatsProvider = new FormatProvider(complexCsvFile);
        this.literalDatatypesProvider = new LiteralDatatypeProvider(literalCsvFile);

    }

    public List<ComplexDataTypeFormat> getComplexDatatypes() throws LoadDataTypesException {
        return this.complexFormatsProvider.getDataTypes();
    }

    public List<String> getLiteralDatatypes() throws LoadDataTypesException {
        return this.literalDatatypesProvider.getDataTypes();
    }
    
    
    

}
