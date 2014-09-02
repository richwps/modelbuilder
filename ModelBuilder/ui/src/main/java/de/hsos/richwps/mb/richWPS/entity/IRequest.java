package de.hsos.richwps.mb.richWPS.entity;

import java.util.List;

/**
 *
 * @author dalcacer
 */
public interface IRequest {
    
        public String getEndpoint();
        public String getIdentifier();
        public String getProcessversion();
        public List<IInputSpecifier> getInputs();
        public List<IOutputSpecifier> getOutputs();
        public boolean isException();
        public String getException();
        public void addException(final String message);
        
}
