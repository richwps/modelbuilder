package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a list of available literal datatypes.
 *
 * @author dziegenh
 */
public class LiteralDatatypeProvider {
    
    private final String csvFile;
    
    private List<String> dataTypes;
    
    public LiteralDatatypeProvider(String datatypesCsvFile) {
        this.csvFile = datatypesCsvFile;
    }
    
    public List<String> getDataTypes() throws LoadDataTypesException {
        
        if (null != dataTypes) {
            return dataTypes;
        }
        
        dataTypes = new LinkedList<>();
        LoadDataTypesException exception = null;
        BufferedReader reader = null;
        
        try {
            String path = AppConstants.RESOURCES_DIR + File.separator + "csv" + File.separator + csvFile;
            reader = new BufferedReader(new FileReader(path));
            String line = "";
            
            while (null != (line = reader.readLine())) {
                line = line.trim();
                
                if (!dataTypes.contains(line)) {
                    dataTypes.add(line);
                }
            }
            
        } catch (FileNotFoundException e) {
            exception = new LoadDataTypesException();
        } catch (IOException e) {
            exception = new LoadDataTypesException();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore, nothing to do here
                }
            }
        }
        
        if (null != exception) {
            throw exception;
        }
        
        return dataTypes;
    }
    
}
