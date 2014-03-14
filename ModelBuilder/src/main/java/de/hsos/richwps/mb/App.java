package de.hsos.richwps.mb;

import de.hsos.richwps.mb.appview.AppFrame;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;


public class App {
    
    private AppFrame frame;
    private ProcessProvider processProvider;


    public static void main( String[] args )
    {
        App app = new App();
    }

    public App() {
        processProvider = new ProcessProvider();
        frame = new AppFrame(this);
    }

    public IProcessProvider getProcessProvider() {
        return processProvider;
    }

}
