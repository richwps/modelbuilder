package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.semanticProxy.exception.LoadDataTypesException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a list of available complex data formats. Currently, the list's
 * items are received from a CSV file.
 *
 * @author dziegenh
 */
public class FormatProvider {

    private static FormatProvider instance;

    private String csvFile = "formats.csv";

    private ComplexDataTypeFormat defaultType;
    private List<ComplexDataTypeFormat> dataTypes;

    // hide
    private FormatProvider() {
    }

    public static FormatProvider getInstance() {
        if (null == instance) {
            instance = new FormatProvider();
        }

        return instance;
    }

    public List<ComplexDataTypeFormat> getComplexDataTypes() throws LoadDataTypesException {

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
            String valueSeperator = ",";
            while (null != (line = reader.readLine())) {
                String[] values = line.split(valueSeperator);
                String mimeType = values[0];
                String schema = (values.length > 1) ? values[1] : "";
                String encoding = (values.length > 2) ? values[2] : "";

                ComplexDataTypeFormat tmp = new ComplexDataTypeFormat(mimeType, schema, encoding);
                dataTypes.add(tmp);
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

    // TODO set a REAL default type
    public ComplexDataTypeFormat getDefaultDataType() {
        if (null != dataTypes && !dataTypes.isEmpty()) {
            return dataTypes.get(0);
        }

        return null;
    }

}
